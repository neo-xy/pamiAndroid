package pami.com.pami.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pami.com.pami.R
import pami.com.pami.models.Colleague
import java.util.*
import kotlin.Comparator

/**
 * Created by Pawel on 10/03/2018.
 */
class ColleaguesAdapter() : RecyclerView.Adapter<ColleaguesAdapter.MyViewHolder>() {

    var colleagues = mutableListOf<Colleague>()
    lateinit var context: Context;

    constructor(list: MutableList<Colleague>, context: Context?) : this() {
        this.context = context!!
        this.colleagues = list
       this.colleagues = this.colleagues.sortedBy { it.firstName } as MutableList<Colleague>
        Collections.sort(colleagues, object : Comparator<Colleague> {
            override fun compare(p0: Colleague, p1: Colleague): Int {
                if (p0.role == "boss") {
                    return -1
                }
                return 0
            }
        })
        colleagues.forEach {

        }
    }

    fun selector(p: Colleague): String = p.firstName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.colleague_info_card, parent, false);
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return colleagues.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val firstName = this.colleagues[position].firstName
        val lastName = this.colleagues[position].lastName
        val role = this.colleagues[position].role
        val phone = this.colleagues[position].phoneNumber
        val mail = this.colleagues[position].email

        if (role == "boss") {

            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            holder.name.setTextColor(Color.WHITE)
            holder.tel.setTextColor(Color.WHITE)
            holder.phoneTitle.setTextColor(Color.WHITE)
            holder.mailTitle.setTextColor(Color.WHITE)
            holder.tel.setLinkTextColor(Color.WHITE)
            holder.mail.setLinkTextColor(Color.WHITE)
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE)
            holder.name.setTextColor(Color.GRAY)
            holder.tel.setTextColor(Color.GRAY)
            holder.phoneTitle.setTextColor(Color.GRAY)
            holder.mailTitle.setTextColor(Color.GRAY)
            holder.tel.setLinkTextColor(Color.GRAY)
            holder.mail.setLinkTextColor(Color.GRAY)
        }

        holder.mail.text = mail
        holder.name.text = firstName + " " + lastName
        holder.tel.text = phone


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name_tv)
        val tel = itemView.findViewById<TextView>(R.id.tel_nr_tv)
        val mail = itemView.findViewById<TextView>(R.id.mail_tv)
        val card = itemView.findViewById<CardView>(R.id.card)
        val phoneTitle = itemView.findViewById<TextView>(R.id.phone_title)
        val mailTitle = itemView.findViewById<TextView>(R.id.mail_title)
    }
}