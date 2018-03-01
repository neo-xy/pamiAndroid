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


    constructor(context: Context?) : super(context) {
//        setUp()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        setUp()
    }

    fun setUp(year: Int, month: Int, day: Int, weekDay: Int) {
        this.orientation = HORIZONTAL

        Log.d("pawell","mm"+String.format("%02d", month) +" "+ day)

        FirebaseController.getShiftsOfaMonth(year.toString() + "" +String.format("%02d", month)).subscribe() {
            Log.d("pawell", "11111111111")
            val shift = it;
            for (i in weekDays.indices){

                val dayColumn = LinearLayout(context);
                dayColumn.orientation = VERTICAL;
                dayColumn.setPadding(10, 5, 10, 5);
                var dayDate = day-(weekDay-2)+i
                if(weekDay==0){
                   dayDate =  day-(7-2)+i
                }
                Log.d("pawell", "s2222 "+shift.size )
                val header = TextView(context);
                header.text = weekDays[i]+" "+dayDate+"/"+month;
                header.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                header.setTextColor(Color.WHITE)
                dayColumn.addView(header);
                FirebaseController.departments?.forEach {
                    val department = it;
                    Log.d("pawell", "sdnflsdjflknsdf")
                    val departmentHeader = TextView(context);
                    departmentHeader.text = it.id;
                    departmentHeader.setBackgroundColor(resources.getColor(R.color.main_gray))
                    dayColumn.addView(departmentHeader)

                    shift.forEach {
                        Log.d("pawell","shiffft" + it.startTime.day.toString()+" "+it.startTime.day+" j "+month)
                        if(it.department.id==department.id&&it.startTime.month==month&&it.startTime.day==dayDate){
                                var t = TextView(context)
                            t.text = it.employeeId
                            dayColumn.addView(t)

                        }
                    }

                }
                this.addView(dayColumn)
            }
        }


    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


}