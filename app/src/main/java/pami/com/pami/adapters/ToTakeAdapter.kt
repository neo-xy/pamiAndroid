package pami.com.pami.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import pami.com.pami.R
import pami.com.pami.RecyclerViewClickListener
import pami.com.pami.models.ShiftsToTake
import java.text.SimpleDateFormat

class ToTakeAdapter() : RecyclerView.Adapter<ToTakeAdapter.CustomHolder>(){
    lateinit var list1: MutableList<ShiftsToTake>
    lateinit var recV: RecyclerViewClickListener

    val sdf = SimpleDateFormat("MMM dd")
    constructor(list: MutableList<ShiftsToTake>, rv: RecyclerViewClickListener) : this() {
        recV = rv
        list1 = list

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.shift_to_take_item, parent, false)
        val holder = CustomHolder(view)

        holder.intressBtn.setOnClickListener {
            recV.listItemClicked(it,holder.layoutPosition)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return list1.size
    }

    override fun onBindViewHolder(holder: CustomHolder, position: Int) {

        holder.time.text = list1[position].start+"-"+list1[position].end
        holder.departmentName.text = list1[position].departmentName
        holder.date.text = sdf.format(list1[position].date)
    }

    class CustomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val time =itemView.findViewById<TextView>(R.id.time)
        val departmentName =itemView.findViewById<TextView>(R.id.department_name)
        val date = itemView.findViewById<TextView>(R.id.date);
        val intressBtn =itemView.findViewById<Button>(R.id.send_intress_btn)
    }
}



