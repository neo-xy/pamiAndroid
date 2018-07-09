package pami.com.pami


import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener
import kotlinx.android.synthetic.main.fragment_personal_information.view.*
import kotlinx.android.synthetic.main.fragment_sick.view.*
import pami.com.pami.models.SickReport
import pami.com.pami.models.User
import java.text.SimpleDateFormat
import java.util.*


class SickFragment : Fragment() {

    lateinit var fromDate: TextView
    lateinit var toDate: TextView
    lateinit var sendReportBtn:Button

    lateinit var rangeContainer:LinearLayout
    var startDate : Date? = null
    var endDate : Date? = null
    var reports = mutableListOf<SickReport>()

    val df = SimpleDateFormat("yyyy MMM dd")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sick, container, false)

        FirebaseController.getSickReports().subscribe {
            it.forEach {
                if(it.rangeEnd < Date()){
                    reports.add(it)
                }
            }
        }

        fromDate = view.findViewById(R.id.from_date)
        toDate = view.findViewById(R.id.to_date)
        sendReportBtn = view.send_report
        sendReportBtn.isEnabled = false
        sendReportBtn.setBackgroundColor(Color.LTGRAY)
        rangeContainer = view.range_container

        sendReportBtn.setOnClickListener{
            onSendSickRereport(it)
        }


        val cal: MaterialCalendarView = view.findViewById(R.id.myCal)
        cal.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE
        cal.setOnRangeSelectedListener { widget, dates ->
            rangeContainer.visibility = View.VISIBLE

            if(reports.size < 3){
                sendReportBtn.isEnabled = true

                if(dates.size > 0){
                    fromDate.text = df.format(dates[0].date)
                    toDate.text = df.format(dates[dates.size - 1].date)
                }
            }

        }
        return view
    }

    private fun showReports() {

    }

    fun onSendSickRereport(view: View) {
        if(startDate !=null && endDate!=null){

            val sickReport = SickReport()
            sickReport.rangeStart = startDate as Date
            sickReport.rangeEnd = endDate as Date
            sickReport.employeeId = User.employeeId
            sickReport.dateAdded = Date()
            FirebaseController.addSickReport(sickReport)
            sendReportBtn.isEnabled = false

        }
    }
}
