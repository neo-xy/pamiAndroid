package pami.com.pami


import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.fragment_sick.view.*
import pami.com.pami.models.AbsenceReport
import pami.com.pami.models.SickType
import pami.com.pami.models.User
import java.text.SimpleDateFormat
import java.util.*


class SickFragment : Fragment() {

    lateinit var fromDate: TextView
    lateinit var toDate: TextView
    lateinit var sendSickBtn: Button
    lateinit var sendChildSickBtn: Button

    lateinit var rangeContainer: LinearLayout
    var startDate: Date? = null
    var endDate: Date? = null
    var reports = mutableListOf<AbsenceReport>()

    val df = SimpleDateFormat("yyyy MMM dd")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sick, container, false)

        FirebaseController.getAbsenceReports().subscribe {
            reports.removeAll(reports)
            it.forEach {
                if (it.endDate > Date()) {
                    reports.add(it)
                }
            }
        }

        fromDate = view.findViewById(R.id.from_date)
        toDate = view.findViewById(R.id.to_date)
        sendSickBtn = view.send_sick
        sendSickBtn.isEnabled = false


        sendChildSickBtn = view.send_sickChild
        sendChildSickBtn.isEnabled = false


        rangeContainer = view.range_container

        sendSickBtn.setOnClickListener {
            onSendSickReport()
        }

        sendChildSickBtn.setOnClickListener {
            onSendChildSickReport(it)
        }
        view.see_sick_reports.setOnClickListener {
            showReports(it)
        }


        val cal: MaterialCalendarView = view.myCal
        cal.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE
        cal.setOnRangeSelectedListener { widget, dates ->
            rangeContainer.visibility = View.VISIBLE

            if (reports.size < 3) {
                sendSickBtn.isEnabled = true
                sendChildSickBtn.isEnabled = true

                if (dates.size > 0) {
                    fromDate.text = df.format(dates[0].date)
                    toDate.text = df.format(dates[dates.size - 1].date)
                    startDate = dates[0].date
                    endDate = dates[dates.size - 1].date
                }
            }else{
                Toast.makeText(context,"Maximal antal anmälningar nåddes",Toast.LENGTH_LONG).show()
            }

        }
        return view
    }

    fun onSendSickReport() {
        if (startDate != null && endDate != null) {

            val sickReport = AbsenceReport()
            sickReport.startDate = startDate as Date
            sickReport.endDate = endDate as Date
            sickReport.employeeId = User.employeeId
            sickReport.dateAdded = Date()
            sickReport.type = SickType.Normal
            sickReport.firstName = User.firstName
            sickReport.lastName = User.lastName
            sickReport.socialSecurityNumber  = User.socialSecurityNumber
            FirebaseController.addAbsenceReport(sickReport)
            sendSickBtn.isEnabled = false
            sendChildSickBtn.isEnabled = false
        }
    }

    fun onSendChildSickReport(view: View) {
        if (startDate != null && endDate != null) {

            val sickReport = AbsenceReport()
            sickReport.startDate = startDate as Date
            sickReport.endDate = endDate as Date
            sickReport.employeeId = User.employeeId
            sickReport.dateAdded = Date()
            sickReport.type = SickType.Child
            sickReport.firstName = User.firstName
            sickReport.lastName = User.lastName
            sickReport.socialSecurityNumber  = User.socialSecurityNumber
            FirebaseController.addAbsenceReport(sickReport)
            sendSickBtn.isEnabled = false
            sendChildSickBtn.isEnabled = false
        }
    }

    fun showReports(view: View) {
        var reportsString = mutableListOf<String>()
        reports.forEach {
            var type =""
          if(it.type == SickType.Child){
              type = "(VAB)"
          }
            reportsString.add(df.format(it.startDate) + "  ->  " + df.format(it.endDate)+ " $type")
        }

        var builder = AlertDialog.Builder(context!!)
                .setTitle("Tryck på tiden att ta bort")
                .setItems(reportsString.toTypedArray(), DialogInterface.OnClickListener { dialogInterface, i ->
                   FirebaseController.removeAbsenceReport(reports[i].reportId)
                })
                .create()
        builder.show()


    }
}
