package pami.com.pami


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 */
class WeekFragment : Fragment(), View.OnScrollChangeListener {
    override fun onScrollChange(p0: View?, scrollX: Int, scrolY: Int, oldScrollX: Int, oldScrollY: Int) {
        Log.d("pawell", " scrollx " + scrollX)
        Log.d("pawell", " oldscrollx " + oldScrollX)
        this.followingViews.forEach {
            it.x = (scrollX).toFloat()+this.center-150
        }

    }


    lateinit var container: LinearLayout;
    var center=0;

    var followingViews = mutableListOf<TextView>();


    var weekDays = listOf<String>("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_week, container, false)
        this.container = LinearLayout(context)
        val horizontalScrollView = view.rootView as HorizontalScrollView;
        horizontalScrollView.setOnScrollChangeListener(this)
        var scroll = view.findViewById<ScrollView>(R.id.scrollCont);

        scroll.addView(this.container)
//        horizontalScrollView.addView(this.container)

        val year = arguments!!.getInt("year")
        val month = arguments!!.getInt("month")
        val day = arguments!!.getInt("day")
        val weekDay = arguments!!.getInt("weekDay")

//        setUp(year, month, day, weekDay)
        setUp2(year, month, day, weekDay)
        return view;
    }



    fun setUp2(year: Int, month: Int, day: Int, weekDay: Int) {
        this.container.orientation = LinearLayout.VERTICAL

        FirebaseController.getShiftsOfaMonth(year.toString() + "" + String.format("%02d", month)).subscribe() {
            var shifts = it;
            var headerLayout = LinearLayout(context)
            headerLayout.orientation = LinearLayout.HORIZONTAL
            for (i in weekDays.indices) {

                val dayColumn = LinearLayout(context);
                dayColumn.orientation = LinearLayout.HORIZONTAL;
                val ll =LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                ll.setMargins(1, 0, 0, 0)
                dayColumn.layoutParams = ll

                dayColumn.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                dayColumn.gravity = Gravity.CENTER
                dayColumn.setPadding(20, 15, 20, 15);

                var dayDate = day - (weekDay - 2) + i
                if (weekDay == 0) {
                    dayDate = day - (7 - 2) + i
                }
                val day = TextView(context);
                val blockDayBtn = TextView(context)
               blockDayBtn.layoutParams = LinearLayout.LayoutParams(90,90);
                blockDayBtn.gravity =Gravity.RIGHT

                blockDayBtn.setBackgroundResource(R.drawable.bg_round_btn)

                blockDayBtn.setOnClickListener{this.blockDay(dayDate)}

                day.text = weekDays[i] + " " + dayDate + "/" + month
                day.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f)

                day.setTextColor(Color.WHITE)
                dayColumn.addView(day);
                dayColumn.addView(blockDayBtn)
                headerLayout.addView(dayColumn)
            }
            this.container.addView(headerLayout);

            FirebaseController.departments!!.forEach {
                val department = it;

                val depheader = LinearLayout(context)
                depheader.setBackgroundColor(Color.DKGRAY)
                val dep: TextView;

                dep = TextView(context)
                this.center= resources.displayMetrics.widthPixels/2
                dep.x =this.center.toFloat()-150
                this.followingViews.add(dep)
                dep.text = department.id
                dep.setTextColor(Color.WHITE)
                depheader.addView(dep)
                this.container.addView(depheader)
                val horiz = LinearLayout(context)

                horiz.orientation = LinearLayout.HORIZONTAL
                for (i in weekDays.indices) {
                    var dayDate = day - (weekDay - 2) + i
                    if (weekDay == 0) {
                        dayDate = day - (7 - 2) + i
                    }
                    val shiftColumn = LinearLayout(context)
                    shiftColumn.setBackgroundResource(R.drawable.bg_side_border)
                    shiftColumn.layoutParams= LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT)
                    shiftColumn.orientation = LinearLayout.VERTICAL

                    val emptyView =TextView(context);
                    emptyView.layoutParams =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    emptyView.setBackgroundColor(Color.WHITE)

                    shiftColumn.addView(emptyView)

                    shifts.forEach {
                        val shift = it;
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
        shiftCard.orientation = LinearLayout.VERTICAL;
        var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(25, 0, 25, 0)
        shiftCard.layoutParams = lp
        shiftCard.setPadding(15, 15, 15, 15)
        shiftCard.gravity= Gravity.CENTER_VERTICAL

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

    private fun blockDay(dayDate: Int){
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
        vibrator.vibrate(200)

        val alerDialog =AlertDialog.Builder(context)
        alerDialog.setView(R.layout.availability_dialog)
        alerDialog.setTitle("Ändra tillgänglighet")
        val dialog = alerDialog.create();


        dialog.show()

        dialog.findViewById<Button>(R.id.save_availability_btn).setOnClickListener {
            val radioGroup =dialog.findViewById<RadioGroup>(R.id.radio_group);


            Log.d("pawell","clicked"+radioGroup.checkedRadioButtonId)
        }
    }

}

