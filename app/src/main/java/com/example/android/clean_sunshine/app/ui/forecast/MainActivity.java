/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.clean_sunshine.app.ui.forecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.ui.detail.DetailActivity;
import com.example.android.clean_sunshine.app.ui.detail.DetailFragment;
import com.example.android.clean_sunshine.app.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
    implements ForecastFragment.ForecastFragmentCallback {

  private static final String DETAIL_FRAGMENT_TAG = "DFTAG";

  private boolean twoPane;
  private String mLocation;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLocation = Utility.getPreferredLocation(this);
    setContentView(R.layout.activity_main);
    twoPane = findViewById(R.id.weather_detail_container) != null;
    if (twoPane) {
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
            .commit();
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override protected void onResume() {
    super.onResume();
    String location = Utility.getPreferredLocation(this);
    // update the location in our second pane using the fragment manager
    if (location != null && !location.equals(mLocation)) {
      ForecastFragment ff =
          (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
      if (null != ff) {
        ff.onLocationChanged(location);
      }
      DetailFragment df =
          (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
      if (null != df) {
        df.onLocationChanged(location);
      }
      mLocation = location;
    }
  }

  @Override public void onItemSelected(int weatherId) {
    if (twoPane) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.weather_detail_container, DetailFragment.newInstance(weatherId),
              DETAIL_FRAGMENT_TAG)
          .commit();
    } else {
      DetailActivity.startActivity(this, weatherId);
    }
  }
}
