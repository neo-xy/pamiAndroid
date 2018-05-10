package pami.com.pami

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastREciver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("pawell","reciverrrr")


        val service = Intent(context, NotificationService::class.java)
        service.putExtra("reason", intent.getStringExtra("reason"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))

        context.startService(service)
    }
}