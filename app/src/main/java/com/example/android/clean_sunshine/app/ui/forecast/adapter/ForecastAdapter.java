package com.example.android.clean_sunshine.app.ui.forecast.adapter;

import android.content.Context;
import android.view.ViewGroup;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.domain.model.Forecast;

public class ForecastAdapter extends EasyRecyclerAdapter {

  public static final int TODAY_VIEW_TYPE = 1;
  public static final int NOT_TODAY_VIEW_TYPE = 0;

  public ForecastAdapter(Context context) {
    super(context);
  }

  @Override public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    EasyViewHolder viewHolder = instantiateViewHolder(parent, viewType);
    bindListeners(viewHolder);
    return viewHolder;
  }

  @Override public int getItemViewType(int position) {
    Forecast forecast = (Forecast) get(position);
    boolean isToday = Utility.isToday(forecast.getDateTime());
    return isToday ? TODAY_VIEW_TYPE : NOT_TODAY_VIEW_TYPE;
  }

  private EasyViewHolder instantiateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TODAY_VIEW_TYPE) {
      return new ForecastTodayViewHolder(parent.getContext(), parent);
    } else {
      return new ForecastViewHolder(parent.getContext(), parent);
    }
  }
}