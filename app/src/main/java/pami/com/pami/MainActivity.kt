package pami.com.pami


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.Layout
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import kotlinx.android.synthetic.main.app_bar_main.view.*
import pami.com.pami.R.id.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var profileImageView: ImageView
    lateinit var callbackManager: CallbackManager
    lateinit var loginManager: LoginManager
    lateinit var clockedInShiftId: String
    lateinit var sp: SharedPreferences

    lateinit var clockOutItem: MenuItem

    lateinit var clockInItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(fragment_container.id, HomeFragment.getInstance()).commit()


        this.sp = this.getPreferences(android.content.Context.MODE_PRIVATE)
        this.clockedInShiftId = this.sp.getString("clockedInId", "");

//        toolbar.setBackgroundResource(R.color.colorPrimaryLight)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        this.loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        this.linkWithFacebook()

        nav_view.setNavigationItemSelectedListener(this)

        profileImageView = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.profile_image)

        if (clockedInShiftId != "") {
            app_main.clock_in_indicator.visibility = View.VISIBLE
        } else {
            app_main.clock_in_indicator.visibility = View.INVISIBLE
        }




        if (User.employmentStatus == "passed") {
            nav_view.menu.findItem(nav_calendar).setVisible(false)
            nav_view.menu.findItem(nav_shifts).setVisible(false)
            nav_view.menu.findItem(nav_contacts).setVisible(false)
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
        FirebaseController.setUpColleagues();

        FirebaseController.setupSalleries()

    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        this.clockOutItem =menu!!.findItem(R.id.clock_out)
        this.clockInItem =menu.findItem(R.id.clock_in)

        if (clockedInShiftId != "") {
            clockOutItem.setVisible(true)
           clockInItem.setVisible(false)
        } else {
           clockOutItem.setVisible(false)
            clockInItem.setVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            clock_in -> {
                checkoutIfitMatchesAshift(Date())
                app_main.clock_in_indicator.visibility=View.VISIBLE
                item.setVisible(false)
                clockOutItem.setVisible(true)
            }
            clock_out -> {
                this.sp.edit().putString("clockedInId", "").apply();
                app_main.clock_in_indicator.visibility = View.INVISIBLE
                item.setVisible(false)
                clockInItem.setVisible(true)
            }
        }
        return true;
    }

    private fun checkoutIfitMatchesAshift(date: Date) {
        val clockedInTime = Calendar.getInstance();
        clockedInTime.time = date
        val year = clockedInTime.get(Calendar.YEAR)
        val month = clockedInTime.get(Calendar.MONTH) + 1
        val day = clockedInTime.get(Calendar.DAY_OF_MONTH)
        val hour = clockedInTime.get(Calendar.HOUR_OF_DAY)
        val minute = clockedInTime.get(Calendar.MINUTE)

        var correspondingShiftHasBeenFound = false
        var selectedShift = Shift()

        FirebaseController.shifts.forEach {

            val orginalDate = Date()
            val cal = Calendar.getInstance()
            cal.time = orginalDate
            cal.set(Calendar.YEAR, it.startTime.year)
            cal.set(Calendar.DAY_OF_MONTH, it.startTime.day)
            cal.set(Calendar.MONTH, it.startTime.month - 1)
            cal.set(Calendar.HOUR_OF_DAY, it.startTime.hour)
            cal.set(Calendar.MINUTE, it.startTime.minute)


            val startTimeDifferance = TimeUnit.MILLISECONDS
                    .toMinutes(Math.sqrt(Math.pow((clockedInTime.time.time - cal.time.time).toDouble(), 2.toDouble())).toLong())

            if (startTimeDifferance < 300) {
                Log.d("pawell","222")
                this.clockedInShiftId = it.shiftId

                if(startTimeDifferance<15){
                    it.startTime.year = year
                    it.startTime.month = month
                    it.startTime.day = day
                    it.startTime.hour = hour
                    it.startTime.minute = minute
                    FirebaseController.updateShift(it)
                }else{
                    FirebaseController.markAsToAccept(it)
                }

                correspondingShiftHasBeenFound = true
                selectedShift = it
                return
            }
        }

        if(!correspondingShiftHasBeenFound){

            Log.d("pawell","333")
            selectedShift.startTime.year=year
            selectedShift.startTime.month=month
            selectedShift.startTime.day=day
            selectedShift.startTime.hour=hour
            selectedShift.startTime.minute=minute
            FirebaseController.markAsToAcceptNew(selectedShift).subscribe{
                sp.edit().putString("clockedInId",it).apply();
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            nav_shifts -> {

                supportFragmentManager.beginTransaction().replace(fragment_container.id, (ShiftsFragment.getInstance())).commit()
            }
            nav_home -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, (HomeFragment())).commit()
            }
            nav_calendar -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, ScheduleFragment(), "schedule").commit()
            }
            nav_settings -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, SettingsFragment(), "settings").commit()
            }

            nav_logout -> {
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            nav_personal_information -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, PersonalInformationFragment(), "personalInfo").commit()
            }

            nav_contacts -> {
                supportFragmentManager.beginTransaction().replace(fragment_container.id, DashboardFragment(), "contacts").commit()
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
}



