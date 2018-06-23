package pami.com.pami

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import pami.com.pami.adapters.ClockedShiftAdapter
import pami.com.pami.models.*
import java.util.*


class ClockInDialogFragment : DialogFragment() {
    lateinit var clockInBtn: Button
    lateinit var clockOutBtn: Button
    lateinit var clockedMessage: TextView
    lateinit var sp: SharedPreferences
    lateinit var rv: RecyclerView;

    var clockedShift = ClockedShift()
    var clockedShifts = mutableListOf<ClockedShift>()
    var isGpsOn = false

    lateinit var locationManager: LocationManager;
    lateinit var company: Company;
    lateinit var savedLocation: Location;

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_clock_in, container, false)
        rv = view.findViewById<RecyclerView>(R.id.clocked_recycler_view)
        clockInBtn = view.findViewById(R.id.clock_in_btn1)
        clockOutBtn = view.findViewById(R.id.clock_out_btn1)
        clockInBtn.isEnabled = false
        clockOutBtn.isEnabled = false
        clockedMessage = view.findViewById(R.id.clocked_in_message)

        locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        FirebaseController.getCompany().subscribe {
            this.company = it;
            if (this.company.gpsLocation != null) {
                savedLocation = Location("")
                savedLocation.latitude = this.company.gpsLocation!!.latitude
                savedLocation.longitude = this.company.gpsLocation!!.longitude
            }

            when (company.locationType) {
                LocationType.none.name -> {

                    locationManager.removeUpdates(locationListener)

                    handleClockInStatusOfEmployee()
                }
                LocationType.gps.name -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.handleGpsSecurity()
                }
                LocationType.wifi.name -> this.handleWifiSecurity()
            }
        }



        this.sp = activity!!.getPreferences(android.content.Context.MODE_PRIVATE)



        clockInBtn.setOnClickListener { clockIn() }
        clockOutBtn.setOnClickListener { clockOut() }

        return view
    }

    private fun handleWifiSecurity() {
        val wm: WifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val id = wm.connectionInfo.ssid

        //needed because id containes quotation characters
        val index = id.indexOf(company.wifiName!!)
        locationManager.removeUpdates(locationListener)

        if (!wm.isWifiEnabled) {
            Toast.makeText(context, "Koppla mobilen till företagets wifi att kunna stmpla in", Toast.LENGTH_LONG).show()
        } else {
            if (index > -1) {
                Log.d("pawell", "wer")
                handleClockInStatusOfEmployee()
            } else {
                Log.d("pawell", "else 2344")
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = false
                Toast.makeText(context, "koppla till företagets wifi och starta om ", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleGpsSecurity() {
        Log.d("pawell", "handlegsp")


        isGpsOn = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("pawell", "111")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 0f, locationListener)
        } else {
//            Toast.makeText(context, "sätt på gps att kunna stämlpa in", Toast.LENGTH_LONG).show()
            Log.d("pawell", "222")
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("pawell", "if")
                activity!!.requestPermissions(permissions, 0)
            } else {
                Log.d("pawell", "else")
                Toast.makeText(context, "sätt på gps att kunna stämlpa in", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun clockOut() {
        val date = Date()
        clockedShift.timeStempOut = date.time

        clockedShift.messageOut = clockedMessage.text.toString()
        FirebaseController.removeShiftFromClockedInShifts(clockedShift).subscribe {
            if (it == true) {
                FirebaseController.addShiftsToAccept(clockedShift).subscribe {
                    if (it == true) {
                        clockInBtn.isEnabled = true
                        clockOutBtn.isEnabled = false
                        clockedMessage.text = ""
                    }
                }
            } else {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun clockIn() {

        val date = Date()
        val clockedShift = ClockedShift()

        clockedShift.timeStempIn = date.time
        clockedShift.firstName = User.firstName
        clockedShift.lastName = User.lastName
        clockedShift.employeeId = User.employeeId
        clockedShift.messageIn = clockedMessage.text.toString()

        FirebaseController.addToClockedInShifts(clockedShift).subscribe() {
            if (it == "") {
                Toast.makeText(context, "Instämpling misslyckades", Toast.LENGTH_SHORT).show()
            } else {
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = true
                clockedMessage.text = ""
            }
        }
    }


    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            if (p0 != null) {

                val distance = p0.distanceTo(savedLocation)

                if (distance < 70) {
//                    clockInBtn.isEnabled = true
//                    clockOutBtn.isEnabled = true
                    handleClockInStatusOfEmployee()
                } else {
                    clockInBtn.isEnabled = false
                    clockOutBtn.isEnabled = false
                }
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
            clockInBtn.isEnabled = true
            clockOutBtn.isEnabled = true
            isGpsOn = true
        }

        override fun onProviderDisabled(p0: String?) {
            isGpsOn = false
            clockOutBtn.isEnabled = false
            clockInBtn.isEnabled = true
        }
    }


    fun handleClockInStatusOfEmployee() {
        FirebaseController.getClockedInShifts().subscribe {
            clockedShifts = it
            clockedShifts.forEach {
                if (it.employeeId == User.employeeId) {
                    clockedShift = it
                    return@forEach
                }
            }

            val clockedAdapter = ClockedShiftAdapter(clockedShifts)
            rv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rv.adapter = clockedAdapter
            clockedAdapter.notifyDataSetChanged()

            if (clockedShift.clockedShiftId == "") {
                clockInBtn.isEnabled = true
                clockOutBtn.isEnabled = false


            } else {
                clockInBtn.isEnabled = false
                clockOutBtn.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }
}



