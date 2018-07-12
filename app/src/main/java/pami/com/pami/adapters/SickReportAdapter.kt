package pami.com.pami.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.row_sick_report.view.*
import pami.com.pami.R
import pami.com.pami.models.SickReport
import java.text.SimpleDateFormat
import java.util.zip.Inflater

class SickReportAdapter(): RecyclerView.Adapter<SickReportAdapter.CustomHolder>() {

   lateinit var list:MutableList<SickReport>
    var df =SimpleDateFormat("MMM dd")

    constructor(list : MutableList<SickReport>):this(){
        this.list = list
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomHolder {
       val view = LayoutInflater.from(parent?.context).inflate(R.layout.row_sick_report,parent,false)
        return CustomHolder(view);
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomHolder?, position: Int) {
       holder?.from?.text = df.format(this.list[position].rangeStart)
       holder?.to?.text = df.format(this.list[position].rangeEnd)
    }


    class CustomHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var from = itemView?.findViewById<TextView>(R.id.from_date_tv)
        var to = itemView?.findViewById<TextView>(R.id.to_date_tv)
        var remove = itemView?.findViewById<TextView>(R.id.remove_ibtn)
    }
}