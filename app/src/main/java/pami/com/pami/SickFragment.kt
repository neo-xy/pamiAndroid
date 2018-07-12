package pami.com.pami


import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.fragment_sick.view.*
import pami.com.pami.models.SickReport
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
    var reports = mutableListOf<SickReport>()

    val df = SimpleDateFormat("yyyy MMM dd")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sick, container, false)

        FirebaseController.getSickReports().subscribe {
            it.forEach {
                if (it.rangeEnd > Date()) {
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
            onSendSickReport(it)
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
            }

        }
        return view
    }

    private fun showReports() {

    }

    fun onSendSickReport(view: View) {
        Log.d("pawell", "lll" + startDate + "  " + endDate)

        if (startDate != null && endDate != null) {

            val sickReport = SickReport()
            sickReport.rangeStart = startDate as Date
            sickReport.rangeEnd = endDate as Date
            sickReport.employeeId = User.employeeId
            sickReport.dateAdded = Date()
            sickReport.type = SickType.NORMAL
            FirebaseController.addSickReport(sickReport)
            sendSickBtn.isEnabled = false
            sendChildSickBtn.isEnabled = false
        }
    }

    fun onSendChildSickReport(view: View) {
        if (startDate != null && endDate != null) {

            val sickReport = SickReport()
            sickReport.rangeStart = startDate as Date
            sickReport.rangeEnd = endDate as Date
            sickReport.employeeId = User.employeeId
            sickReport.dateAdded = Date()
            sickReport.type = SickType.CHILD
            FirebaseController.addSickReport(sickReport)
            sendSickBtn.isEnabled = false
            sendChildSickBtn.isEnabled = false

        }
    }

    fun showReports(view: View) {
        var reportsString = mutableListOf<String>()
        reports.forEach {
            reportsString.add(df.format(it.rangeStart) + "  ->  " + df.format(it.rangeEnd)+ " ("+it.type.name+")")
        }

        var builder = AlertDialog.Builder(context!!)
                .setTitle("Tryck på tiden att ta bort")
                .setItems(reportsString.toTypedArray(), DialogInterface.OnClickListener { dialogInterface, i ->
                   FirebaseController.removeSickReport(reports[i].reportId)
                })
                .create()
        builder.show()


    }
}
