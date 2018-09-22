package pami.com.pami


import android.content.*
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pami.com.pami.R.id.*
import pami.com.pami.models.RoleType
import pami.com.pami.models.ShiftsToTake
import pami.com.pami.models.User
import java.util.*
import kotlin.Comparator


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var profileImageView: ImageView
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager
    private lateinit var clockedInShiftId: String
    private lateinit var clockInBtn: Button
    private lateinit var toTakeBtn: Button
    private lateinit var sp: SharedPreferences
    private lateinit var shiftsToTake: MutableList<ShiftsToTake>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                IntentFilter("tokenReceiver"));

//        val refreshedToken = FirebaseInstanceId.getInstance().token
//        if (refreshedToken != null) {
//            FirebaseController.updateRegistrationToken(refreshedToken)
//        }

        supportFragmentManager.beginTransaction().add(fragment_container.id, HomeFragment.getInstance()).commit()

        FirebaseController.getInfoMessagesForDay()

        this.sp = this.getPreferences(android.content.Context.MODE_PRIVATE)
        this.clockedInShiftId = this.sp.getString("clockedInId", "")

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        this.clockInBtn = findViewById(R.id.clock_in_btn)
        this.toTakeBtn = findViewById(R.id.to_take_btn)
        clockInBtn.setOnClickListener { openClockInDialog() }
        toTakeBtn.setOnClickListener { openShiftsToTakeDialog() }
        this.loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        this.linkWithFacebook()

        nav_view.setNavigationItemSelectedListener(this)

        profileImageView = nav_view.getHeaderView(0).findViewById(R.id.profile_image)

        FirebaseController.getShiftsToTake().subscribe {

            if (it.size > 0) {
                this.shiftsToTake = it
                this.toTakeBtn.visibility = View.VISIBLE
            } else {
                this.toTakeBtn.visibility = View.GONE
            }
        }
        FirebaseController.getInterests().subscribe()

        if (User.employmentStatus == "passed") {
            nav_view.menu.findItem(nav_calendar).isVisible = false
            nav_view.menu.findItem(nav_shifts).isVisible = false
            nav_view.menu.findItem(nav_contacts).isVisible = false
        }
        if (User.role != RoleType.Boss) {
            nav_view.menu.findItem(nav_shift_manager).isVisible = false
        }

        User.salaries.sortWith(Comparator { p0, p1 ->
            when {
                p0.startDate > p1.startDate -> -1
                p0.startDate == p1.startDate -> 0
                else -> 1
            }
        })


        nav_view.menu.findItem(nav_sick).isVisible = false
