package com.example.android.clean_sunshine.app.data.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.data.local.ForecastContract.LocationEntry;
import com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.Location;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.getInstance;

public class LocalForecastGatewayImp implements LocalForecastGateway {

  private final String preferredLocation;
  private final DbForecastMapper mapper = new DbForecastMapper();
  private final ContentResolver contentResolver;

  public LocalForecastGatewayImp(Context context) {
    preferredLocation = Utility.getPreferredLocation(context);
    contentResolver = context.getContentResolver();
  }

  @Override public Forecast loadToday() {
    return load().get(0);
  }

  @Override public List<Forecast> load() {
    List<Forecast> forecastList = new ArrayList<>();
    Cursor cursor =
        contentResolver.query(WeatherEntry.buildWeatherLocation(preferredLocation), null, null,
            null, null);
    if (cursor != null) {
      while (cursor.moveToNext()) {
        forecastList.add(mapper.mapFromDb(cursor));
      }
      cursor.close();
    }
    return forecastList;
  }

  @Override public void update(List<Forecast> forecastList) {
    if (!forecastList.isEmpty()) {
      long locationId = updateLocation(forecastList.get(0).getLocation());
      updateForecasts(forecastList, locationId);
      purge();
    }
  }

  private void updateForecasts(List<Forecast> forecastList, long locationId) {
    List<ContentValues> contentValues = mapper.mapToDb(forecastList, locationId);
    ContentValues[] values = contentValues.toArray(new ContentValues[contentValues.size()]);
    contentResolver.bulkInsert(WeatherEntry.CONTENT_URI, values);
  }

  private long updateLocation(Location location) {
    contentResolver.delete(LocationEntry.CONTENT_URI, null, null);
    ContentValues contentValues = mapper.mapLocationToDb(location);
    Uri insertedUri = contentResolver.insert(LocationEntry.CONTENT_URI, contentValues);
    return ContentUris.parseId(insertedUri);
  }

  private void purge() {
    Calendar calendar = getInstance();
    calendar.add(DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();
    contentResolver.delete(WeatherEntry.CONTENT_URI, WeatherEntry.COLUMN_DATE + " <= ?",
        new String[] {valueOf(yesterday.getTime())});
  }
}
