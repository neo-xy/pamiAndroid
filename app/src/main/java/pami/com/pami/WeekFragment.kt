package pami.com.pami


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*


class WeekFragment : Fragment(), View.OnScrollChangeListener {
    override fun onScrollChange(p0: View?, scrollX: Int, scrolY: Int, oldScrollX: Int, oldScrollY: Int) {
        this.followingViews.forEach {
            it.x = (scrollX).toFloat() + this.center - 150
        }
    }

    lateinit var container: LinearLayout
    var center = 0

    var followingViews = mutableListOf<TextView>()

    var weekDays = listOf<String>("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_week, container, false)
        this.container = LinearLayout(context)
        val horizontalScrollView = view.rootView as HorizontalScrollView
        horizontalScrollView.setOnScrollChangeListener(this)
        var scroll = view.findViewById<ScrollView>(R.id.scrollCont)

        scroll.addView(this.container)

        val year = arguments!!.getInt("year")
        val month = arguments!!.getInt("month")
        val day = arguments!!.getInt("day")
        val weekDay = arguments!!.getInt("weekDay")
        setUp2(year, month, day, weekDay)
        return view;
    }


    fun setUp2(year: Int, month: Int, day1: Int, weekDay: Int) {
        this.container.orientation = LinearLayout.VERTICAL

        FirebaseController.getShiftsOfaMonth(year.toString() + "" + String.format("%02d", month)).subscribe() {
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
                dayColumn.setPadding(20, 15, 20, 15);

                var dayDate = day1 - (weekDay - 2) + i
                if (weekDay == 0) {
                    dayDate = day1 - (7 - 2) + i
                }

                if(dayDate<1){
                    val cal = Calendar.getInstance()
                    cal.set(year,month2-1,1)
                    cal.add(Calendar.DAY_OF_MONTH,(dayDate-1))
                    dayDate = cal.get(Calendar.DAY_OF_MONTH)
                    month2 = cal.get(Calendar.MONTH)+1
                }

                val dateKey = (year.toString() + String.format("%02d", month2) + String.format("%02d", dayDate)).toInt()
                val day = TextView(context)
                val blockDayBtn = TextView(context)
                blockDayBtn.layoutParams = LinearLayout.LayoutParams(90, 90)
                blockDayBtn.gravity = Gravity.RIGHT


                if (User.datesUnavailable.contains(dateKey)) {
                    blockDayBtn.setBackgroundResource(R.drawable.bg_green_red_circle)
                } else {
                    blockDayBtn.setBackgroundResource(R.drawable.bg_green_grey_circle)
                }

                blockDayBtn.setOnClickListener { this.blockDay(year, month, dayDate, blockDayBtn) }

                day.text = weekDays[i] + " " + dayDate + "/" + month2
                day.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                day.setTextColor(Color.WHITE)
                dayColumn.addView(day);
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

                    shifts.forEach {
                        val shift = it
                        if (shift.startTime.day == dayDate && shift.department.id == department.id) {
                            shiftColumn.addView(getCell(shift, dayDate, department))
                        }
                    }
                    horiz.addView(shiftColumn)
                }
                this.container.addView(horiz)
            }
        }
    }

    private fun getCell(shift: Shift, dayDate: Int, department: Department): View? {
        var shiftCard = LinearLayout(context)
        shiftCard.orientation = LinearLayout.VERTICAL
        var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(25, 0, 25, 0)
        shiftCard.layoutParams = lp
        shiftCard.setPadding(15, 15, 15, 15)
        shiftCard.gravity = Gravity.CENTER_VERTICAL

        shiftCard.setBackgroundResource(R.drawable.shadow_3)

        var cell = TextView(context)
        var name = "undefined"
        var lastName = "undefined"
        FirebaseController.employees!!.forEach {
            if (it.employeeId == shift.employeeId) {
                name = it.firstName
                lastName = it.lastName
            }
        }
        var timeView = TextView(context)
        var nameView = TextView(context)
        timeView.gravity = Gravity.CENTER
        nameView.gravity = Gravity.CENTER

        timeView.text = "" + String.format("%02d", shift.startTime.hour) + ":" + String.format("%02d", shift.startTime.minute) +
                "-" + String.format("%02d", shift.endTime.hour) + ":" + String.format("%02d", shift.endTime.minute)

        nameView.text = "$name $lastName"

        shiftCard.addView(timeView)
        shiftCard.addView(nameView)
        return shiftCard
    }

    private fun blockDay(year: Int, month: Int, date: Int, blockDayBtn: TextView) {

        val dateKey = (year.toString() + String.format("%02d", month) + String.format("%02d", date)).toInt()

        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
        vibrator.vibrate(200)

        val alerDialog = AlertDialog.Builder(context)
        alerDialog.setView(R.layout.availability_dialog)
        alerDialog.setTitle("Ändra tillgänglighet för: "+String.format("%02d", date)+"/"+String.format("%02d", month))
        val dialog = alerDialog.create()

        dialog.show()
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radio_group)
        val availablRadioBtn = dialog.findViewById<RadioButton>(R.id.available_btn)
        val notAvailablRadioBtn = dialog.findViewById<RadioButton>(R.id.not_available_btn)
        if (!User.datesUnavailable.contains(dateKey)) {
            availablRadioBtn.isChecked = true
        } else {
            notAvailablRadioBtn.isChecked = true
        }
        dialog.findViewById<Button>(R.id.save_availability_btn).setOnClickListener {

            if (availablRadioBtn.isChecked) {
                User.datesUnavailable.remove(dateKey)
                blockDayBtn.setBackgroundResource(R.drawable.bg_green_grey_circle)

            } else if (!availablRadioBtn.isChecked && !User.datesUnavailable.contains(dateKey)) {
                User.datesUnavailable.add(dateKey)
                blockDayBtn.setBackgroundResource(R.drawable.bg_green_red_circle)
            }

            FirebaseController.updateUnavailableDates(User.datesUnavailable)
            dialog.dismiss()

        }
    }

}