//        User.salaries = Shared.sortSalaries(User.salaries)

        FirebaseController.getCompany().subscribe {
            nav_view.menu.findItem(nav_sick).isVisible = true

            val currentSalary = Shared.getSalarieOfDate(Date())
            nav_view.menu.findItem(nav_sick).isVisible = false
            it.sickAccess.forEach { sickAcces ->

                if(sickAcces==currentSalary.employmentType?.ordinal){
                    nav_view.menu.findItem(nav_sick).isVisible = true
                    return@forEach
                }else{
                    nav_view.menu.findItem(nav_sick).isVisible = false
                }
//                Log.d("pawell", "par " + User.salaries[0].employmentType?.name)
//                if (sickAcces == User.salaries[0].employmentType?.ordinal && User.salaries[0].partValue != 100) {
//                    nav_view.menu.findItem(nav_sick).isVisible = true
//                } else if (sickAcces == "fullTime") {
//                    if (User.salaries[0].employmentType?.name == "partTime" && User.salaries[0].partValue == 100) {
//                        nav_view.menu.findItem(nav_sick).isVisible = true
//                    }
//                }

            }
        }


        FirebaseAuth.getInstance().currentUser!!.providerData.forEach {
            if (it.providerId == "facebook.com") {
                nav_view.menu.findItem(nav_link).isVisible = false
            }
        }

        val drawerNameTV: TextView = nav_view.getHeaderView(0).findViewById(R.id.drawer_name_tv)
        val displayedName = User.firstName.capitalize() + " " + User.lastName.capitalize()
        drawerNameTV.text = displayedName
        if (!User.imgUrl.isEmpty()) {
            Glide.with(this)
                    .load(User.imgUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView)
        }

        val drawerMailTV = nav_view.getHeaderView(0).findViewById<TextView>(R.id.drawer_mail_tv)
        if (drawerMailTV != null) {
            drawerMailTV.text = User.email
        }

        FirebaseController.getClockedInShifts().subscribe {
            var isClockedIn = false
            it.forEach { cshift ->
                if (cshift.employeeId == User.employeeId) {
                    isClockedIn = true
                    return@forEach
                }
            }

            if (isClockedIn) {
                clockInBtn.backgroundTintList = ColorStateList.valueOf(ActivityCompat.getColor(this, R.color.colorPrimaryDark))
            } else {
                clockInBtn.backgroundTintList = ColorStateList.valueOf(ActivityCompat.getColor(this, R.color.main_gray))
            }
        }
        FirebaseController.setUpColleagues()
        FirebaseController.setupSalleries()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            nav_shifts -> {

                supportFragmentManager.beginTransaction().replace(fragment_container.id, (SaleriesFragment.getInstance())).commit()
            }
            nav_home -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, (HomeFragment())).commit()
            }
            nav_calendar -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, ScheduleFragment(), "schedule").commit()
            }
//            nav_settings -> {
//                supportFragmentManager.beginTransaction().replace(fragment_container.id, SettingsFragment(), "settings").commit()
//            }

            nav_logout -> {
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            nav_personal_information -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, PersonalInformationFragment(), "personalInfo").commit()
            }
            nav_sick -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, SickFragment(), "sick").commit()
            }

            nav_contacts -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, DashboardFragment(), "contacts").commit()
            }
            nav_shift_manager -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, ShiftManagerFragment(), "shiftManager").commit()
            }

            nav_link -> {
                this.loginManager.logInWithReadPermissions(this, mutableListOf("email", "public_profile"))
            }
        }

        drawer_layout.closeDrawer(Gravity.START)
        return true
    }

    private fun linkWithFacebook() {
        Log.d("pawell", "link with facebook")
        this.loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                Log.d("pawell", "link with facebook222")
                val token = result!!.accessToken.token
                val credential = FacebookAuthProvider.getCredential(token)
                FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credential).addOnCompleteListener {
                    Log.d("pawell", "link with facebook333")
                    it.result.user.providerData.forEach { userInfo ->
                        if (userInfo.providerId == "facebook.com") {
                            FirebaseController.saveImgUrl(userInfo.photoUrl)
                           Log.d("pawell","id"+ userInfo.uid)
                            Glide.with(this@MainActivity)
                                    .load(userInfo.photoUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(this@MainActivity.profileImageView)
                            nav_view.menu.findItem(nav_link).isVisible = false
                        }
                    }
                }
            }

            override fun onCancel() {

                Log.d("pawell", "link with facebook4444")
            }

            override fun onError(error: FacebookException?) {
                Log.d("pawell", "link with facebook5555" + error)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun openClockInDialog() {
        val clockInDialogFragment = ClockInDialogFragment()
        clockInDialogFragment.show(supportFragmentManager, "clockIn")
    }

    private fun openShiftsToTakeDialog() {
        val toTakeDialogFragment = ToTakeDialogFragment()
        val bundle = Bundle()

        bundle.putParcelableArrayList("shiftsToTake", this.shiftsToTake as ArrayList<out Parcelable>)
        toTakeDialogFragment.arguments = bundle
        toTakeDialogFragment.arguments
        toTakeDialogFragment.show(supportFragmentManager, "toTake")
    }

    var tokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val token = intent.getStringExtra("token")
            if (token != null) {
                //send token to your server or what you want to do
                FirebaseController.updateRegistrationToken(token)
            }

        }
    }
}






