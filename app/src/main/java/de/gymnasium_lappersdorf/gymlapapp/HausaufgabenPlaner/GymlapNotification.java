package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * Created by Lukas S | 03.12.2017
 */

public class GymlapNotification {
    private final static String CHANNEL_ID = "default_channel";
    private static NotificationChannel sNotificationChannel;

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private int mUniqueId;

    //One time initialization of the notification channel
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sNotificationChannel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
        }
    }

    /**
     * @param context     - basic context to create notification
     * @param text        - text to be displayed in the notification
     * @param resultClass - class to be executed on notification click
     * @param id          - must be unique to identify the notification
     */
    public GymlapNotification(Context context, String text, @Nullable Class<?> resultClass, int id) {
        mUniqueId = id;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (mNotificationManager != null)
                mNotificationManager.createNotificationChannel(sNotificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Gymlap Erinnerung")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        if (resultClass != null) {
            Intent resultIntent = new Intent(context, resultClass);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotification = mBuilder.build();
    }


    public void show() {
        if (mNotificationManager != null)
            mNotificationManager.notify(mUniqueId, mNotification);
    }

    public void cancel() {
        mNotificationManager.cancel(mUniqueId);
    }

}
