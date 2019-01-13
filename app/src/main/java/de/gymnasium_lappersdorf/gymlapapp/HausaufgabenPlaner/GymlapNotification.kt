package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat

import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * Created by Lukas S | 03.12.2017
 */

class GymlapNotification
/**
 * @param context     - basic context to create notification
 * @param text        - text to be displayed in the notification
 * @param resultClass - class to be executed on notification click
 * @param mUniqueId   - must be unique to identify the notification
 */
(context: Context, text: String, resultClass: Class<*>?, private val mUniqueId: Int) {

    private val mNotificationManager: NotificationManager?
    private val mNotification: Notification

    init {

        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager?.createNotificationChannel(sNotificationChannel!!)
        }

        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Gymlap Erinnerung")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        if (resultClass != null) {
            val resultIntent = Intent(context, resultClass)
            val resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            mBuilder.setContentIntent(resultPendingIntent)
        }

        mNotification = mBuilder.build()
    }


    fun show() {
        mNotificationManager?.notify(mUniqueId, mNotification)
    }

    fun cancel() {
        mNotificationManager!!.cancel(mUniqueId)
    }

    companion object {
        private val CHANNEL_ID = "default_channel"
        private var sNotificationChannel: NotificationChannel? = null

        //One time initialization of the notification channel
        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sNotificationChannel = NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_HIGH)
            }
        }
    }

}
