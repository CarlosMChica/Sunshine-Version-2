package com.example.android.clean_sunshine.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.data.sync.notification.NotificationBuilder;
import com.example.android.clean_sunshine.data.sync.notification.SunshineNotificationManager;

import static com.example.android.clean_sunshine.dependencies.InteractorFactory.makeLoadTodayForecastInteractor;

public class SunshineNotificationManagerFactory {

  public static SunshineNotificationManager make(Context context) {
    return new SunshineNotificationManager(context, makeLoadTodayForecastInteractor(context),
        new NotificationBuilder(context));
  }
}
