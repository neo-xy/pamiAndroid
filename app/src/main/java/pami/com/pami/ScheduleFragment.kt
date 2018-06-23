package pami.com.pami

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class ScheduleFragment : Fragment(), OnCalendarClickedListener {

    lateinit var container: LinearLayout

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCalendarClicked(year: Int, month: Int, day: Int, weekDay: Int) {

        val bundle = Bundle()
        bundle.putInt("year", year)
        bundle.putInt("month", month)
        bundle.putInt("day", day)
        bundle.putInt("weekDay", weekDay)
        val weekFragment = WeekFragment()
        weekFragment.arguments = bundle

        Log.d("pawell","scheduel fragment")
        activity!!.supportFragmentManager.beginTransaction().replace(container.id, weekFragment).addToBackStack("month").commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        this.container = view.findViewById<LinearLayout>(R.id.calendar_switch_container)
        setUp()

        return view
    }

    private fun setUp() {
        val monthFragment = MonthFragment()
        activity!!.supportFragmentManager.beginTransaction().add(container.id, monthFragment).commit()
    }
}

interface OnCalendarClickedListener {
    fun onCalendarClicked(year: Int, month: Int, day: Int, weekDay: Int)
}