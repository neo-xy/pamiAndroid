package pami.com.pami.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pami.com.pami.R
import pami.com.pami.Shared
import pami.com.pami.models.ClockedShift


class ClockedShiftAdapter() : RecyclerView.Adapter<ClockedShiftAdapter.CustomeHolder>() {
    lateinit var list1: MutableList<ClockedShift>

    constructor(list: MutableList<ClockedShift>) : this() {
        list1 = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomeHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.clocked_in_entry, parent, false)
        return CustomeHolder(view)
    }

    override fun getItemCount(): Int {
        return list1.size
    }

    override fun onBindViewHolder(holder: CustomeHolder, position: Int) {
        val firstName = list1[position].firstName
        val lastName = list1[position].lastName
        holder.name?.text = firstName +" "+ lastName
        holder.time?.text = Shared.df.format(Shared.getHour(list1[position].timeStempIn))+":"+ Shared.df.format(Shared.getMinute(list1[position].timeStempIn))
    }

    class CustomeHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.clocked_in_entry_name)
        var time = view.findViewById<TextView>(R.id.clocked_in_entry_time_tv)

    }
}