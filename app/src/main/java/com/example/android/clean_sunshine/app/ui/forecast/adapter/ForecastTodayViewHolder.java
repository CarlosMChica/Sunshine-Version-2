package com.example.android.clean_sunshine.app.ui.forecast.adapter;

import android.content.Context;
import android.view.ViewGroup;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.data.domain.Forecast;

public class ForecastTodayViewHolder extends ForecastViewHolder {

  public ForecastTodayViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_forecast_today);
  }

  @Override protected int getIconImageRes(Forecast item) {
    return Utility.getArtResourceForWeatherCondition(item.getId());
  }
}
