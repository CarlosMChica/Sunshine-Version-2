package com.example.android.clean_sunshine.app.ui.forecast.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.domain.model.Forecast;

public class ForecastTodayViewHolder extends ForecastViewHolder {

  @Bind(R.id.city_textview) TextView cityTextview;

  public ForecastTodayViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_forecast_today);
  }

  @Override public void bindTo(Forecast item) {
    super.bindTo(item);
    cityTextview.setText(item.getLocation().getCityName());
  }

  @Override protected int getIconImageRes(Forecast item) {
    return Utility.getArtResourceForWeatherCondition(item.getId());
  }
}
