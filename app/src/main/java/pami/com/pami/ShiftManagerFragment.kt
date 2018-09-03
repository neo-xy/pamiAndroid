package pami.com.pami

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import pami.com.pami.adapters.ShiftManagerAdapter
import pami.com.pami.models.*
import java.util.*
import java.util.concurrent.TimeUnit

class ShiftManagerFragment : Fragment(), RecyclerViewClickListener {

    val clockedInEntres = mutableListOf<ClockedInEntre>()
    lateinit var recyclerView: RecyclerView
    lateinit var clockedShifts: MutableList<ClockedShift>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_shift_manager, container, false)
        recyclerView = view.findViewById(R.id.manager_recycler_view)

        FirebaseController.getClockedInShifts().subscribe {
            this.clockedInEntres.removeAll(this.clockedInEntres)
            clockedShifts = it

            FirebaseController.employees.forEach {employee->
                val entre = ClockedInEntre()

                var employeeIsClockedIn = false
                clockedShifts.forEach f@{shift->
                    if (shift.employeeId == employee.employeeId) {
                        employeeIsClockedIn = true

                        entre.name = shift.firstName + " " + shift.lastName
                        entre.employeeId = shift.employeeId
                        entre.startDate = shift.startDate

                        entre.clockedInShiftId = shift.clockedShiftId
                        Log.d("pawell","ggg "+ entre.startDate)

                        this.clockedInEntres.add(entre)
                        return@f
                    }
                }
                if (!employeeIsClockedIn) {
                    entre.name = employee.firstName + " " + employee.lastName
                    entre.startDate = null
                    entre.employeeId = employee.employeeId
                    entre.companyId = User.companies[User.latestCompanyIndex].companyId
                    this.clockedInEntres.add(entre)
                }
            }

            Collections.sort(this.clockedInEntres, object : Comparator<ClockedInEntre> {
                override fun compare(p0: ClockedInEntre, p1: ClockedInEntre): Int {
                    if (p0.startDate != null && p1.startDate!= null) {
                        if (p0.name > p1.name) {
                            return 1
                        } else {
                            return -1
                        }
                    } else {
                        if (p0.startDate != null) {
                            return -1
                        } else if (p1.startDate != null) {
                            return 1
                        } else {
                            if (p0.name > p1.name) {
                                return 1
                            } else {
                                return -1
                            }
                        }
                    }
                }
            })

            val managerAdapter = ShiftManagerAdapter(this.clockedInEntres, this)
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            recyclerView.adapter = managerAdapter
            managerAdapter.notifyDataSetChanged()

        }
        return view
    }

    private fun clockOut(entre: ClockedInEntre) {
        var shift = ClockedShift()

        clockedShifts.forEach {
            if (it.clockedShiftId == entre.clockedInShiftId) {
                shift = it;
            }
        }

        shift.endDate = Date()
        shift.messageOut = "Utstämplad av " + User.firstName + " " + User.lastName
        shift.shiftStatus = ShiftStatus.ClockedOut

        shift.shiftStatus = ShiftStatus.ClockedOut
        shift.employeeId = entre.employeeId

        var selectedEmployee:Employee?=null
        FirebaseController.employees.forEach { emp->
            if (emp.employeeId == entre.employeeId){
                selectedEmployee = emp
            }
        }

        shift.firstName = selectedEmployee!!.firstName
        shift.lastName = selectedEmployee!!.lastName

        shift.duration = (Math.round((TimeUnit.MILLISECONDS.toMinutes(shift.endDate!!.time - shift.startDate!!.time)/60.0)*100))/100.0

        val log = ShiftLog()
        log.shiftStatus = ShiftStatus.ClockedOut
        log.bossFirstName = User.firstName
        log.bossLastName =User.lastName
        log.bossId = User.employeeId
        log.startDate = shift.startDate
        log.endDate = shift.endDate
        log.date = shift.endDate
        shift.logs = mutableListOf()
        shift.logs.add(log)

        FirebaseController.removeShiftFromClockedInShifts(shift).subscribe {
            if (it == true) {
                Log.d("pawell","trueeee" + shift.firstName)
                FirebaseController.addShiftsToAccept(shift).subscribe()
            } else {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clockIn(entre: ClockedInEntre) {
        val date = Date()
        val clockedShift = ClockedShift()


        var emp = Employee()

        FirebaseController.employees.forEach {
            if (it.employeeId == entre.employeeId) {
                emp = it;
            }
        }

        clockedShift.firstName = emp.firstName
        clockedShift.lastName = emp.lastName
        clockedShift.employeeId = emp.employeeId
        clockedShift.socialNumber = emp.socialSecurityNumber

        clockedShift.messageIn = "Instämplad av " + User.firstName + " " + User.lastName
        clockedShift.startDate = date

        FirebaseController.addToClockedInShifts(clockedShift).subscribe() {
            if (it == "") {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun listItemClicked(view: View, position: Int) {

        val entre = this.clockedInEntres[position]
        val clockedStatusDialog = AlertDialog.Builder(context!!)
        clockedStatusDialog.setTitle(entre.name)
        if (entre.startDate != null) {
            clockedStatusDialog.setPositiveButton("Stämpla Ut", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    clockOut(entre)
                }
            })
        } else {
            clockedStatusDialog.setPositiveButton("Stämpla In", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    clockIn(entre)
                }
            })
        }

        clockedStatusDialog.setNegativeButton("Avbryt", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })
        clockedStatusDialog.show()
    }
}

class ClockedInEntre {
    var name: String = ""
    var timeStempIn: Long = 0
    var startDate:Date? = null
    var employeeId: String = ""
    var companyId: String = ""
    var startHour: String = ""
    var clockedInShiftId = ""
}

interface RecyclerViewClickListener {
    fun listItemClicked(view: View, position: Int){
        Log.d("pawell", "position "+ position)
    }
}