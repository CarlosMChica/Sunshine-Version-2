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

import android.os.Bundle;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.data.domain.Forecast;
import com.example.android.clean_sunshine.app.data.local.LocalGateway;
import com.example.android.clean_sunshine.app.data.remote.RemoteGateway;
import com.example.android.clean_sunshine.app.ui.forecast.ForecastPresenter.ForecastView;
import com.example.android.clean_sunshine.app.ui.forecast.adapter.ForecastAdapter;
import java.util.List;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements ForecastView {

  @Bind(R.id.recycler) RecyclerView recyclerView;

  private EasyRecyclerAdapter adapter;
  private ForecastPresenter presenter;
  private ForecastFragmentCallback callback;

  public ForecastFragment() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LocalGateway localGateway = new LocalGateway(getContext());
    RemoteGateway remoteGateway = new RemoteGateway(getContext());

    presenter = new ForecastPresenter(localGateway, remoteGateway, this);
    // Add this line in order for this fragment to handle menu events.
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
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //        if (id == R.id.action_refresh) {
    //            updateWeather();
    //            return true;
    //        }
    if (id == R.id.action_map) {
      openPreferredLocationInMap();
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

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init();
    // If there's instance state, mine it for useful information.
    // The end-goal here is that the user never knows that turning their device sideways
    // does crazy lifecycle related things.  It should feel like some stuff stretched out,
    // or magically appeared to take advantage of room, but data or place in the app was never
    // actually *lost*.
    //if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
    //  // The listview probably hasn't even been populated yet.  Actually perform the
    //  // swapout in onLoadFinished.
    //  mPosition = savedInstanceState.getInt(SELECTED_KEY);
    //}

    //mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
  }

  @Override public void updateForecast(final List<Forecast> localData) {
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        adapter.addAll(localData);
      }
    });
  }

  private void init() {
    initUi();
    presenter.onUiReady();
  }

  private void initUi() {
    adapter = new ForecastAdapter(getContext());
    adapter.setOnClickListener(new EasyViewHolder.OnItemClickListener() {
      @Override public void onItemClick(int position, View viewWe) {
        Forecast forecast = (Forecast) adapter.get(position);
        callback.onItemSelected(forecast.getId());
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        //Cursor cursor = (Cursor) adapter.get(position);
        //if (cursor != null) {
        //  String locationSetting = Utility.getPreferredLocation(getActivity());
        //  ((Callback) getActivity()).onItemSelected(
        //      WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting,
        //          cursor.getLong(COL_WEATHER_DATE)));
        //}
      }
    });
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  // since we read the location when we create the loader, all we need to do is restart things
  public void onLocationChanged() {
    presenter.onLocationChanged();
  }

  private void openPreferredLocationInMap() {
    // Using the URI scheme for showing a location found on a map.  This super-handy
    // intent can is detailed in the "Common Intents" page of Android's developer site:
    // http://developer.android.com/guide/components/intents-common.html#Maps

    //if (null != mForecastAdapter) {
    //  Cursor c = mForecastAdapter.getCursor();
    //  if (null != c) {
    //    c.moveToPosition(0);
    //    String posLat = c.getString(COL_COORD_LAT);
    //    String posLong = c.getString(COL_COORD_LONG);
    //    Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
    //
    //    Intent intent = new Intent(Intent.ACTION_VIEW);
    //    intent.setData(geoLocation);
    //
    //    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
    //      startActivity(intent);
    //    } else {
    //      Log.d(LOG_TAG,
    //          "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
    //    }
    //  }
    //}
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

  public interface ForecastFragmentCallback {

    void onItemSelected(int weatherId);
  }
}
