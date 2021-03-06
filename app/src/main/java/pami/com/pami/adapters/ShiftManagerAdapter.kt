package pami.com.pami.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pami.com.pami.ClockedInEntre
import pami.com.pami.R
import pami.com.pami.RecyclerViewClickListener
import pami.com.pami.Shared
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ShiftManagerAdapter(employeesClockedInEntres: MutableList<ClockedInEntre>, val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<ShiftManagerAdapter.MyHolder>() {
    var emp = employeesClockedInEntres
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manager_employee_row, parent, false)
        this.context = parent.context
        val myHolder = MyHolder(view)
        view.setOnClickListener{
            recyclerViewClickListener.listItemClicked(it,myHolder.layoutPosition)
        }
        return myHolder
    }

    override fun getItemCount(): Int {

        return emp.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.name.text = emp[position].name

        if (emp[position].startDate!=null){

            Log.d("pawell","shiftMenager adapter iffff")
            val sdf = SimpleDateFormat("HH:mm", Locale("sv"))

            holder.clockedInTime.text =sdf.format(emp[position].startDate)
            holder.clockedInTime.setTextColor(Color.WHITE)
            holder.name.setTextColor(Color.WHITE)
            holder.marker.setBackgroundResource(R.drawable.ic_done)
            holder.marker.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            holder.parent.setBackgroundResource(R.drawable.bg_border_bottom_with_accent_bg)

        }else{
            Log.d("pawell","else")
            holder.clockedInTime.text = "     "
            holder.marker.setBackgroundResource(R.drawable.ic_close)
            holder.marker.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

            holder.parent.setBackgroundResource(R.drawable.bg_bottom_border_gray)
            holder.clockedInTime.setTextColor(Color.GRAY)
            holder.name.setTextColor(Color.GRAY)

        }

    }


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.employee_name)
        var clockedInTime = itemView.findViewById<TextView>(R.id.clocked_in_time)
        var marker = itemView.findViewById<TextView>(R.id.marker)
        var parent = itemView

    }
}