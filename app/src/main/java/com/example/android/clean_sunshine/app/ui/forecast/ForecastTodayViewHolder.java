package com.example.android.clean_sunshine.app.ui.forecast;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
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
