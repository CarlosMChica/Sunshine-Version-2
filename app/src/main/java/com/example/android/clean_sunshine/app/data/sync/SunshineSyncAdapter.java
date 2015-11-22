package com.example.android.clean_sunshine.app.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.example.android.clean_sunshine.app.ui.forecast.MainActivity;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.data.local.LocalGateway;
import com.example.android.clean_sunshine.app.data.remote.RemoteGateway;
import com.example.android.clean_sunshine.app.data.local.ForecastContract;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
  public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();
  // Interval at which to sync with the weather, in seconds.
  // 60 seconds (1 minute) * 180 = 3 hours
  public static final int SYNC_INTERVAL = 60 * 180;
  public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
  private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
  private static final int WEATHER_NOTIFICATION_ID = 3004;

  private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
      ForecastContract.WeatherEntry.COLUMN_WEATHER_ID, ForecastContract.WeatherEntry.COLUMN_MAX_TEMP,
      ForecastContract.WeatherEntry.COLUMN_MIN_TEMP, ForecastContract.WeatherEntry.COLUMN_SHORT_DESC
  };

  // these indices must match the projection
  private static final int INDEX_WEATHER_ID = 0;
  private static final int INDEX_MAX_TEMP = 1;
  private static final int INDEX_MIN_TEMP = 2;
  private static final int INDEX_SHORT_DESC = 3;

  private LocalGateway localGateway;
  private RemoteGateway remoteGateway;

  public SunshineSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    remoteGateway = new RemoteGateway(context);
    localGateway = new LocalGateway(context);
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    Log.d(LOG_TAG, "Starting sync");
    localGateway.update(remoteGateway.refresh());
    notifyWeather();
  }

  private void notifyWeather() {
    Context context = getContext();
    //checking the last update and notify if it' the first of the day
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
    boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
        Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

    if (displayNotifications) {

      String lastNotificationKey = context.getString(R.string.pref_last_notification);
      long lastSync = prefs.getLong(lastNotificationKey, 0);

      if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
        // Last sync was more than 1 day ago, let's send a notification with the weather.
        String locationQuery = Utility.getPreferredLocation(context);

        Uri weatherUri = ForecastContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery,
            System.currentTimeMillis());

        // we'll query our contentProvider, as always
        Cursor cursor = context.getContentResolver()
            .query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

        if (cursor.moveToFirst()) {
          int weatherId = cursor.getInt(INDEX_WEATHER_ID);
          double high = cursor.getDouble(INDEX_MAX_TEMP);
          double low = cursor.getDouble(INDEX_MIN_TEMP);
          String desc = cursor.getString(INDEX_SHORT_DESC);

          int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
          Resources resources = context.getResources();
          Bitmap largeIcon = BitmapFactory.decodeResource(resources,
              Utility.getArtResourceForWeatherCondition(weatherId));
          String title = context.getString(R.string.app_name);

          // Define the text of the forecast.
          String contentText = String.format(context.getString(R.string.format_notification), desc,
              Utility.formatTemperature(context, high), Utility.formatTemperature(context, low));

          // NotificationCompatBuilder is a very convenient way to build backward-compatible
          // notifications.  Just throw in some data.
          NotificationCompat.Builder mBuilder =
              new NotificationCompat.Builder(getContext()).setColor(
                  resources.getColor(R.color.sunshine_light_blue))
                  .setSmallIcon(iconId)
                  .setLargeIcon(largeIcon)
                  .setContentTitle(title)
                  .setContentText(contentText);

          // Make something interesting happen when the user clicks on the notification.
          // In this case, opening the app is sufficient.
          Intent resultIntent = new Intent(context, MainActivity.class);

          // The stack builder object will contain an artificial back stack for the
          // started Activity.
          // This ensures that navigating backward from the Activity leads out of
          // your application to the Home screen.
          TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
          stackBuilder.addNextIntent(resultIntent);
          PendingIntent resultPendingIntent =
              stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
          mBuilder.setContentIntent(resultPendingIntent);

          NotificationManager mNotificationManager =
              (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
          // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
          mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

          //refreshing last sync
          SharedPreferences.Editor editor = prefs.edit();
          editor.putLong(lastNotificationKey, System.currentTimeMillis());
          editor.commit();
        }
        cursor.close();
      }
    }
  }

  /**
   * Helper method to handle insertion of a new location in the weather database.
   *
   * @param locationSetting The location string used to request updates from the server.
   * @param cityName A human-readable city name, e.g "Mountain View"
   * @param lat the latitude of the city
   * @param lon the longitude of the city
   * @return the row ID of the added location.
   */
  long addLocation(String locationSetting, String cityName, double lat, double lon) {
    long locationId;

    // First, check if the location with this city name exists in the db
    Cursor locationCursor = getContext().getContentResolver()
        .query(ForecastContract.LocationEntry.CONTENT_URI,
            new String[] {ForecastContract.LocationEntry._ID},
            ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
            new String[] {locationSetting}, null);

    if (locationCursor.moveToFirst()) {
      int locationIdIndex = locationCursor.getColumnIndex(ForecastContract.LocationEntry._ID);
      locationId = locationCursor.getLong(locationIdIndex);
    } else {
      // Now that the content provider is set up, inserting rows of data is pretty simple.
      // First create a ContentValues object to hold the data you want to insert.
      ContentValues locationValues = new ContentValues();

      // Then add the data, along with the corresponding name of the data type,
      // so the content provider knows what kind of value is being inserted.
      locationValues.put(ForecastContract.LocationEntry.COLUMN_CITY_NAME, cityName);
      locationValues.put(ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
      locationValues.put(ForecastContract.LocationEntry.COLUMN_COORD_LAT, lat);
      locationValues.put(ForecastContract.LocationEntry.COLUMN_COORD_LONG, lon);

      // Finally, insert location data into the database.
      Uri insertedUri = getContext().getContentResolver()
          .insert(ForecastContract.LocationEntry.CONTENT_URI, locationValues);

      // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
      locationId = ContentUris.parseId(insertedUri);
    }

    locationCursor.close();
    // Wait, that worked?  Yes!
    return locationId;
  }

  /**
   * Helper method to schedule the sync adapter periodic execution
   */
  public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
    Account account = getSyncAccount(context);
    String authority = context.getString(R.string.application_id);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // we can enable inexact timers in our periodic sync
      SyncRequest request = new SyncRequest.Builder().
          syncPeriodic(syncInterval, flexTime).
          setSyncAdapter(account, authority).
          setExtras(new Bundle()).build();
      ContentResolver.requestSync(request);
    } else {
      ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
    }
  }

  /**
   * Helper method to have the sync adapter sync immediately
   *
   * @param context The context used to access the account service
   */
  public static void syncImmediately(Context context) {
    Bundle bundle = new Bundle();
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    ContentResolver.requestSync(getSyncAccount(context),
        context.getString(R.string.application_id), bundle);
  }

  /**
   * Helper method to get the fake account to be used with SyncAdapter, or make a new one
   * if the fake account doesn't exist yet.  If we make a new account, we call the
   * onAccountCreated method so we can initialize things.
   *
   * @param context The context used to access the account service
   * @return a fake account.
   */
  public static Account getSyncAccount(Context context) {
    // Get an instance of the Android account manager
    AccountManager accountManager =
        (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

    // Create the account type and default account
    Account newAccount = new Account(context.getString(R.string.app_name),
        context.getString(R.string.sync_account_type));

    // If the password doesn't exist, the account doesn't exist
    if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
      if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
        return null;
      }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

      onAccountCreated(newAccount, context);
    }
    return newAccount;
  }

  private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
    SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
    ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.application_id),
        true);

        /*
         * Finally, let's do a sync to get things started
         */
    syncImmediately(context);
  }

  public static void initializeSyncAdapter(Context context) {
    getSyncAccount(context);
  }
}