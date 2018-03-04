package pami.com.pami

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


private const val INTENT_USER_ID = "user_id"

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var loginView = findViewById<ConstraintLayout>(R.id.login_layout)
        var splashView = findViewById<ImageView>(R.id.splash_view)
        FirebaseAuth.getInstance().addAuthStateListener {

            if (it.currentUser != null) {

                FirebaseController.getUser().subscribe() {
                    if (it == true) {

                        FirebaseController.setUpDepartments();
                        FirebaseController.setUpEmployees();
                        val intent = Intent(this, MainActivity::class.java);
                        intent.putExtra(INTENT_USER_ID, FirebaseAuth.getInstance().uid);
                        startActivity(intent);
                        finish();
                    }

                }
            }else{
                splashView.visibility = View.GONE
                loginView.visibility =View.VISIBLE
            }


        }
    }

    fun logIn(v: View) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email_et.text.toString(), password_et.text.toString()).addOnCompleteListener() { task ->
            if (task.isSuccessful) {


            } else {
                Toast.makeText(this, "Fel e-mail eller lösenord", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
