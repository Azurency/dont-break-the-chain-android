package fr.lassiergedeon.dontbreakthechain;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Antoine on 08/04/2015.
 */
public class AlarmManagerHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, TaskListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri ringtoneUri = Uri.parse(intent.getStringExtra("ringtone"));
        String taskTitle = intent.getStringExtra("task_title");
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskTitle).setContentText(context.getString(R.string.notificationText)).setAutoCancel(true)
                .setSound(ringtoneUri).setWhen(when).setContentIntent(pendingIntent);
        notificationManager.notify((int) when, mNotificationBuilder.build());
    }
}
