package pami.com.pami


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pami.com.pami.R.id.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val firebaseAuth = FirebaseAuth.getInstance();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(fragment_container.id, HomeFragment.getInstance()).commit()

        toolbar.setBackgroundResource(R.color.colorPrimaryLight)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        var drawerNameTV: TextView;
        drawerNameTV = nav_view.getHeaderView(0).findViewById<TextView>(R.id.drawer_name_tv)
        drawerNameTV.text = User.firstName.capitalize() + " " + User.lastName.capitalize();


        val drawerMailTV = nav_view.getHeaderView(0).findViewById<TextView>(R.id.drawer_mail_tv);
        if (drawerMailTV != null) {
            drawerMailTV.text = User.email;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true;
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
                supportFragmentManager.beginTransaction().replace(fragment_container.id, (ScheduleFragment())).commit()
            }
            nav_settings->{
                supportFragmentManager.beginTransaction().replace(fragment_container.id,(SettingsFragment())).commit();
            }

            nav_logout -> {
                firebaseAuth.signOut();
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent);
                finish();
            }
        }

        drawer_layout.closeDrawer(Gravity.START)
        return true;
    }
}



