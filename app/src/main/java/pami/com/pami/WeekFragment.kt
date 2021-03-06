package pami.com.pami


import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import android.widget.LinearLayout
import pami.com.pami.models.*


@RequiresApi(Build.VERSION_CODES.M)
class WeekFragment : Fragment(), View.OnScrollChangeListener {
    override fun onScrollChange(p0: View?, scrollX: Int, scrolY: Int, oldScrollX: Int, oldScrollY: Int) {
        this.followingViews.forEach {
            it.x = (scrollX).toFloat() + this.center - 150
        }
    }

    lateinit var container: LinearLayout
    var center = 0

    var dayInfoMesages = mutableListOf<DayInfoMessage>()

    var followingViews = mutableListOf<TextView>()

    var weekDays = listOf("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.dayInfoMesages =FirebaseController.dayInfoMesages


        val view = inflater.inflate(R.layout.fragment_week, container, false)
        this.container = LinearLayout(context)
        val horizontalScrollView = view.rootView as HorizontalScrollView
        horizontalScrollView.setOnScrollChangeListener(this)
        val scroll = view.findViewById<ScrollView>(R.id.scrollCont)

        Log.d("pawell","week fragment 1")
        scroll.addView(this.container)

        val year = arguments!!.getInt("year")
        val month = arguments!!.getInt("month")
        val day = arguments!!.getInt("day")
        val weekDay = arguments!!.getInt("weekDay")
        Log.d("pawell","week fragment 2")
        setUp(year, month, day, weekDay)
        return view
    }


