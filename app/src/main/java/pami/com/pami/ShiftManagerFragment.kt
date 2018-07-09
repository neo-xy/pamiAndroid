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
import pami.com.pami.models.ClockedShift
import pami.com.pami.models.Employee
import pami.com.pami.models.User
import java.util.*

class ShiftManagerFragment : Fragment(), RecyclerViewClickListener {

    val employees = mutableListOf<ClockedInEntre>()
    lateinit var recyclerView: RecyclerView
    lateinit var clockedShifts: MutableList<ClockedShift>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_shift_manager, container, false)
        recyclerView = view.findViewById(R.id.manager_recycler_view)

        FirebaseController.getClockedInShifts().subscribe {
            employees.removeAll(employees)
            clockedShifts = it

            FirebaseController.employees.forEach {
                val entre = ClockedInEntre()
                val employee = it
                var employeeIsClockedIn = false
                clockedShifts.forEach {
                    if (it.employeeId == employee.employeeId) {
                        employeeIsClockedIn = true

                        entre.name = it.firstName + " " + it.lastName
                        entre.timeStempIn = it.timeStempIn
                        entre.employeeId = it.employeeId

                        entre.clockedInShiftId = it.clockedShiftId

                        employees.add(entre)
                        return@forEach
                    }
                }
                if (!employeeIsClockedIn) {
                    entre.name = it.firstName + " " + it.lastName
                    entre.timeStempIn = 0
                    entre.employeeId = it.employeeId
                    entre.companyId = User.companies[User.latestCompanyIndex].companyId
                    employees.add(entre)
                }
            }

            Collections.sort(employees, object : Comparator<ClockedInEntre> {
                override fun compare(p0: ClockedInEntre, p1: ClockedInEntre): Int {
                    if (p0.timeStempIn != 0L && p1.timeStempIn != 0L) {
                        if (p0.name > p1.name) {
                            return 1
                        } else {
                            return -1
                        }
                    } else {
                        if (p0.timeStempIn != 0L) {
                            return -1
                        } else if (p1.timeStempIn != 0L) {
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

            val managerAdapter = ShiftManagerAdapter(employees, this)
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            recyclerView.adapter = managerAdapter
            managerAdapter.notifyDataSetChanged()

        }
        return view
    }

    private fun clockOut(entre: ClockedInEntre) {
        val date = Date()
        var shift = ClockedShift()

        clockedShifts.forEach {
            if (it.clockedShiftId == entre.clockedInShiftId) {
                shift = it;
            }
        }

        shift.timeStempOut = date.time
        shift.messageOut = "Utstämplad av " + User.firstName + " " + User.lastName
        FirebaseController.removeShiftFromClockedInShifts(shift).subscribe {
            if (it == true) {
                FirebaseController.addShiftsToAccept(shift).subscribe {
                }
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

        clockedShift.timeStempIn = date.time
        clockedShift.firstName = emp.firstName
        clockedShift.lastName = emp.lastName
        clockedShift.employeeId = emp.employeeId
        clockedShift.messageIn = "Instämplad av " + User.firstName + " " + User.lastName

        FirebaseController.addToClockedInShifts(clockedShift).subscribe() {
            if (it == "") {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun listItemClicked(view: View, position: Int) {

        val employee = employees[position]
        val clockedStatusDialog = AlertDialog.Builder(context!!)
        clockedStatusDialog.setTitle(employee.name)
        if (employee.timeStempIn != 0L) {
            clockedStatusDialog.setPositiveButton("Stämpla Ut", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    clockOut(employee)
                }
            })
        } else {
            clockedStatusDialog.setPositiveButton("Stämpla In", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    clockIn(employee)
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