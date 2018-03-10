package pami.com.pami

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*


private const val INTENT_USER_ID = "user_id"

class LoginActivity : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager;
    lateinit var sp: SharedPreferences
    lateinit var kk: Disposable;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_btn).setOnClickListener { logIn() }
        val loginView = findViewById<ConstraintLayout>(R.id.login_layout)
        val emailView = findViewById<EditText>(R.id.email_et)
        val splashView = findViewById<RelativeLayout>(R.id.splash_view)
        val bg = findViewById<ImageView>(R.id.bg)
        val forgetPasswordTv = findViewById<TextView>(R.id.forget_password_tv)
        this.sp = this.getPreferences(android.content.Context.MODE_PRIVATE)


        Glide.with(this)
                .load(R.drawable.clock_bg)
                .into(bg)
        emailView.setText((this.sp.getString("mail", "")))
        forgetPasswordTv.setOnClickListener { openForgetPasswordDialog() }

        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser != null) {

                this.kk = FirebaseController.getUser().subscribe() {
                    if (it == true) {

                        this.sp.edit().putString("mail", User.email).commit()
                        FirebaseController.setUpDepartments()
                        FirebaseController.setUpEmployees()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(INTENT_USER_ID, FirebaseAuth.getInstance().uid)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                splashView.visibility = View.GONE
                loginView.visibility = View.VISIBLE
                bg.visibility = View.VISIBLE
            }
        }
    }

    private fun openForgetPasswordDialog() {

        val builder = AlertDialog.Builder(this)
        val insertEmail = EditText(this)

        val container = LinearLayout(baseContext)

        val ll = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.setMargins(50, 80, 50, 50)


        insertEmail.layoutParams = ll
        insertEmail.hint = "Din e-post"

        container.addView(insertEmail)

        builder.setTitle("Återställ lösenord")
        builder.setView(container)
        builder.setPositiveButton("skicka e-post", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(insertEmail.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(baseContext, "E-post blev skickad", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(baseContext, "ett fel uppstog, kontrollera angivet e-post", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        builder.create().show()
    }

    fun logIn() {
        if (!email_et.text.isEmpty() && !password_et.text.isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email_et.text.toString(), password_et.text.toString()).addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                } else {
                    Toast.makeText(this, "Fel e-mail eller lösenord", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        kk.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
