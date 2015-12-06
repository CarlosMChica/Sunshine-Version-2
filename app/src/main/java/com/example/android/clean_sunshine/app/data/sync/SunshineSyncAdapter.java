package com.example.android.clean_sunshine.app.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.data.sync.notification.SunshineNotificationManager;
import com.example.android.clean_sunshine.app.dependencies.SunshineNotificationManagerFactory;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshCurrentLocationForecastInteractor.RefreshCurrentLocationForecastInteractorOutput;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import java.util.List;

import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter
    implements RefreshCurrentLocationForecastInteractorOutput {

  private static final int SYNC_INTERVAL = 60 * 180;
  private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

  private final RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor;
  private final SunshineNotificationManager notificationManager;

  public SunshineSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    refreshCurrentLocationForecastInteractor = makeRefreshForecastInteractor(context);
    notificationManager = SunshineNotificationManagerFactory.make(context);
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    updateData();
  }

  @Override public void onCurrentLocationForecastRefreshed(List<Forecast> forecastList) {
    notificationManager.notifyWeather();
    refreshLastSync();
  }

  @Override public void onRefreshCurrentLocationForecastError() {
    //DO NOTHING
  }

  private void updateData() {
    refreshCurrentLocationForecastInteractor.setOutput(this);
    refreshCurrentLocationForecastInteractor.run();
  }

  private void refreshLastSync() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    String lastNotificationKey = getContext().getString(R.string.pref_last_notification);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putLong(lastNotificationKey, System.currentTimeMillis());
    editor.apply();
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
   * Helper method to get the fake account to be used with SyncAdapter, or makeForecast a new one
   * if the fake account doesn't exist yet.  If we makeForecast a new account, we call the
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