package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_DATE;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_DEGREES;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_HUMIDITY;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_LOC_KEY;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_PRESSURE;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_SHORT_DESC;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_WIND_SPEED;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.CONTENT_URI;

public class LocalGateway {

  private Context context;

  public LocalGateway(Context context) {
    this.context = context;
  }

  public List<Weather> load() {
    List<Weather> weatherList = new ArrayList<>();
    Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
    if (cursor != null) {
      while (cursor.moveToNext()) {
        weatherList.add(mapWeatherFromDb(cursor));
      }
    }
    return weatherList;
  }

  public void update(List<Weather> weatherList) {
    List<ContentValues> contentValues = new ArrayList<>(weatherList.size());
    for (Weather weather : weatherList) {
      contentValues.add(mapWeatherToDb(weather));
    }
    ContentValues[] values = contentValues.toArray(new ContentValues[contentValues.size()]);
    context.getContentResolver().bulkInsert(CONTENT_URI, values);
    purge();
  }

  private void purge() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();
    context.getContentResolver()
        .delete(WeatherContract.WeatherEntry.CONTENT_URI,
            WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
            new String[] {String.valueOf(yesterday.getTime())});
  }

  @NonNull private ContentValues mapWeatherToDb(Weather weather) {
    ContentValues weatherValues = new ContentValues();

    //TODO deal with location
    weatherValues.put(COLUMN_LOC_KEY, 0);
    weatherValues.put(COLUMN_DATE, weather.getDateTime());
    weatherValues.put(COLUMN_HUMIDITY, weather.getHumidity());
    weatherValues.put(COLUMN_PRESSURE, weather.getPressure());
    weatherValues.put(COLUMN_WIND_SPEED, weather.getWindSpeed());
    weatherValues.put(COLUMN_DEGREES, weather.getWindDirection());
    weatherValues.put(COLUMN_MAX_TEMP, weather.getHigh());
    weatherValues.put(COLUMN_MIN_TEMP, weather.getLow());
    weatherValues.put(COLUMN_SHORT_DESC, weather.getDescription());
    weatherValues.put(COLUMN_WEATHER_ID, weather.getId());
    return weatherValues;
  }

  private Weather mapWeatherFromDb(Cursor cursor) {
    long dateTime = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
    double pressure = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRESSURE));
    double high = cursor.getDouble(cursor.getColumnIndex(COLUMN_MAX_TEMP));
    double low = cursor.getDouble(cursor.getColumnIndex(COLUMN_MIN_TEMP));
    double windSpeed = cursor.getDouble(cursor.getColumnIndex(COLUMN_WIND_SPEED));
    double windDirection = cursor.getDouble(cursor.getColumnIndex(COLUMN_DEGREES));
    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_WEATHER_ID));
    String description = cursor.getString(cursor.getColumnIndex(COLUMN_SHORT_DESC));
    return new Weather.Builder().id(id)
        .description(description)
        .dateTime(dateTime)
        .windDirection(windDirection)
        .windSpeed(windSpeed)
        .pressure(pressure)
        .high(high)
        .low(low)
        .build();
  }
}
