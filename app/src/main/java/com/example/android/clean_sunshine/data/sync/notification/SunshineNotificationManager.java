package com.example.android.clean_sunshine.data.sync.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import com.example.android.clean_sunshine.domain.interactor.LoadTodayForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.LoadTodayForecastInteractor.LoadTodayForecastInteractorOutput;
import com.example.android.clean_sunshine.domain.model.Forecast;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.android.clean_sunshine.app.Utility.isShowNotificationSettingEnabled;
import static com.example.android.clean_sunshine.app.Utility.wasLastTimeSyncBeforeToday;

public class SunshineNotificationManager implements LoadTodayForecastInteractorOutput {

  private static final int WEATHER_NOTIFICATION_ID = 3004;

  private final Context context;
  private final LoadTodayForecastInteractor todayForecastInteractor;
  private final NotificationBuilder notificationBuilder;
  private final NotificationManager notificationManager;

  public SunshineNotificationManager(Context context,
      LoadTodayForecastInteractor todayForecastInteractor,
      NotificationBuilder notificationBuilder) {
    this.context = context;
    this.todayForecastInteractor = todayForecastInteractor;
    this.notificationBuilder = notificationBuilder;
    this.notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
  }

  @Override public void onTodayForecastLoaded(Forecast todayForecast) {
    showTodayForecastNotification(todayForecast);
  }

  public void notifyWeather() {
    if (hasToShowNotification()) {
      todayForecastInteractor.setOutput(this);
      todayForecastInteractor.run();
    }
  }

  private void showTodayForecastNotification(Forecast todayForecast) {
    NotificationInfo notificationInfo = new NotificationInfo(context, todayForecast);
    showNotification(notificationBuilder.build(notificationInfo));
  }

  private boolean hasToShowNotification() {
    return wasLastTimeSyncBeforeToday(context) && isShowNotificationSettingEnabled(context);
  }

  private void showNotification(Notification notification) {
    notificationManager.notify(WEATHER_NOTIFICATION_ID, notification);
  }
}
