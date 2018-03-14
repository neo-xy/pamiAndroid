package pami.com.pami

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import java.util.*


class ClockInDialogFragment : DialogFragment() {
    lateinit var clockInBtn: Button;
    lateinit var clockOutBtn: Button;
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

        this.sp = activity!!.getPreferences(android.content.Context.MODE_PRIVATE)

        FirebaseController.getClockedInShifts().subscribe {
            clockedShifts = it
            clockedShifts.forEach {
                if (it.employeeId == User.employeeId) {
                    clockedShift = it
                    return@forEach
                }
            }

            val clockedAdapter = ClockedShiftAdapter(clockedShifts);
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
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val customeDate = CustomDateModel()

        customeDate.year = year
        customeDate.month = month
        customeDate.day = day
        customeDate.hour = hour
        customeDate.minute = minute


        clockedShift.endTime = customeDate
        FirebaseController.removeShiftFromClockedInShifts(clockedShift).subscribe {
            if (it == true) {
                FirebaseController.addShiftsToAccept(clockedShift).subscribe {
                    if (it == true) {
                        clockInBtn.isEnabled = true
                        clockOutBtn.isEnabled = false
                    }
                }
            }
        }
    }

    private fun clockIn() {
        val date = Date()
        val clockedInTime = Calendar.getInstance();
        clockedInTime.time = date
        val year = clockedInTime.get(Calendar.YEAR)
        val month = clockedInTime.get(Calendar.MONTH) + 1
        val day = clockedInTime.get(Calendar.DAY_OF_MONTH)
        val hour = clockedInTime.get(Calendar.HOUR_OF_DAY)
        val minute = clockedInTime.get(Calendar.MINUTE)

        val clockedShift = ClockedShift()
        val customeDate = CustomDateModel()

        customeDate.year = year
        customeDate.month = month
        customeDate.day = day
        customeDate.hour = hour
        customeDate.minute = minute

        clockedShift.startTime = customeDate
        clockedShift.firstName = User.firstName
        clockedShift.lastName = User.lastName
        clockedShift.employeeId = User.employeeId

        FirebaseController.addToClockedInShifts(clockedShift).subscribe() {
            if (it == "") {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            } else {
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = true
            }
        }
    }


}