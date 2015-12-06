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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.dependencies.PresenterFactory;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.presenter.forecast.CurrentLocationForecastPresenter;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastView;
import com.example.android.clean_sunshine.app.ui.forecast.adapter.ForecastAdapter;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder.OnItemClickListener;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements ForecastView {

  @Bind(R.id.recycler) RecyclerView recyclerView;
  @Bind(R.id.progressBar) ProgressBar progressBar;

  private EasyRecyclerAdapter adapter;
  private CurrentLocationForecastPresenter presenter;
  private ForecastFragmentCallback callback;

  public ForecastFragment() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = PresenterFactory.makeForecast(getActivity(), this);
    setHasOptionsMenu(true);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getActivity() instanceof ForecastFragmentCallback) {
      callback = (ForecastFragmentCallback) getActivity();
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.forecastfragment, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.action_refresh:
        presenter.onRefreshClick();
        return true;
      case R.id.action_map:
        presenter.onOpenMapOptionClick();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override public void onDestroy() {
    presenter.detachView();
    super.onDestroy();
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init();
  }

  @Override public void updateForecast(final List<Forecast> localData) {
    adapter.addAll(localData);
  }

  @Override public void showRefreshForecastError() {
    showError(R.string.refresh_forecast_error);
  }

  @Override public void showLoadForecastError() {
    showError(R.string.load_forecast_error);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(GONE);
  }

  @Override public void goToMapScreen(double lat, double lon) {
    Uri geoLocation = Uri.parse("geo:" + lat + "," + lon);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(geoLocation);
    startActivity(intent);
  }

  private void init() {
    initUi();
    presenter.onUiReady();
  }

  private void initUi() {
    adapter = new ForecastAdapter(getContext());
    adapter.setOnClickListener(new OnItemClickListener() {
      @Override public void onItemClick(int position, View viewWe) {
        Forecast forecast = (Forecast) adapter.get(position);
        callback.onItemSelected(forecast.getId());
      }
    });
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  public void onLocationChanged(String location) {
    presenter.onLocationChanged(location);
  }

  private void showError(@StringRes int error) {
    Toast.makeText(getActivity(), getString(error), Toast.LENGTH_SHORT).show();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    // When tablets rotate, the currently selected list item needs to be saved.
    // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
    // so check for that before storing.
    //if (mPosition != ListView.INVALID_POSITION) {
    //  outState.putInt(SELECTED_KEY, mPosition);
    //}
    //super.onSaveInstanceState(outState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  public interface ForecastFragmentCallback {

    void onItemSelected(int weatherId);
  }
}
