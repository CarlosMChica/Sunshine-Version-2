package com.example.android.clean_sunshine.app.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.domain.interactor.LoadTodayForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshWeekForecastInteractor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.ui.forecast.MainActivity;
import java.util.List;

import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadTodayForecastInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter
    implements LoadTodayForecastInteractor.LoadTodayForecastInteractorOutput,
    RefreshWeekForecastInteractor.RefreshForecastInteractorOutput {

  public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();

  private static final int SYNC_INTERVAL = 60 * 180;
  private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
  private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
  private static final int WEATHER_NOTIFICATION_ID = 3004;

  private LoadTodayForecastInteractor todayForecastInteractor;
  private RefreshWeekForecastInteractor refreshWeekForecastInteractor;
  private SharedPreferences prefs;

  public SunshineSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    refreshWeekForecastInteractor = makeRefreshForecastInteractor(context);
    todayForecastInteractor = makeLoadTodayForecastInteractor(context);
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    Log.d(LOG_TAG, "Starting sync");
    updateData();
    notifyWeather();
  }

  @Override public void onForecastRefreshed(List<Forecast> forecastList) {
    notifyWeather();
  }

  @Override public void onRefreshForecastError() {
    //DO NOTHING
  }

  @Override public void onTodayForecastLoaded(Forecast todayForecast) {
    showTodayForecastNotification(todayForecast);
  }

  private void updateData() {
    refreshWeekForecastInteractor.setOutput(this);
    refreshWeekForecastInteractor.run();
  }

  private void notifyWeather() {
    if (hasToShowNotification()) {
      todayForecastInteractor.setOutput(this);
      todayForecastInteractor.run();
    }
  }

  private void showTodayForecastNotification(Forecast todayForecast) {
    int iconId = Utility.getIconResourceForWeatherCondition(todayForecast.getId());
    Resources resources = getContext().getResources();
    Bitmap largeIcon = BitmapFactory.decodeResource(resources,
        Utility.getArtResourceForWeatherCondition(todayForecast.getId()));
    String title = getContext().getString(R.string.app_name);
    Notification notification =
        buildNotification(todayForecast, iconId, resources, largeIcon, title);
    showNotification(notification);
    refreshLastSync();
  }

  private boolean hasToShowNotification() {
    long lastSync =
        prefs.getLong(getContext().getString(R.string.pref_last_notification), 0);
    return wasLastTimeSyncBeforeToday(lastSync) && isShowNotificationSettingEnabled();
  }

  private boolean isShowNotificationSettingEnabled() {
    String displayNotificationsKey = getContext().getString(R.string.pref_enable_notifications_key);
    return prefs.getBoolean(displayNotificationsKey,
        Boolean.parseBoolean(getContext().getString(R.string.pref_enable_notifications_default)));
  }

  private boolean wasLastTimeSyncBeforeToday(long lastSync) {
    return System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS;
  }

  private void refreshLastSync() {
    String lastNotificationKey = getContext().getString(R.string.pref_last_notification);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putLong(lastNotificationKey, System.currentTimeMillis());
    editor.apply();
  }

  private void showNotification(Notification notification) {
    NotificationManager notificationManager =
        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(WEATHER_NOTIFICATION_ID, notification);
  }

  private Notification buildNotification(Forecast todayForecast, int iconId, Resources resources,
      Bitmap largeIcon, String title) {
    String description = todayForecast.getDescription();
    String highTemp = Utility.formatTemperature(getContext(), todayForecast.getHigh());
    String lowTemp = Utility.formatTemperature(getContext(), todayForecast.getLow());
    String contentText =
        getContext().getString(R.string.format_notification, description, highTemp, lowTemp);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext()).setColor(
        resources.getColor(R.color.sunshine_light_blue))
        .setSmallIcon(iconId)
        .setLargeIcon(largeIcon)
        .setContentTitle(title)
        .setContentText(contentText);

    Intent resultIntent = new Intent(getContext(), MainActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent =
        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    return builder.build();
  }

  /**
   * Helper method to schedule the sync adapter periodic execution
   */
  public static void configurePeriodicSync(Context context) {
    Account account = getSyncAccount(context);
    String authority = context.getString(R.string.application_id);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // we can enable inexact timers in our periodic sync
      SyncRequest request = new SyncRequest.Builder().
          syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME).
          setSyncAdapter(account, authority).
          setExtras(new Bundle()).build();
      ContentResolver.requestSync(request);
    } else {
      ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL);
    }
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
    SunshineSyncAdapter.configurePeriodicSync(context);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
    ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.application_id),
        true);
  }

  public static void initializeSyncAdapter(Context context) {
    getSyncAccount(context);
  }
}