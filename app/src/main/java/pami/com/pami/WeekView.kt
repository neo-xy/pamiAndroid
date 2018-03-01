package pami.com.pami

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by Pawel on 01/03/2018.
 */
class WeekView : LinearLayout {
    var weekDays = listOf<String>("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")


    fun setUp(year: Int, month: Int, day: Int, weekDay: Int) {
        this.orientation = HORIZONTAL

        FirebaseController.getShiftsOfaMonth(year.toString() + "" +String.format("%02d", month)).subscribe() {
            val shift = it;
            for (i in weekDays.indices){

                val dayColumn = LinearLayout(context);
                dayColumn.orientation = VERTICAL;
                dayColumn.setPadding(10, 5, 10, 5);
                var dayDate = day-(weekDay-2)+i
                if(weekDay==0){
                   dayDate =  day-(7-2)+i
                }
                val header = TextView(context);
                header.text = weekDays[i]+" "+dayDate+"/"+month;
                header.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                header.setTextColor(Color.WHITE)
                dayColumn.addView(header);
                FirebaseController.departments?.forEach {
                    val department = it;
                    val departmentHeader = TextView(context);
                    departmentHeader.text = it.id;
                    departmentHeader.setBackgroundColor(resources.getColor(R.color.main_gray))
                    dayColumn.addView(departmentHeader)

                    shift.forEach {
                        val shift=it;
                        if(it.department.id==department.id&&it.startTime.month==month&&it.startTime.day==dayDate){
                                var t = TextView(context)
                            FirebaseController.employees!!.forEach{
                                if(it.employeeId==shift.employeeId){
                                    t.text=it.firstName+" "+it.lastName
                                    return@forEach
                                }
                            }
                            if(t.text==null){
                                t.text="undefined"
                            }
                            dayColumn.addView(t)
                        }
                    }
                }
                this.addView(dayColumn)
            }
        }
    }


    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}