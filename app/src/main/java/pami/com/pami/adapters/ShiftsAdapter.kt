package pami.com.pami.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import pami.com.pami.FirebaseController
import pami.com.pami.R
import pami.com.pami.models.DayInfoMessage
import pami.com.pami.models.Shift
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class ShiftsAdapter() : RecyclerView.Adapter<ShiftsAdapter.MyViewHolder>() {

    var shifts: MutableList<Shift> = mutableListOf()
    var df: DecimalFormat = DecimalFormat("00")
    var dailyMessages = mutableListOf<DayInfoMessage>()
    var simpleHourMinuteFormat = SimpleDateFormat("HH:mm",Locale("swe"))

    constructor(shifts: MutableList<Shift>) : this() {
        this.dailyMessages = FirebaseController.dayInfoMesages;
        this.shifts = shifts
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.startTime?.text = this.simpleHourMinuteFormat.format(shifts[position].start.time)
        holder.endTime?.text = "-" + this.simpleHourMinuteFormat.format(shifts[position].end.time)
        holder.dayNr?.text = df.format(shifts[position].start.get(Calendar.DATE))

        val cal = Calendar.getInstance()
        cal.set(shifts[position].start.get(Calendar.YEAR), shifts[position].start.get(Calendar.MONTH), shifts[position].start.get(Calendar.DATE))
        val date: Date = cal.time
        val df = SimpleDateFormat("MMMM", Locale("swe"))

        val df2 = SimpleDateFormat("yyyy MMM dd", Locale("sv"))

        holder.month?.text = df.format(date).capitalize()
        var hasDayMsg = false

        dailyMessages.forEach { dailyMessage ->
            if (df2.format(dailyMessage.messageDate) == df2.format(date)) {
                holder.dayMessage.text = dailyMessage.message;
                holder.dayMessageTitle.visibility = View.VISIBLE
                holder.dayMessage.visibility = View.VISIBLE
                holder.msgDivider.visibility = View.VISIBLE
                if (dailyMessage.message.length > 0) {
                    hasDayMsg = true;
                }
                return@forEach
            }
        }

        if (!hasDayMsg) {
            holder.dayMessage.visibility = View.GONE
            holder.dayMessageTitle.visibility = View.GONE
            holder.msgDivider.visibility = View.GONE
        }


        holder.extraInfo?.text = shifts[position].message
        holder.department?.text = shifts[position].department.id

        if (shifts[position].badge.length > 0) {
            val t = " (" + shifts[position].badge + ")"
            holder.badge?.text = t
        }

        holder.extraInfo.visibility = View.VISIBLE
        holder.extraTitle.visibility = View.VISIBLE

        if (shifts[position].message != null) {
            if (shifts[position].message!!.isEmpty()) {
//                holder.extraContainer?.visibility = View.GONE
                holder.extraInfo.visibility = View.GONE
                holder.extraTitle.visibility = View.GONE
            }
        }

        holder.departmentColor?.setBackgroundColor(Color.parseColor(shifts[position].department.color))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shift, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val startTime = item.findViewById<TextView>(R.id.shift_start_tv)
        val endTime = item.findViewById<TextView>(R.id.shift_end_tv)
        val dayNr = item.findViewById<TextView>(R.id.day_nr_tv)
        val month = item.findViewById<TextView>(R.id.month_tv)
        val extraInfo = item.findViewById<TextView>(R.id.extra_info_tv)
        val department = item.findViewById<TextView>(R.id.department_tv)
        val badge = item.findViewById<TextView>(R.id.badge_tv)
        val departmentColor = item.findViewById<View>(R.id.department_color_v)
        val extraTitle = item.findViewById<TextView>(R.id.extra_title_tv)
        var extraContainer = item.findViewById<LinearLayout>(R.id.extra_info_container)
        var dayMessageTitle = item.findViewById<TextView>(R.id.daily_message_title)
        var dayMessage = item.findViewById<TextView>(R.id.daily_message_tv)
        var msgDivider = item.findViewById<View>(R.id.msg_divider)
    }
}
