package hz.chargealert;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class broadcastService extends IntentService {

    private final BroadcastReceiver br = new PowerConnectionReceiver();
    final String TAG = "hfs";
    
    public broadcastService() {
        super("broadcastService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this,0, notificationIntent, 0);
        notificationChannel notification_Channel = new notificationChannel();
        notification_Channel.createNotificationChannel(getApplicationContext());
        Notification mBuilder =  new NotificationCompat.Builder(this, "275")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Alert is set")
                .setContentText("It will sound when the battery is full. Click here to remove the alert.")
                .setAutoCancel(true)
                .setContentIntent(pendingNotificationIntent)
                .setOngoing(true)
                .build();
        IntentFilter filter = new IntentFilter(BatteryManager.EXTRA_STATUS);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br, filter);
        startForeground(15, mBuilder);
        Log.d(TAG, "onStartCommand: Service Started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true){}
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "onDestroy: Exception in broadcastService onDestroy: " + e);
        }
        Log.d(TAG, "onDestroy: service destroyed");
    }
}
