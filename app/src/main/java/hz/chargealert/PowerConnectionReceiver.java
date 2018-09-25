package hz.chargealert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;

import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver {
    final String TAG = "hfs";

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharged = status == BatteryManager.BATTERY_STATUS_FULL;
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        Intent broadcastServiceIntent = new Intent(context, broadcastService.class);
        /*if (!isCharging && !isCharged) {
//            context.stopService(broadcastServiceIntent);
            Toast.makeText(context, "Charger not plugged in", Toast.LENGTH_LONG).show();
//            return;
        }*/
        SharedPreferences sharedPref = context.getSharedPreferences(
                "hz.chargealert.sharedPreferencesFile", Context.MODE_PRIVATE);
        boolean defaultValue = false;
        boolean buttonSetting = sharedPref.getBoolean("buttonSetting", defaultValue);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null)
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        if (buttonSetting &&  isCharged) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("buttonSetting", false);
            editor.apply();
            notificationChannel notification_Channel = new notificationChannel();
            notification_Channel.createNotificationChannel(context);
            Notification notification = new NotificationCompat.Builder(context, "273")
                    .setContentTitle("Battery Fully Charged")
                    .setContentText("Swipe to dismiss")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .setSound(alert)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
            notification.flags |= Notification.FLAG_INSISTENT;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(10, notification);
            context.stopService(broadcastServiceIntent);
        }
        Log.d("hfs", "onReceive: Status received: " + status);
    }

}