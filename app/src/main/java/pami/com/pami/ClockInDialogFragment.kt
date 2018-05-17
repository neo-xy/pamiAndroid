package pami.com.pami

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import pami.com.pami.adapters.ClockedShiftAdapter
import pami.com.pami.models.ClockedShift
import pami.com.pami.models.Shift
import pami.com.pami.models.User
import java.util.*


class ClockInDialogFragment : DialogFragment() {
    lateinit var clockInBtn: Button
    lateinit var clockOutBtn: Button
    lateinit var clockedMessage:TextView
    lateinit var sp: SharedPreferences
    val DISTINCTION = 30
    var selectedShift = Shift()
    var clockedShift = ClockedShift()
    var clockedShifts = mutableListOf<ClockedShift>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_clock_in, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.clocked_recycler_view)

        clockInBtn = view.findViewById(R.id.clock_in_btn1)
        clockOutBtn = view.findViewById(R.id.clock_out_btn1)
        clockedMessage = view.findViewById(R.id.clocked_in_message)

        this.sp = activity!!.getPreferences(android.content.Context.MODE_PRIVATE)

        FirebaseController.getClockedInShifts().subscribe {
            clockedShifts = it
            clockedShifts.forEach {
                if (it.employeeId == User.employeeId) {
                    clockedShift = it
                    return@forEach
                }
            }

            val clockedAdapter = ClockedShiftAdapter(clockedShifts)
            rv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rv.adapter = clockedAdapter
            clockedAdapter.notifyDataSetChanged()

            if (clockedShift.clockedShiftId == "") {
                clockInBtn.isEnabled = true
                clockOutBtn.isEnabled = false


            } else {
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = true

            }
        }

        clockInBtn.setOnClickListener { clockIn() }
        clockOutBtn.setOnClickListener { clockOut() }

        return view
    }

    private fun clockOut() {
        val date = Date()

        clockedShift.timeStempOut = date.time

        clockedShift.messageOut = clockedMessage.text.toString()
        FirebaseController.removeShiftFromClockedInShifts(clockedShift).subscribe {
            if (it == true) {
                FirebaseController.addShiftsToAccept(clockedShift).subscribe {
                    if (it == true) {
                        clockInBtn.isEnabled = true
                        clockOutBtn.isEnabled = false
                        clockedMessage.text=""
                    }
                }
            }else{
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clockIn() {
        val date = Date()
        val clockedShift = ClockedShift()

        clockedShift.timeStempIn = date.time
        clockedShift.firstName = User.firstName
        clockedShift.lastName = User.lastName
        clockedShift.employeeId = User.employeeId
        clockedShift.messageIn = clockedMessage.text.toString()

        FirebaseController.addToClockedInShifts(clockedShift).subscribe() {
            if (it == "") {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            } else {
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = true
                clockedMessage.text=""
            }
        }
    }


}