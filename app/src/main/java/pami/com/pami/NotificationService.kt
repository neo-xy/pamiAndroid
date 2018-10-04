package pami.com.pami

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent


class NotificationService : FirebaseMessagingService() {

    val CHANNEL_NAME = "PAMI"
    val CHANNEL_ID = "pami.com.pami"
    var message = ""
    var messageIndex = 8
    var title = ""
    override fun onMessageReceived(p0: RemoteMessage) {

        Log.d("pawell", p0.data.get("msg") + " " + p0.data.get("messageIndex"))
        if (p0.data.get("messageIndex") == null) {

            title = p0.data.get("title") as String
            val timeArray = p0.data.get("timeStemps")!!.split(",")
            message = ""
            val df = SimpleDateFormat("dd MMM", Locale("sv", "SE"))

            timeArray.toString()

            timeArray.forEach {
                val stemp: Long = it.toLong()
                Log.d("pawell", "stemp" + stemp.toString())
                message = message + (df.format(Date(stemp))) + ", "
            }
            messageIndex = p0.data.get("msg")!!.toInt()
        } else {
            message = p0.data.get("msg") as String
//            messageIndex = 7
            title = p0.data.get("title") as String
            messageIndex = p0.data.get("messageIndex")!!.toInt()
        }

        createChannel()
        val intent = Intent(this, LoginActivity::class.java);
        val pintent = PendingIntent.getActivity(this, 0,
                intent, 0)


        val newMessageNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_dallero)
                .setGroup("schedule")
                .setGroupSummary(true)
                .setContentIntent(pintent)
                .build()

        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_dallero)
                .setContentText(message)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setGroup("schedule")
                .setContentIntent(pintent)

        val nm: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.notify(3, newMessageNotification)
        nm.notify(messageIndex, mBuilder.build())


        super.onMessageReceived(p0)

    }

    override fun onMessageSent(p0: String?) {

        super.onMessageSent(p0)
    }

    override fun onDeletedMessages() {

        super.onDeletedMessages()
    }

    override fun onSendError(p0: String?, p1: Exception?) {
        super.onSendError(p0, p1)

    }

    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("pawell", "createChanel")

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {

        const val CHANNEL_ID = "pami.com.pami"
        const val CHANNEL_NAME = "Sample Notification"
    }

}