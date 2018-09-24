package pami.com.pami

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.*
import pami.com.pami.models.Shift
import java.util.*


class CustomCalendar : LinearLayout {

    private var calendar: Calendar = Calendar.getInstance()
    private var currentMonth = 0
    private var calendarTable = TableLayout(context)
    private val daysInWeek = 7
    private val weeksInMonth = 6
    private var shifts = mutableListOf<Shift>()

    constructor(context: Context?) : super(context) {
        shifts = FirebaseController.shifts
        this.orientation = LinearLayout.VERTICAL
        this.calendarTable.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setUpCalendarGrid(year: Int, month: Int, on: OnCalendarClickedListener) {
        this.calendarTable.removeAllViewsInLayout()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        this.currentMonth = calendar.get(Calendar.MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.firstDayOfWeek = Calendar.MONDAY
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            calendar.add(Calendar.DAY_OF_MONTH, 2 - 8)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 2 - calendar.get(Calendar.DAY_OF_WEEK))
        }

        (0 until weeksInMonth).forEach { _ ->
            val tableRow = TableRow(context)
            tableRow.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f)
            tableRow.background = ContextCompat.getDrawable(context, R.drawable.bg_bottom_border_gray)
            (0 until daysInWeek).forEach { _ ->

                val llCell = RelativeLayout(context)

                llCell.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f)
                val prick = TextView(context)

                val rLp = RelativeLayout.LayoutParams(30, 30)
                rLp.setMargins(91, 30, 0, 0)

                prick.layoutParams = rLp

                val cell = TextView(context)

                cell.gravity = Gravity.CENTER
                cell.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                cell.textSize = 18f

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month2 = calendar.get(Calendar.MONTH)
                val weekDay = calendar.get(Calendar.DAY_OF_WEEK)


                var dateSaved = false
                FirebaseController.unavailableShifts.forEach { udate ->

                    val c = Calendar.getInstance()
                    c.time = udate.date
                    if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == (month2) && c.get(Calendar.DATE) == day) {
                        dateSaved = true
                        return@forEach
                    }
                }

                if (dateSaved) {
                    prick.setBackgroundResource(R.drawable.bg_red_circle)
                }

                if (this.currentMonth != calendar.get(Calendar.MONTH)) {
                    cell.setBackgroundColor(ContextCompat.getColor(context, R.color.main_gray))
                    cell.setTextColor(ContextCompat.getColor(context, R.color.gray_light))
                }


                shifts.forEach { shift ->
                    if (day == shift.start.get(Calendar.DATE) && month2 == shift.start.get(Calendar.MONTH)) {
                        cell.background = context.getDrawable(R.drawable.bg_shift_that_day)
                        cell.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))

                        if(this.currentMonth  != shift.start.get(Calendar.MONTH)){
                            cell.background = context.getDrawable(R.drawable.bg_shift_that_day_of_month)
                        }
                    }
                }



                cell.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                cell.text = day.toString()
                cell.setOnClickListener {
                    on.onCalendarClicked(year, month2, day, weekDay)
                }
                llCell.addView(cell)
                llCell.addView(prick)
                tableRow.addView(llCell)
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            calendarTable.addView(tableRow)
        }
        this.addView(calendarTable)
    }
}
