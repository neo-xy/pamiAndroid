package pami.com.pami


import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.shiftize.calendarview.CalendarView
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment() : Fragment() {


    var shifts = mutableListOf<Shift>()

    var simpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale("swe"));

    lateinit var weekdays:LinearLayout;
    lateinit var pager:ViewPager;
    lateinit var header:TextView;
    lateinit var weekView:WeekView;
    var myAdapter:MyPAgerAdapter?=null;



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        weekdays = view.findViewById(R.id.calendar_week_days);
        pager = view.findViewById(R.id.my_pager)
        header =view.findViewById(R.id.month_display)
        weekView =view.findViewById(R.id.week_View);


        setUpWeekdaysRow();
        setUpGrid();
        return view
    }

    private fun setUpGrid() {
                val date = Date()
        val cal =Calendar.getInstance();
        cal.time = date;

        var on=object :OnCalendarClickedListener{
            override fun onCalendarClicked(year: Int, month: Int, day: Int) {

                weekdays.visibility=View.GONE
                pager.visibility=View.GONE
                header.visibility=View.GONE
                weekView.visibility =View.VISIBLE

                Log.d("pawell",year.toString()+"/"+month+"/"+ day)
            }
        };

        val myAdapter = MyPAgerAdapter(context,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),on)
        header.text = simpleDateFormat.format(date)
        pager.adapter = myAdapter;
        pager.setCurrentItem(25)




        pager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                Log.d("pawell","position "+position)

                var calendar: Calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, position-25)
                var date:Date = calendar.time;
                val nextYear = calendar.get(Calendar.YEAR)
                val nextMonth = calendar.get(Calendar.MONTH) + 1
                header.text = simpleDateFormat.format(date)
            }
            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun setUpWeekdaysRow() {
        val weekdays = listOf<String>("Mån", "Tis", "Ons", "Tor", "Fre", "Lör", "Sön")
        (0..weekdays.size - 1).forEach {
            val weekDay = TextView(context);
            weekDay.text = weekdays[it];
            weekDay.typeface= Typeface.DEFAULT_BOLD;
            weekDay.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f)
            weekDay.gravity = Gravity.CENTER;
            this.weekdays.addView(weekDay)
        }
    }

    private fun hasShiftThatDay(dayInMonth: Int): Boolean {
        var hasShift = false
//        shifts.forEach({
//            if (it.startTime.year == calendar.get(Calendar.YEAR) && it.startTime.month == calendar.get(Calendar.MONTH) + 1 && it.startTime.day == dayInMonth) {
//                hasShift = true;
//            }
//        })
        return hasShift;
    }


    companion object {
        fun getInstance(context: Context): ScheduleFragment {
            return ScheduleFragment();
        }
    }

    interface OnCalendarClickedListener {
        fun onCalendarClicked(year: Int, month: Int, day: Int)
    }


}
