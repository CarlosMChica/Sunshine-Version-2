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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.presentation.detail.DetailPresenter;
import com.example.android.clean_sunshine.presentation.detail.DetailView;

import static com.example.android.clean_sunshine.app.dependencies.PresenterFactory.makeDetail;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements DetailView {

  private static final String DATE_TIME = "DetailFragment.DATE_TIME";
  private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
  private static final String LOCATION_SETTING = "DetailFragment.LOCATION_SETTING";

  @Bind(R.id.detail_icon) ImageView mIconView;
  @Bind(R.id.detail_day_textview) TextView mFriendlyDateView;
  @Bind(R.id.detail_date_textview) TextView mDateView;
  @Bind(R.id.detail_forecast_textview) TextView mDescriptionView;
  @Bind(R.id.detail_high_textview) TextView mHighTempView;
  @Bind(R.id.detail_low_textview) TextView mLowTempView;
  @Bind(R.id.detail_humidity_textview) TextView mHumidityView;
  @Bind(R.id.detail_wind_textview) TextView mWindView;
  @Bind(R.id.detail_pressure_textview) TextView mPressureView;

  private long dateTime;
  private DetailPresenter presenter;
  private String locationSetting;

  public DetailFragment() {
  }

  public static Fragment newInstance(long dateTime, String locationSetting) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putLong(DATE_TIME, dateTime);
    args.putString(LOCATION_SETTING, locationSetting);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = makeDetail(getActivity(), this);
    setHasOptionsMenu(true);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    getArgments();
    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    presenter.setForecastData(dateTime, locationSetting);
    presenter.onUiReady();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.detailfragment, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_share) {
      presenter.onShareClicked();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void showFriendDate(long dateTime) {
    mFriendlyDateView.setText(Utility.getDayName(getActivity(), dateTime));
  }

  @Override public void showIcon(int id) {
    mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(id));
  }

  @Override public void showDate(long dateTime) {
    mDateView.setText(Utility.getFormattedMonthDay(getActivity(), dateTime));
  }

  @Override public void showDescription(String description) {
    mDescriptionView.setText(description);
    mIconView.setContentDescription(description);
  }

  @Override public void showHighTemp(double high) {
    mHighTempView.setText(Utility.formatTemperature(getActivity(), high));
  }

  @Override public void showLowTemp(double low) {
    mLowTempView.setText(Utility.formatTemperature(getActivity(), low));
  }

  @Override public void showHumidity(int humidity) {
    mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
  }

  @Override public void showWind(float windDirection, float windSpeed) {
    mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDirection));
  }

  @Override public void showPressure(double pressure) {
    mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
  }

  @Override public void showShareOptions(String shareText) {
    startActivity(buildChooserIntent(shareText));
  }

  @Override public void onDestroy() {
    presenter.detachView();
    super.onDestroy();
  }

  private Intent buildChooserIntent(String shareText) {
    Intent chooser = Intent.createChooser(createShareForecastIntent(shareText), null);
    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return chooser;
  }

  private Intent createShareForecastIntent(String shareText) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText + FORECAST_SHARE_HASHTAG);
    return shareIntent;
  }

  private void getArgments() {
    Bundle arguments = getArguments();
    if (arguments != null) {
      dateTime = arguments.getLong(DATE_TIME);
      locationSetting = arguments.getString(LOCATION_SETTING);
    }
  }
}