package pami.com.pami

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Pawel on 11/03/2018.
 */
class ClockInDialogFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_clock_in,container,false)

        return view
    }

}