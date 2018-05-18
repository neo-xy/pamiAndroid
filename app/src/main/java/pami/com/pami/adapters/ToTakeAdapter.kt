package pami.com.pami.adapters

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import pami.com.pami.FirebaseController
import pami.com.pami.R
import pami.com.pami.RecyclerViewClickListener
import pami.com.pami.models.ShiftsToTake
import java.text.SimpleDateFormat

class ToTakeAdapter() : RecyclerView.Adapter<ToTakeAdapter.CustomHolder>(){
    lateinit var list1: MutableList<ShiftsToTake>
    lateinit var recV: RecyclerViewClickListener

    val simpleDateFormat = SimpleDateFormat("HH:mm")

    val sdf = SimpleDateFormat("MMM dd")
    constructor(list: MutableList<ShiftsToTake>, rv: RecyclerViewClickListener) : this() {
        recV = rv
        list1 = list

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.shift_to_take_item, parent, false)
        val holder = CustomHolder(view)
        holder.intressBtn.setOnCheckedChangeListener { compoundButton, b ->
            val container = compoundButton.parent as LinearLayout

            recV.listItemClicked(compoundButton,holder.layoutPosition)

            if(compoundButton.isChecked){
                container.setBackgroundColor(ContextCompat.getColor(parent!!.context,R.color.colorPrimaryDark))
                holder.date.setTextColor(Color.WHITE)
                holder.time.setTextColor(Color.WHITE)
                holder.departmentName.setTextColor(Color.WHITE)
                compoundButton.buttonTintList =ContextCompat.getColorStateList(parent.context, android.R.color.white)

            }else{
                container.setBackgroundColor(Color.TRANSPARENT)
                holder.date.setTextColor(Color.DKGRAY)
                holder.time.setTextColor(Color.DKGRAY)
                holder.departmentName.setTextColor(Color.DKGRAY)
                compoundButton.buttonTintList =ContextCompat.getColorStateList(parent!!.context, R.color.colorPrimaryDark)
            }
        }

//        holder.intressBtn.setOnClickListener {
//            recV.listItemClicked(it,holder.layoutPosition)
//            var t = it as CheckBox;
//            Log.d("pawell", "isChecked "+ t.isChecked)
//        }
        return holder
    }

    override fun getItemCount(): Int {
        return list1.size
    }

    override fun onBindViewHolder(holder: CustomHolder, position: Int) {

        holder.time.text = simpleDateFormat.format(list1[position].startDate)+"-"+simpleDateFormat.format(list1[position].startDate)
        holder.departmentName.text = list1[position].departmentName
        holder.date.text = sdf.format(list1[position].date)

        FirebaseController.interests.forEach {
            if(it.shiftToTakeId == list1[position].id){
                holder.intressBtn.isChecked =true;
            }else{
                holder.intressBtn.isChecked =false;
            }
        }
    }

    class CustomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val time =itemView.findViewById<TextView>(R.id.time)
        val departmentName =itemView.findViewById<TextView>(R.id.department_name)
        val date = itemView.findViewById<TextView>(R.id.date);
        val intressBtn =itemView.findViewById<CheckBox>(R.id.send_intress_btn)
    }
}



