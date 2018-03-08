package pami.com.pami

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import java.util.*


class CalendarAdapter (val context: Context?, val initYear: Int, val initMonth: Int, val on :OnCalendarClickedListener) : PagerAdapter() {

    lateinit var cust: CustomCalendar

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val calendar = Calendar.getInstance()

        val date = Date()
        calendar.time = date
        calendar.set(Calendar.YEAR, initYear)
        calendar.set(Calendar.MONTH, initMonth )
        calendar.add(Calendar.MONTH, position - (count / 2))
        cust = CustomCalendar(context)
        cust.setUpCalendarGrid(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),this.on)

        container.addView(cust)
        return cust
    }
    override fun isViewFromObject(view: View, objectt: Any): Boolean {
        return view == objectt
    }
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
    override fun getCount(): Int {
        return 50
    }
}