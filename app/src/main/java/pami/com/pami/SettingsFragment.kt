package pami.com.pami


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.shiftize.calendarview.Agenda
import com.shiftize.calendarview.CalendarView
import com.shiftize.calendarview.DayView
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false);

//        var pager = view.findViewById<ViewPager>(R.id.my_page)
//        var date = Date()
//        var cal =Calendar.getInstance();
//        cal.time = date;
//        var myAdapter = MyPAgerAdapter(context,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH))
//        pager.adapter = myAdapter;
//        pager.setCurrentItem(25)

        return view
    }

    private fun setUp() {




    }


}
