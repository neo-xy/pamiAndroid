package pami.com.pami

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import pami.com.pami.R.id.*
import pami.com.pami.adapters.ShiftsAdapter
import pami.com.pami.models.Shift
import pami.com.pami.models.User
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    var currantDate = Date()
    var df = DecimalFormat("00")
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM HH:mm")
    lateinit var companyObservable: Disposable

    lateinit var infoName:TextView;
    lateinit var infoDate:TextView
    lateinit var infoMsg:TextView
    lateinit var infoTel:TextView

    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val calendar = Calendar.getInstance()
        val infoCardBoss = view.findViewById<CardView>(R.id.info_card)

        infoName = view.findViewById(R.id.info_name_tv)
        infoDate = view.findViewById(R.id.info_date_tv)
        infoMsg = view.findViewById(R.id.info_message_tv)
        infoTel = view.findViewById(R.id.info_tel_tv)

        calendar.time = currantDate

        FirebaseController.getUserShifts().subscribe() {
            if (it.size > 0) {

                var upcomingShifts = mutableListOf<Shift>()
                val currentDateKey = (calendar.get(Calendar.YEAR).toString() + df.format(calendar.get(Calendar.MONTH) ) + df.format(calendar.get(Calendar.DATE))).toInt()

                it.forEach { shift ->
                    val shiftDateKey = (shift.start.get(Calendar.YEAR).toString() + df.format(shift.start.get(Calendar.MONTH)) + df.format(shift.start.get(Calendar.DATE))).toInt()
                    if (shiftDateKey >= currentDateKey) {
                        upcomingShifts.add(shift)
                    }
                }
                upcomingShifts = Shared.sortShifts(upcomingShifts)
                Log.d("pawell","sssss "+ it.size)

                val adap = ShiftsAdapter(upcomingShifts)
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                recyclerView.adapter = adap
                adap.notifyDataSetChanged()
            }
        }

        FirebaseController.getUanavailableDates().subscribe();
        FirebaseController.getAcceptedShifts().subscribe();

        if (User.employmentStatus == "passed") {
            infoCardBoss.visibility = View.GONE
        }

        this.companyObservable = FirebaseController.getCompany().subscribe() {
            if (it.infoMessage!!.message.isEmpty()) {
                infoCardBoss.visibility = View.GONE
            }
            infoName.text = it.infoMessage?.author?.capitalize()
            infoMsg.text = it.infoMessage?.message?.capitalize()
            infoTel.text = it.infoMessage?.authorTel
            infoDate.text = simpleDateFormat.format(it.infoMessage?.date)
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        this.companyObservable.dispose()
    }
}
