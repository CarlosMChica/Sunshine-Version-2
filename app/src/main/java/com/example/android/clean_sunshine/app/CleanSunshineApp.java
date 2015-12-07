package com.example.android.clean_sunshine.app;

import android.app.Application;
import com.example.android.clean_sunshine.data.sync.SunshineSyncAdapter;
import com.karumi.dexter.Dexter;

public class CleanSunshineApp extends Application {

  @Override public void onCreate() {
    super.onCreate();
    initializeApp();
  }

  private void initializeApp() {
    initializeDexter();
    initializeSyncAdapter();
  }

  private void initializeDexter() {
    Dexter.initialize(this);
  }

  private void initializeSyncAdapter() {
    SunshineSyncAdapter.initializeSyncAdapter(this);
  }
}
