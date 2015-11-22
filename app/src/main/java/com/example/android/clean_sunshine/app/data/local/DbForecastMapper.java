package com.example.android.clean_sunshine.app.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import com.example.android.clean_sunshine.app.data.domain.Forecast;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_DATE;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_DEGREES;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_HUMIDITY;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_LOC_KEY;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_PRESSURE;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_SHORT_DESC;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_WEATHER_ID;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.COLUMN_WIND_SPEED;

public class DbForecastMapper {

  public List<ContentValues> mapToDb(List<Forecast> forecastList) {
    List<ContentValues> contentValues = new ArrayList<>(forecastList.size());
    for (Forecast forecast : forecastList) {
      contentValues.add(mapItemToDb(forecast));
    }
    return contentValues;
  }

  private ContentValues mapItemToDb(Forecast forecast) {
    ContentValues contentValues = new ContentValues();

    //TODO deal with location
    contentValues.put(COLUMN_LOC_KEY, 0);
    contentValues.put(COLUMN_DATE, forecast.getDateTime());
    contentValues.put(COLUMN_HUMIDITY, forecast.getHumidity());
    contentValues.put(COLUMN_PRESSURE, forecast.getPressure());
    contentValues.put(COLUMN_WIND_SPEED, forecast.getWindSpeed());
    contentValues.put(COLUMN_DEGREES, forecast.getWindDirection());
    contentValues.put(COLUMN_MAX_TEMP, forecast.getHigh());
    contentValues.put(COLUMN_MIN_TEMP, forecast.getLow());
    contentValues.put(COLUMN_SHORT_DESC, forecast.getDescription());
    contentValues.put(COLUMN_WEATHER_ID, forecast.getId());
    return contentValues;
  }

  public Forecast mapFromDb(Cursor cursor) {
    long dateTime = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
    double pressure = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRESSURE));
    double high = cursor.getDouble(cursor.getColumnIndex(COLUMN_MAX_TEMP));
    double low = cursor.getDouble(cursor.getColumnIndex(COLUMN_MIN_TEMP));
    double windSpeed = cursor.getDouble(cursor.getColumnIndex(COLUMN_WIND_SPEED));
    double windDirection = cursor.getDouble(cursor.getColumnIndex(COLUMN_DEGREES));
    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_WEATHER_ID));
    String description = cursor.getString(cursor.getColumnIndex(COLUMN_SHORT_DESC));
    return new Forecast.Builder().id(id)
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
