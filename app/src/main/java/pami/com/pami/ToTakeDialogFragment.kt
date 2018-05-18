package pami.com.pami

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import pami.com.pami.adapters.ToTakeAdapter
import pami.com.pami.models.Interest
import pami.com.pami.models.ShiftsToTake
import pami.com.pami.models.User
import java.util.*

class ToTakeDialogFragment() : DialogFragment(), RecyclerViewClickListener {

    lateinit var shiftsToTake: MutableList<ShiftsToTake>;
    override fun listItemClicked(view: View, position: Int) {


        val compoundButton = view as CheckBox
        val interest = Interest();
        interest.employeeId = User.employeeId;
        interest.shiftToTakeId = shiftsToTake[position].id
        interest.dateAdded = Date();
        interest.department = shiftsToTake[position].departmentName
//        interest.timeInterval = shiftsToTake[position].start+" "+ shiftsToTake[position].end

        interest.startDate = shiftsToTake[position].startDate
        interest.endDate = shiftsToTake[position].endDate

        var isRaport = false;
        var selected: Interest?=null;
        FirebaseController.interests.forEach {
            if (it.shiftToTakeId == interest.shiftToTakeId && it.employeeId == User.employeeId) {
                isRaport = true;
                selected = it;
            }
        }
        if (compoundButton.isChecked && !isRaport) {
            FirebaseController.addIntress(interest)
            Toast.makeText(context, "Intresse skickades!", Toast.LENGTH_SHORT).show()
        } else if(selected != null&&!compoundButton.isChecked) {
                FirebaseController.removeInterest(selected!!);
        }


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