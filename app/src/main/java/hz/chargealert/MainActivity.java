package hz.chargealert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final private BroadcastReceiver powerStateReceiver = new PowerStateReceiver();
    final String TAG = "hfs";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean isCharging = intent.getBooleanExtra("isCharging", false);
            boolean isCharged = intent.getBooleanExtra("isCharged", false);
            float batteryPct = (intent.getFloatExtra("batteryPct", 0)) * 100;
            TextView textView = findViewById(R.id.main_text);
            if (isCharging) {
                textView.setText("Battery Status: Charging\nCurrent Battery Percentage: " + batteryPct + "\n");
            }else if (isCharged) {
                textView.setText("Battery Status: Charged\nCurrent Battery Percentage: " + batteryPct + "\n");
            }else {
                textView.setText("Battery Status: Discharging\nCurrent Battery Percentage: " + batteryPct + "\n");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;
        final Button button = findViewById(R.id.set_alert);
        createNotificationChannel();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        boolean defaultValue = false;
        boolean buttonSetting = sharedPref.getBoolean(getString(R.string.buttonSetting), defaultValue);
        final Intent startbroadcastServiceIntent = new Intent(this, broadcastService.class);
        IntentFilter filter = new IntentFilter(BatteryManager.EXTRA_STATUS);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(powerStateReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("batteryInformationLocalBroadcast"));
        if (buttonSetting) {
            button.setText(R.string.alertSet);
        } else {
            button.setText(R.string.alertNotSet);
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
                boolean defaultValue = false;
                boolean buttonSetting = sharedPref.getBoolean(getString(R.string.buttonSetting), defaultValue);
                if (buttonSetting) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.buttonSetting), false);
                    editor.apply();
                    button.setText(R.string.alertNotSet);
                    Toast.makeText(context,"Alarm Removed", Toast.LENGTH_SHORT).show();
                    stopService(startbroadcastServiceIntent);
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.buttonSetting), true);
                    editor.apply();
                    button.setText(R.string.alertSet);
                    Toast.makeText(context,"Alarm Set", Toast.LENGTH_LONG).show();
                    if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(startbroadcastServiceIntent);
                    else
                        startService(startbroadcastServiceIntent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(powerStateReceiver);
        }catch (IllegalArgumentException e) {
            Log.d(TAG, "onDestroy: Exception in uregistering receiver: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Context context = this;
        final Button button = findViewById(R.id.set_alert);
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        boolean defaultValue = false;
        boolean buttonSetting = sharedPref.getBoolean(getString(R.string.buttonSetting), defaultValue);
        IntentFilter filter = new IntentFilter(BatteryManager.EXTRA_STATUS);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(powerStateReceiver, filter);
        if (buttonSetting) {
            button.setText(R.string.alertSet);
        } else {
            button.setText(R.string.alertNotSet);
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        try {
            unregisterReceiver(powerStateReceiver);
        }catch (IllegalArgumentException e) {
            Log.d(TAG, "onDestroy: Exception in uregistering receiver: " + e);
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Default Channel
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("273", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Charging channel
            CharSequence name2 = getString(R.string.channel_name2);
            String description2 = getString(R.string.channel_description2);
            int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel2 = new NotificationChannel("274", name2, importance2);
            channel2.setDescription(description2);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel2);

        }
    }
}
