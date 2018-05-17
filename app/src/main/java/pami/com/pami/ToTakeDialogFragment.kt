package pami.com.pami

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import pami.com.pami.adapters.ToTakeAdapter
import pami.com.pami.models.ShiftsToTake
import pami.com.pami.models.User

class ToTakeDialogFragment(): DialogFragment(),RecyclerViewClickListener {

    lateinit var shiftsToTake:MutableList<ShiftsToTake>;
    override fun listItemClicked(view: View, position: Int) {

            val pickedInterest = shiftsToTake[position]
            pickedInterest.employeeId= User.employeeId;
            FirebaseController.addShiftToTakeIntress(pickedInterest)
            Toast.makeText(context,"Intresse skickades!",Toast.LENGTH_SHORT).show()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_to_take, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.shifts_to_take_recycler_view);
        shiftsToTake = arguments?.get("shiftsToTake") as MutableList<ShiftsToTake>

        val toTakeAdapter = ToTakeAdapter(shiftsToTake, this)
        rv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        rv.adapter = toTakeAdapter
        toTakeAdapter.notifyDataSetChanged()
        return view
    }
}