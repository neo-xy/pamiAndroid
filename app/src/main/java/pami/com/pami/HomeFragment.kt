package pami.com.pami

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class HomeFragment : Fragment() {

    var currantDate = Date();
    var df = DecimalFormat("00");
    val simpleDateFormat:SimpleDateFormat= SimpleDateFormat("dd MMM HH:mm")


    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment();
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val rec = view.findViewById<RecyclerView>(R.id.myRec)
        var calendar = Calendar.getInstance();
        calendar.time = currantDate;

        FirebaseController.getInstance().getShifts2().subscribe() {
            if (it.size > 0) {

                var upcomingShifts = mutableListOf<Shift>();
                var currentDateKey = (calendar.get(Calendar.YEAR).toString() + df.format(calendar.get(Calendar.MONTH) + 1) + df.format(calendar.get(Calendar.DATE))).toInt();

                Log.d("pawell", currentDateKey.toString())
                it.forEach { shift ->
                    var shiftDateKey = (shift.startTime.year.toString() + df.format(shift.startTime.month) + df.format(shift.startTime.day)).toInt();
                    Log.d("pawell", shiftDateKey.toString())
                    if (shiftDateKey >= currentDateKey) {
                        upcomingShifts.add(shift)
                    }
                }

//                upcomingShifts.sortBy { (it.startTime.year.toString() + df.format(it.startTime.month) + df.format(it.startTime.day)).toInt() }


                Collections.sort(upcomingShifts, object : Comparator<Shift> {
                    override fun compare(p0: Shift, p1: Shift): Int {
                        if (p0.startTime.year > p1.startTime.year) {
                            return 1
                        } else if (p0.startTime.year < p1.startTime.year) {
                            return -1
                        } else {
                            if (p0.startTime.month > p1.startTime.month) {
                                return 1
                            } else if (p0.startTime.month < p1.startTime.month) {
                                return -1
                            } else {

                                if (p0.startTime.day > p1.startTime.day) {
                                    return 1
                                } else if (p0.startTime.day < p1.startTime.day) {
                                    return -1
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }
                })

                val adap = ShiftsAdapter(upcomingShifts);
                rec.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                rec.adapter = adap;
                adap.notifyDataSetChanged()

            }
        }

        FirebaseController.getInstance().getCompany().subscribe() {

            info_name_tv.text = it.infoMessage?.author?.capitalize();
            info_message_tv.text = it.infoMessage?.message?.capitalize();
            info_tel_tv.text = it.infoMessage?.authorTel;
            info_date_tv.text = simpleDateFormat.format(it.infoMessage?.date);
        }
        return view;
    }


}// Required empty public constructor
