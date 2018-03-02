package pami.com.pami


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 */
class WeekFragment : Fragment() {
    lateinit var container:LinearLayout;

    var weekDays = listOf<String>("Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_week, container, false)
        this.container = LinearLayout(context)
        val horizontalScrollView = view.rootView as HorizontalScrollView;
        horizontalScrollView.addView(this.container)

        val year = arguments!!.getInt("year")
        val month = arguments!!.getInt("month")
        val day  = arguments!!.getInt("day")
        val weekDay = arguments!!.getInt("weekDay")

        setUp(year,month,day,weekDay)
        return view;
    }


    fun setUp(year: Int, month: Int, day: Int, weekDay: Int) {


        FirebaseController.getShiftsOfaMonth(year.toString() + "" +String.format("%02d", month)).subscribe() {
            val shift = it;
            for (i in weekDays.indices){

                val dayColumn = LinearLayout(context);
                dayColumn.orientation = LinearLayout.VERTICAL;
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
                this.container.addView(dayColumn)
            }
        }
    }

}
