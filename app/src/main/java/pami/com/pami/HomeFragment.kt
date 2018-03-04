package pami.com.pami

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    var currantDate = Date();
    var df = DecimalFormat("00");
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM HH:mm")
    lateinit var companyObservable: Disposable

    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment();
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val calendar = Calendar.getInstance();
        calendar.time = currantDate;

        FirebaseController.getShifts().subscribe() {
            if (it.size > 0) {

                val upcomingShifts = mutableListOf<Shift>();
                val currentDateKey = (calendar.get(Calendar.YEAR).toString() + df.format(calendar.get(Calendar.MONTH) + 1) + df.format(calendar.get(Calendar.DATE))).toInt();

                it.forEach { shift ->
                    val shiftDateKey = (shift.startTime.year.toString() + df.format(shift.startTime.month) + df.format(shift.startTime.day)).toInt();
                    if (shiftDateKey >= currentDateKey) {
                        upcomingShifts.add(shift)
                    }
                }

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
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                recyclerView.adapter = adap;
                adap.notifyDataSetChanged()
            }
        }

        this.companyObservable = FirebaseController.getCompany().subscribe() {

            info_name_tv.text = it.infoMessage?.author?.capitalize()
            info_message_tv.text = it.infoMessage?.message?.capitalize();
            info_tel_tv.text = it.infoMessage?.authorTel;
            info_date_tv.text = simpleDateFormat.format(it.infoMessage?.date);
        }
        return view;
    }

    override fun onDestroy() {
        super.onDestroy()
        this.companyObservable.dispose()
    }
}
