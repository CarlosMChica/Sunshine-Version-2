package com.example.android.clean_sunshine.app.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalGateway;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry;
import static com.example.android.clean_sunshine.app.data.local.ForecastContract.WeatherEntry.CONTENT_URI;

public class LocalGatewayImp implements LocalGateway {

  private DbForecastMapper mapper = new DbForecastMapper();
  private ContentResolver contentResolver;

  public LocalGatewayImp(Context context) {
    contentResolver = context.getContentResolver();
  }

  @Override public Forecast loadToday() {
    return load().get(0);
  }

  @Override public List<Forecast> load() {
    List<Forecast> forecastList = new ArrayList<>();
    Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
    if (cursor != null) {
      while (cursor.moveToNext()) {
        forecastList.add(mapper.mapFromDb(cursor));
      }
    }
    return forecastList;
  }

  @Override public void update(List<Forecast> forecastList) {
    List<ContentValues> contentValues = mapper.mapToDb(forecastList);
    ContentValues[] values = contentValues.toArray(new ContentValues[contentValues.size()]);
    contentResolver.bulkInsert(CONTENT_URI, values);
    purge();
  }

  private void purge() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date yesterday = calendar.getTime();
    contentResolver.delete(WeatherEntry.CONTENT_URI, WeatherEntry.COLUMN_DATE + " <= ?",
        new String[] {String.valueOf(yesterday.getTime())});
  }
}