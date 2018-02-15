package pami.com.pami


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_schedule.*


/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater!!.inflate(R.layout.fragment_schedule, container, false);

        return view
    }

    companion object {
        fun getInstance():ScheduleFragment{
            return ScheduleFragment();
        }
    }

}// Required empty public constructor
