package com.example.android.clean_sunshine.data.sync.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.domain.model.Forecast;

import static com.example.android.clean_sunshine.app.Utility.formatTemperature;
import static com.example.android.clean_sunshine.app.Utility.getArtResourceForWeatherCondition;
import static com.example.android.clean_sunshine.app.Utility.getIconResourceForWeatherCondition;

public class NotificationInfo {

  private final Forecast todayForecast;
  private Context context;

  public NotificationInfo(Context context, Forecast todayForecast) {
    this.context = context;
    this.todayForecast = todayForecast;
  }

  public String getContentText() {
    String description = todayForecast.getDescription();
    String highTemp = formatTemperature(context, todayForecast.getHigh());
    String lowTemp = formatTemperature(context, todayForecast.getLow());
    return context.getString(R.string.format_notification, description, highTemp, lowTemp);
  }

  public int getIconId() {
    return getIconResourceForWeatherCondition(todayForecast.getId());
  }

  public Bitmap getLargeIcon() {
    return BitmapFactory.decodeResource(context.getResources(),
        getArtResourceForWeatherCondition(todayForecast.getId()));
  }

  public String getTitle() {
    return context.getString(R.string.app_name);
  }
}
