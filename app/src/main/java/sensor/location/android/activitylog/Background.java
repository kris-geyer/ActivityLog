package sensor.location.android.activitylog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class Background extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenReceiver, screenFilter);

        if (Build.VERSION.SDK_INT >= 26) {
            if(Build.VERSION.SDK_INT > 26){
                String CHANNEL_ONE_ID = "sensor.example. geyerk1.inspect.screenservice";
                String CHANNEL_ONE_NAME = "Screen service";
                NotificationChannel notificationChannel = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                            CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setShowBadge(true);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.createNotificationChannel(notificationChannel);
                    }
                }

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.background_running);
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setChannelId(CHANNEL_ONE_ID)
                        .setContentTitle("Recording data")
                        .setContentText("ActivityLog is logging data")
                        .setSmallIcon(R.drawable.background_running)
                        .setLargeIcon(icon)
                        .build();

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                startForeground(101, notification);
            }
            else{
                startForeground(101, updateNotification());

            }
        }else{
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.activity_log_icon);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Activity logger")
                    .setContentText("data recording on going")
                    .setSmallIcon(R.drawable.background_running)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
        storeInternally("Background operations initiated");
        return START_STICKY;
    }


    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .setContentTitle("Activity log")
                .setTicker("Ticker")
                .setContentText("recording of data is on going")
                .setSmallIcon(R.drawable.activity_log_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = "";
            switch (intent.getAction()){
                case Intent.ACTION_SCREEN_ON:
                    result = "Screen on";
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    result = "Screen off";
                    break;
            }
            storeInternally(result);
        }
    };

    private void storeInternally(String result) {
        long timestamp = System.currentTimeMillis()/1000;
        String dataEntry = result + " - " + timestamp + ": ";
        try {
            File path = this.getFilesDir();
            File file = new File(path, "data.txt");
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(dataEntry.getBytes());
            fos.close();
            Log.i("from screen service", dataEntry);
        } catch (Exception e) {
            Log.d("Main - issue writing", "Exception: " + e);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        storeInternally("activity Log background operations destroyed");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, Beric.class));
        }else{
            startService(new Intent(this, Beric.class));
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        storeInternally("activity Log all apps running in background were destroyed");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, Beric.class));
        }else{
            startService(new Intent(this, Beric.class));
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        storeInternally("Excessive amount of apps open, trim occurred");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, Beric.class));
        }else{
            startService(new Intent(this, Beric.class));
        }
        super.onTrimMemory(level);
    }

}
