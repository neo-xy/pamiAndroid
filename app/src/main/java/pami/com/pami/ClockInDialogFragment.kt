package pami.com.pami

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import io.reactivex.disposables.Disposable
import pami.com.pami.adapters.ClockedShiftAdapter
import pami.com.pami.models.ClockedShift
import pami.com.pami.models.Company
import pami.com.pami.models.LocationType
import pami.com.pami.models.User
import java.util.*


class ClockInDialogFragment : DialogFragment() {


    lateinit var clockInBtn: Button
    lateinit var clockOutBtn: Button
    lateinit var clockedMessage: TextView
    lateinit var sp: SharedPreferences
    lateinit var rv: RecyclerView;

    var clockedShift = ClockedShift()
    var clockedShifts = mutableListOf<ClockedShift>()

    lateinit var locationManager: LocationManager
    lateinit var company: Company
    lateinit var savedLocation: Location

    lateinit var dispComp: Disposable
   var dispClockedInShifts: Disposable? =null

    val permissionArray = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)

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

        dispComp = FirebaseController.getCompany().subscribe {

            clockInBtn.isEnabled = false
            clockOutBtn.isEnabled = false
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
        val wm: WifiManager = activity!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val id = wm.connectionInfo.ssid

        //needed because id containes quotation characters
        val index = id.indexOf(company.wifiName!!)
        locationManager.removeUpdates(locationListener)

        if (!wm.isWifiEnabled || index < 0) {
            Toast.makeText(context, "Koppla mobilen till företagets wifi att kunna stmpla in", Toast.LENGTH_LONG).show()

            AlertDialog.Builder(context)
                    .setTitle("Stämpelklocka")
                    .setMessage("Koppla mobilen till företagets Wifi för att kunna stämpla In och Ut")
                    .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            dismiss()
                        }
                    }).create().show()
        } else {
            handleClockInStatusOfEmployee()
        }
    }


    private fun handleGpsSecurity() {
        this@ClockInDialogFragment.requestPermissions(permissionArray, 3)

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "dröj medans din Gps ställs in ", Toast.LENGTH_LONG).show()
            if (this.checkPermission(context!!, permissionArray)) {
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 0f, locationListener)
            }

        } else {
            AlertDialog.Builder(context)
                    .setTitle("Stämpelklocka")
                    .setMessage("Sätt på ditt GPS att kunna stämpla in och ut")
                    .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            dismiss()
                        }
                    }).create().show()
        }
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (context.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
            }
        }
        return allSuccess
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

                if (distance < 100) {
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
        }

        override fun onProviderDisabled(p0: String?) {
            clockOutBtn.isEnabled = false
            clockInBtn.isEnabled = false
        }
    }


    fun handleClockInStatusOfEmployee() {
        dispClockedInShifts = FirebaseController.getClockedInShifts().subscribe {
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
        if(!dispComp.isDisposed){
            dispComp.dispose()
        }
        dispComp.dispose()
        if(dispClockedInShifts != null && !dispClockedInShifts!!.isDisposed){
            dispClockedInShifts!!.dispose()
        }

        locationManager.removeUpdates(locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            AlertDialog.Builder(context)
                    .setTitle("Tillåtelse för platstjänsten")
                    .setMessage("För att kunna stämpla in måste du tillåta platstjänsten för appen, det hittar du under appens inställningar -> Behörighet")
                    .setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            dismiss()
                        }
                    }).create().show()
        }

    }
}



