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
package com.example.android.clean_sunshine.app.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.domain.model.Forecast;

public class DetailActivity extends AppCompatActivity {

  private static final String DATE_TIME_INTENT_EXTRA = "DATE_TIME_INTENT_EXTRA";
  private static final String LOCATION_SETTING_INTENT_EXTRA = "LOCATION_SETTING_INTENT_EXTRA";

  public static void startActivity(Context context, Forecast forecast) {
    Intent intent = new Intent(context, DetailActivity.class);
    intent.putExtra(DATE_TIME_INTENT_EXTRA, forecast.getDateTime());
    intent.putExtra(LOCATION_SETTING_INTENT_EXTRA, forecast.getLocation().getLocationSetting());
    context.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    if (savedInstanceState == null) {
      long dateTime = getIntent().getLongExtra(DATE_TIME_INTENT_EXTRA, 0);
      String locationSetting = getIntent().getStringExtra(LOCATION_SETTING_INTENT_EXTRA);
      Fragment fragment = DetailFragment.newInstance(dateTime, locationSetting);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.weather_detail_container, fragment)
          .commit();
    }
  }
}