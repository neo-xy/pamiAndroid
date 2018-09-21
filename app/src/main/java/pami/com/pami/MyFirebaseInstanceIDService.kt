package pami.com.pami

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d("pawell", "refresh token " + token)


        val intent = Intent("tokenReceiver")
        val broadcastManager = LocalBroadcastManager.getInstance(this)
        intent.putExtra("token", token)
        broadcastManager.sendBroadcast(intent)

    }
}