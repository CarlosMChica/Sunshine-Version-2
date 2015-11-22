package com.example.android.clean_sunshine.app.ui.forecast;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyRecyclerAdapter;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.data.domain.Forecast;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends EasyRecyclerAdapter {

  public static final int TODAY_VIEW_TYPE = 1;
  public static final int NOT_TODAY_VIEW_TYPE = 0;

  public ForecastAdapter(Context context) {
    super(context);
  }

  @Override public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TODAY_VIEW_TYPE) {
      return new ForecastTodayViewHolder(parent.getContext(), parent);
    } else {
      return new ForecastViewHolder(parent.getContext(), parent);
    }
  }

  @Override public int getItemViewType(int position) {
    Forecast forecast = (Forecast) get(position);
    boolean isToday = Utility.isToday(forecast.getDateTime());
    return isToday ? TODAY_VIEW_TYPE : NOT_TODAY_VIEW_TYPE;
  }

}