    fun setUp(year: Int, month: Int, day1: Int, weekDay: Int) {
        this.container.orientation = LinearLayout.VERTICAL
        FirebaseController.getScheduledShifts().subscribe() {
            //need to be clear in case of new shift being add so that double week view does not come up
            this.container.removeAllViews()
            //todo if context is not chacked it trows null exception on context when shifts is added while in the view(implement better)
            if (context != null) {
                val shifts = it
                val headerLayout = LinearLayout(context)
                headerLayout.orientation = LinearLayout.HORIZONTAL
                for (i in weekDays.indices) {
                    var month2 = month
                    val dayColumn = LinearLayout(context)
                    dayColumn.orientation = LinearLayout.HORIZONTAL
                    val ll = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    ll.setMargins(1, 0, 0, 0)
                    dayColumn.layoutParams = ll

                    dayColumn.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                    dayColumn.gravity = Gravity.CENTER
                    dayColumn.setPadding(20, 15, 20, 15)

                    var dayDate = day1 - (weekDay - 2) + i
                    if (weekDay == 0) {
                        dayDate = day1 - (7 - 2) + i
                    }
                    val cal = Calendar.getInstance()
                    if (dayDate < 1) {

                        cal.set(year, month2, 1)
                        cal.add(Calendar.DAY_OF_MONTH, (dayDate - 1))
                        dayDate = cal.get(Calendar.DAY_OF_MONTH)
                        month2 = cal.get(Calendar.MONTH)
                    }

                    val day = TextView(context)
                    val blockDayBtn = TextView(context)
                    blockDayBtn.layoutParams = LinearLayout.LayoutParams(90, 90)
                    blockDayBtn.gravity = Gravity.END


                    var dateSaved =false;

                    FirebaseController.unavailableShifts.forEach {
                        val c =Calendar.getInstance()
                        c.time= it.date
                        if(c.get(Calendar.YEAR)==year&&c.get(Calendar.MONTH)==(month)&&c.get(Calendar.DATE)==dayDate){
                            dateSaved = true
                            return@forEach
                        }
                    }

                    if (dateSaved) {
                        blockDayBtn.setBackgroundResource(R.drawable.bg_green_red_circle)
                    } else {
                        blockDayBtn.setBackgroundResource(R.drawable.bg_green_grey_circle)
                    }

                    blockDayBtn.setOnClickListener { this.blockDay(year, month2, dayDate, blockDayBtn) }

                    day.text = weekDays[i] + " " + dayDate + "/" + (month2+1)
                    day.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                    day.setTextColor(Color.WHITE)
                    dayColumn.addView(day)
                    dayColumn.addView(blockDayBtn)
                    headerLayout.addView(dayColumn)
                }
                this.container.addView(headerLayout)

                FirebaseController.departments!!.forEach {
                    val department = it

                    val depheader = LinearLayout(context)
                    depheader.setBackgroundColor(Color.DKGRAY)
                    val dep: TextView

                    dep = TextView(context)
                    this.center = resources.displayMetrics.widthPixels / 2
                    dep.x = this.center.toFloat() - 150
                    this.followingViews.add(dep)
                    dep.text = department.id
                    dep.setTextColor(Color.WHITE)
                    depheader.addView(dep)
                    this.container.addView(depheader)
                    val horiz = LinearLayout(context)

                    horiz.orientation = LinearLayout.HORIZONTAL
                    for (i in weekDays.indices) {
                        var dayDate = day1 - (weekDay - 2) + i
                        if (weekDay == 0) {
                            dayDate = day1 - (7 - 2) + i
                        }
                        val shiftColumn = LinearLayout(context)
                        shiftColumn.setBackgroundResource(R.drawable.bg_left_border)
                        shiftColumn.layoutParams = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT)
                        shiftColumn.orientation = LinearLayout.VERTICAL

                        val emptyView = TextView(context)
                        emptyView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                        emptyView.setBackgroundColor(Color.WHITE)

                        shiftColumn.addView(emptyView)

                        shifts.forEach {shift->
                            if (shift.start.get(Calendar.DATE) == dayDate && shift.department.id == department.id) {
                                shiftColumn.addView(getCell(shift, dayDate, department))
                            }
                        }
                        horiz.addView(shiftColumn)
                    }
                    this.container.addView(horiz)
                }
            }
        }
    }

    private fun getCell(shift: Shift, dayDate: Int, department: Department): View? {

        val shiftCard2 = CardView(context!!)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER_VERTICAL

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(25,10,25,10)
        shiftCard2.layoutParams = params;
        shiftCard2.cardElevation = 9.0f

        shiftCard2.setContentPadding(15,15,15,15)

        var name = "undefined"
        var lastName = "undefined"
        var employeeId=""
        FirebaseController.employees.forEach {
            if (it.employeeId == shift.employeeId) {
                name = it.firstName
                lastName = it.lastName
                employeeId = it.employeeId
            }
        }
        val timeView = TextView(context)
        val nameView = TextView(context)
        timeView.gravity = Gravity.CENTER
        nameView.gravity = Gravity.CENTER

        timeView.text = "" + String.format("%02d", shift.start.get(Calendar.HOUR)) + ":" + String.format("%02d", shift.start.get(Calendar.MINUTE)) +
                "-" + String.format("%02d", shift.end.get(Calendar.HOUR)) + ":" + String.format("%02d", shift.end.get(Calendar.MINUTE))

        nameView.text = "$name $lastName"
        if(User.employeeId == employeeId){
            shiftCard2.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
            nameView.setTextColor(Color.WHITE)
            timeView.setTextColor(Color.WHITE)
        }

        linearLayout.addView(timeView)
        linearLayout.addView(nameView)
        shiftCard2.addView(linearLayout)
        return shiftCard2
    }

    private fun blockDay(year: Int, month: Int, date: Int, blockDayBtn: TextView) {

        val dateKey = year.toString() + String.format("%02d", month)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DATE,date)

        val d =cal.time


        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
        vibrator.vibrate(200)

        val alerDialog = AlertDialog.Builder(context)
        alerDialog.setView(R.layout.availability_dialog)
        alerDialog.setTitle("Ändra tillgänglighet för: " + String.format("%02d", date) + "/" + String.format("%02d", month+1))
        val dialog = alerDialog.create()

        dialog.show()

        var msg = dialog.findViewById<EditText>(R.id.not_availableMsg)

        val availablRadioBtn = dialog.findViewById<RadioButton>(R.id.available_btn)
        val notAvailablRadioBtn = dialog.findViewById<RadioButton>(R.id.not_available_btn)

        availablRadioBtn.setOnClickListener { view->
            if((view as RadioButton).isChecked) msg.visibility = View.GONE
        }
        notAvailablRadioBtn.setOnClickListener { view->
            if((view as RadioButton).isChecked) msg.visibility = View.VISIBLE
        }




       var dateSaved =false
        var unavailableDate = UnavailableDate()
        unavailableDate.date = d
        unavailableDate.employeeId = User.employeeId
        unavailableDate.markDate = Date()
        FirebaseController.unavailableShifts.forEach {
            val c =Calendar.getInstance()
            c.time = it.date
            if(c.get(Calendar.YEAR)==year&&c.get(Calendar.MONTH)==(month)&&c.get(Calendar.DATE)==date){
                dateSaved = true
                unavailableDate = it
                return@forEach
            }
        }

        if (!dateSaved) {
            availablRadioBtn.isChecked = true

            msg.visibility = View.GONE
        } else {
            notAvailablRadioBtn.isChecked = true
            msg.visibility = View.VISIBLE
        }



        dialog.findViewById<Button>(R.id.save_availability_btn).setOnClickListener {

            if (availablRadioBtn.isChecked&&dateSaved) {

                FirebaseController.removeUnavailableDate(unavailableDate)
                blockDayBtn.setBackgroundResource(R.drawable.bg_green_grey_circle)

            } else if (!availablRadioBtn.isChecked && !dateSaved) {

                unavailableDate.message  = msg.editableText.toString()

                FirebaseController.updateUnavailableDates(unavailableDate)
                blockDayBtn.setBackgroundResource(R.drawable.bg_green_red_circle)
            }
            dialog.dismiss()
        }

    }
}

