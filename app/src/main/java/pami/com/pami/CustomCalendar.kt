package pami.com.pami


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Pawel on 25/02/2018.
 */

class CustomCalendar : LinearLayout, View.OnClickListener {
    override fun onClick(p0: View?) {
        Log.d("pawell", "clicked " + p0)
    }

    var calendar: Calendar = Calendar.getInstance();

    var simpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale("swe"));
    var selectedDate = Date();
    var currentMonth = 0;

    var calendarTable = TableLayout(context);

    private var initYear: Int = 0
    private var initMonth: Int = 0

    val DAYS_IN_A_WEEK = 7
    val WEEKS_IN_A_MONTH = 6
    var shifts = mutableListOf<Shift>()

    var onCalendarClickedListener: ScheduleFragment.OnCalendarClickedListener? = null
        set(value) {
        Log.d("pawell","value "+ value)
        }


    constructor(context: Context?) : super(context) {
        shifts = FirebaseController.shifts;
        this.orientation = LinearLayout.VERTICAL
        this.calendarTable.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public fun setUpCalendarGrid(year: Int, month: Int,on:ScheduleFragment.OnCalendarClickedListener) {
        this.calendarTable.removeAllViewsInLayout();

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        this.currentMonth = calendar.get(Calendar.MONTH);
        var date = calendar.time;

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.firstDayOfWeek = Calendar.MONDAY
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, 2 - 8)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 2 - calendar.get(Calendar.DAY_OF_WEEK))
        }



        (0..WEEKS_IN_A_MONTH - 1).forEach {
            val tableRow = TableRow(context);
            tableRow.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f);
            tableRow.background = resources.getDrawable(R.drawable.bg_calendar_row_bottom_border)
            (0..DAYS_IN_A_WEEK - 1).forEach {
                val cell = TextView(context);

                cell.gravity = Gravity.CENTER
                cell.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                cell.textSize = 18f;
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
                shifts.forEach() {
                    if (day == it.startTime.day && month == it.startTime.month) {
                        cell.setBackgroundColor(resources.getColor(R.color.colorPrimaryAlmostDark))
                        cell.setTextColor(Color.WHITE)
                    }
                }

                if (this.currentMonth != calendar.get(Calendar.MONTH)) {
                    cell.setTextColor(resources.getColor(R.color.main_gray))
                }

                cell.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
                cell.text = day.toString()
                cell.setOnClickListener({
                    Log.d("pawell" ,"pp" +day )
                    on.onCalendarClicked(year, month, day,weekDay)
                })
                tableRow.addView(cell)
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            calendarTable.addView(tableRow);
        }
        this.addView(calendarTable)
    }
}
