package pami.com.pami

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*


class MonthFragment : Fragment() {
    lateinit var container: RelativeLayout;

    var simpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale("swe"));

    lateinit var weekdays: LinearLayout;
    lateinit var pager: ViewPager;
    lateinit var header: TextView;
    lateinit var weekViewContainer: HorizontalScrollView;
    lateinit var weekView: WeekView;
    lateinit var oncalendarClickedListener:OnCalendarClickedListener;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_month, container, false)
        this.container = view.rootView as RelativeLayout

       //TODO must be a better way to get instance of OnCalenderClickedLIstener than trough the budle/arguments
//        = arguments!!.getSerializable("dd") as OnCalendarClickedListener
        this.oncalendarClickedListener =activity!!.supportFragmentManager.findFragmentByTag("schedule") as OnCalendarClickedListener
        if(oncalendarClickedListener!=null){
            Log.d("pawell","not null")
        }else{
            Log.d("pawell","null")
        }

        weekdays = view.findViewById(R.id.calendar_week_days);
        pager = view.findViewById(R.id.my_pager)
        header = view.findViewById(R.id.month_display)
        weekView = view.findViewById(R.id.week_view);
        weekViewContainer = view.findViewById(R.id.week_view_container)

        setUpWeekdaysRow();
setUpGrid()

        return view;
    }

    public fun setUpGrid() {
        val date = Date()
        val cal = Calendar.getInstance();
        cal.time = date;

        val myAdapter = CalendarAdapter(context, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), oncalendarClickedListener)
        header.text = simpleDateFormat.format(date)
        pager.adapter = myAdapter;
        pager.setCurrentItem(25)




        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

                val calendar: Calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, position - 25)
                val date: Date = calendar.time;
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
            weekDay.typeface = Typeface.DEFAULT_BOLD;
            weekDay.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f)
            weekDay.gravity = Gravity.CENTER;
            this.weekdays.addView(weekDay)
        }
    }



}
