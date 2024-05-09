package com.praytimes.MobileProject.prayernotifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PrayTimeNotification extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_1";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve NotificationID from the intent
        int notificationID = intent.getIntExtra("NotificationID", 0);

        // Create Notification Channel (for API 26 and above)
        createNotificationChannel(context);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Prayer Time")
                .setContentText("Reminder for next prayer time")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());
    }

    // Create a Notification Channel (for API 26 and above)
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Notifications";
            String description = "Channel for prayer time notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
