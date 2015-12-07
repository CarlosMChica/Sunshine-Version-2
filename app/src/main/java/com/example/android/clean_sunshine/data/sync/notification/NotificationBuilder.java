package com.example.android.clean_sunshine.data.sync.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.ui.forecast.MainActivity;

public class NotificationBuilder {

  private Context context;

  public NotificationBuilder(Context context) {
    this.context = context;
  }

  public Notification build(NotificationInfo notificationInfo) {
    return new NotificationCompat.Builder(context).setColor(
        ContextCompat.getColor(context, R.color.sunshine_light_blue))
        .setSmallIcon(notificationInfo.getIconId())
        .setLargeIcon(notificationInfo.getLargeIcon())
        .setContentTitle(notificationInfo.getTitle())
        .setContentText(notificationInfo.getContentText())
        .setContentIntent(buildPendingIntent())
        .build();
  }

  private PendingIntent buildPendingIntent() {
    Intent resultIntent = new Intent(context, MainActivity.class);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addNextIntent(resultIntent);
    return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
