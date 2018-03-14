package pami.com.pami

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Pawel on 13/03/2018.
 */
class ClockedShiftAdapter() : RecyclerView.Adapter<ClockedShiftAdapter.CustomeHolder>() {
    lateinit var list1: MutableList<ClockedShift>

    constructor(list: MutableList<ClockedShift>) : this() {
        list1 = list
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomeHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.clocked_in_entry, parent, false)
        return CustomeHolder(view)
    }

    override fun getItemCount(): Int {
        return list1.size
    }

    override fun onBindViewHolder(holder: CustomeHolder?, position: Int) {
        val firstName = list1[position].firstName
        val lastName = list1[position].lastName
        holder?.name?.text = firstName +" "+ lastName
        holder?.time?.text = Shared.df.format(list1[position].startTime.hour)+":"+Shared.df.format(list1[position].startTime.minute)
    }

    class CustomeHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.clocked_in_entry_name)
        var time = view.findViewById<TextView>(R.id.clocked_in_entry_time_tv)

    }
}