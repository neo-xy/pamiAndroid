package pami.com.pami

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class ShiftsAdapter() : RecyclerView.Adapter<ShiftsAdapter.MyViewHolder>(){
    var shifts:MutableList<Shift> = mutableListOf()
    var df: DecimalFormat = DecimalFormat("00")
    constructor(shifts: MutableList<Shift>) : this() {
        this.shifts =shifts
    }
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {

        holder?.startTime?.text = df.format(shifts[position].startTime.hour) + ":"+ df.format(shifts[position].startTime.minute)
        holder?.endTime?.text ="-"+ df.format(shifts[position].endTime.hour) + ":"+ df.format(shifts[position].endTime.minute)
        holder?.dayNr?.text = df.format(shifts[position].startTime.day)

        val cal =Calendar.getInstance()
        cal.set(shifts[position].startTime.year,shifts[position].startTime.month-1,shifts[position].startTime.day)
        val date:Date = cal.time
        val df = SimpleDateFormat("MMMM", Locale("swe"))
        holder?.month?.text = df.format(date).capitalize()

        holder?.extraInfo?.text = shifts[position].message
        holder?.department?.text = shifts[position].department.id

        if(shifts[position].badge.length>0){
            holder?.badge?.text =" (" +shifts[position].badge+")"
        }
        if(shifts[position].message.length<1){
            holder?.extraContainer?.visibility = View.GONE
        }
        holder?.departmentColor?.setBackgroundColor(Color.parseColor(shifts[position].department.color))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder? {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.card_shift,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shifts.size
    }

    class MyViewHolder(item:View):RecyclerView.ViewHolder(item){
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


    }
}
