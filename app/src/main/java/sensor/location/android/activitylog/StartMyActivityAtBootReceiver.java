package sensor.location.android.activitylog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class StartMyActivityAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT < 26) {
                Intent activityIntent = new Intent(context, Background.class);
                context.startService(activityIntent);
                Intent documentIntent = new Intent(context, whenRestart.class);
                context.startService(documentIntent);
            }
            else{
                Intent activityIntent = new Intent(context, Background.class);
                context.startForegroundService(activityIntent);
                Intent documentIntent = new Intent(context, whenRestart.class);
                context.startForegroundService(documentIntent);
            }

        }
    }
}
