package pami.com.pami


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import pami.com.pami.models.User


class SettingsFragment : Fragment() {
    lateinit var callbackManager: CallbackManager
    lateinit var unMerge: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        var tt = view.findViewById<ImageView>(R.id.dddd)
        Glide.with(this)
                .load(User.imgUrl)
                .into(tt)
        return view
    }

    private fun unlink() {

        FirebaseAuth.getInstance().currentUser!!.unlink("facebook.com").addOnCompleteListener {
        }
    }

    private fun mergeAccount(token: String) {

        val credentials = FacebookAuthProvider.getCredential(token)
        FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credentials).addOnCompleteListener {
            it.getResult()!!.user.providerData.forEach {
                if (it.providerId == "facebook.com") {
                    val link = "https://graph.facebook.com/${it.uid}/picture"
                    Log.d("pawell","link $link")
                    FirebaseController.saveImgUrl(link)
                }
            }

        }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


