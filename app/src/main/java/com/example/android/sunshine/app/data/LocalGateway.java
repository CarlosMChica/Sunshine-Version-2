package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class LocalGateway {

  private Context context;

  public LocalGateway(Context context) {
    this.context = context;
  }

  public void update(List<Weather> weatherList) {
    List<ContentValues> contentValues = new ArrayList<>(weatherList.size());

    for (Weather weather : weatherList) {

      ContentValues weatherValues = new ContentValues();

      //TODO deal with location
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, 0);
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, weather.getDateTime());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, weather.getHumidity());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, weather.getPressure());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, weather.getWindSpeed());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, weather.getWindDirection());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, weather.getHigh());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, weather.getLow());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, weather.getDescription());
      weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather.getId());

      contentValues.add(weatherValues);

      // TODO delete old data so we don't build up an endless history
      //context.getContentResolver()
      //    .delete(WeatherContract.WeatherEntry.CONTENT_URI,
      //        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
      //        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay - 1))});
      //notifyWeather();
    }
    ContentValues[] values = contentValues.toArray(new ContentValues[contentValues.size()]);
    context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, values);
  }
}
