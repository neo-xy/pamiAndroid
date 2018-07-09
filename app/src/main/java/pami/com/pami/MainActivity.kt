package pami.com.pami


import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
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
import com.google.firebase.iid.FirebaseInstanceId
import pami.com.pami.R.id.*
import pami.com.pami.models.ShiftsToTake
import pami.com.pami.models.User
import java.util.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var profileImageView: ImageView
    lateinit var callbackManager: CallbackManager
    lateinit var loginManager: LoginManager
    lateinit var clockedInShiftId: String
    lateinit var clockInBtn: Button
    lateinit var toTakeBtn: Button
    lateinit var sp: SharedPreferences
    lateinit var shiftsToTake: MutableList<ShiftsToTake>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refreshedToken = FirebaseInstanceId.getInstance().token
        if (refreshedToken != null) {
            Log.d("pawell", "token " + refreshedToken)
            FirebaseController.updateRegistrationToken(refreshedToken)
        };


        supportFragmentManager.beginTransaction().add(fragment_container.id, HomeFragment.getInstance()).commit()

        this.sp = this.getPreferences(android.content.Context.MODE_PRIVATE)
        this.clockedInShiftId = this.sp.getString("clockedInId", "");

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

        profileImageView = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.profile_image)

        FirebaseController.getShiftsToTake().subscribe() {

            if (it.size > 0) {
                this.shiftsToTake = it
                this.toTakeBtn.visibility = View.VISIBLE
            } else {
                this.toTakeBtn.visibility = View.GONE
            }
        }
        FirebaseController.getInterests().subscribe()




        if (User.employmentStatus == "passed") {
            nav_view.menu.findItem(nav_calendar).setVisible(false)
            nav_view.menu.findItem(nav_shifts).setVisible(false)
            nav_view.menu.findItem(nav_contacts).setVisible(false)
        }
        if (User.companies[User.latestCompanyIndex].role != "boss") {
            nav_view.menu.findItem(nav_shift_manager).setVisible(false)
        }

        FirebaseAuth.getInstance().currentUser!!.providerData.forEach {
            if (it.providerId == "facebook.com") {
                nav_view.menu.findItem(nav_link).setVisible(false)
            }
        }


        val drawerNameTV: TextView
        drawerNameTV = nav_view.getHeaderView(0).findViewById<TextView>(R.id.drawer_name_tv)
        drawerNameTV.text = User.firstName.capitalize() + " " + User.lastName.capitalize()
        if (User.imgUrl.length < 1) {

        } else {
            Glide.with(this)
                    .load(User.imgUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView)
        }

        val drawerMailTV = nav_view.getHeaderView(0).findViewById<TextView>(R.id.drawer_mail_tv)
        if (drawerMailTV != null) {
            drawerMailTV.text = User.email
        }

        FirebaseController.getClockedInShifts().subscribe() {
            var isClockedIn = false
            it.forEach {
                if (it.employeeId == User.employeeId) {
                    isClockedIn = true
                    return@forEach
                }
            }

            if (isClockedIn) {
                clockInBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark))
            } else {
                clockInBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.main_gray))
            }
        }
        FirebaseController.setUpColleagues();
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
        this.loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val token = result!!.accessToken.token
                val credential = FacebookAuthProvider.getCredential(token)
                FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credential).addOnCompleteListener {
                    it.getResult().user.providerData.forEach {
                        if (it.providerId == "facebook.com") {
                            FirebaseController.saveImgUrl(it.photoUrl)
                            Glide.with(this@MainActivity)
                                    .load(it.photoUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(this@MainActivity.profileImageView)
                            nav_view.menu.findItem(nav_link).setVisible(false)
                        }
                    }
                }
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
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
        var bundle = Bundle()

        bundle.putParcelableArrayList("shiftsToTake", this.shiftsToTake as ArrayList<out Parcelable>)
        toTakeDialogFragment.arguments = bundle;
        toTakeDialogFragment.arguments
        toTakeDialogFragment.show(supportFragmentManager, "toTake")
    }
}






