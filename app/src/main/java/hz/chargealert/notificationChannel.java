package hz.chargealert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

public class notificationChannel {
    protected void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Default Channel
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("273", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Charging channel
            /*CharSequence name2 = context.getString(R.string.channel_name2);
            String description2 = context.getString(R.string.channel_description2);
            int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel2 = new NotificationChannel("274", name2, importance2);
            channel2.setDescription(description2);
            notificationManager.createNotificationChannel(channel2);*/

            CharSequence name3 = context.getString(R.string.channel_name3);
            int importance3 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel3 = new NotificationChannel("275", name3, importance3);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}
