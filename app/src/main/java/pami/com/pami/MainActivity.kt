package pami.com.pami


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import pami.com.pami.R.font.logo_font
import pami.com.pami.R.id.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val firebaseAuth = FirebaseAuth.getInstance();
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            nav_shifts -> {

                supportFragmentManager.beginTransaction().replace(fragment_container.id, (ShiftsFragment.getInstance())).commit()
            }
            nav_home->{ supportFragmentManager.beginTransaction().replace(fragment_container.id, (HomeFragment())).commit()}
            nav_calendar->{supportFragmentManager.beginTransaction().replace(fragment_container.id, (ScheduleFragment())).commit()}
            nav_logout -> {
                FirebaseAuth.getInstance().signOut();
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent);
                finish();
            }
        }

        drawer_layout.closeDrawer(Gravity.START)
        return true;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(fragment_container.id, HomeFragment.getInstance()).commit()

        var l = AuthStateListener { auth ->
            FirebaseController.getInstance().getUser();
        }

        FirebaseController.getInstance().setUpUser().subscribe() {


               val drawerNameTV = findViewById<TextView>(R.id.drawer_name_tv);
               drawerNameTV.text = it.firstName.capitalize()+" "+it.lastName.capitalize();

               val drawerMailTV = findViewById<TextView>(R.id.drawer_mail_tv);
               drawerMailTV.text =User.email;

        }


        firebaseAuth.addAuthStateListener(l);

        toolbar.setBackgroundResource(R.color.colorPrimaryLight)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true;
    }
}



