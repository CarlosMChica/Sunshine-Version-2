package com.example.android.clean_sunshine.app.ui.forecast.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.carlosdelachica.easyrecycleradapters.adapter.EasyViewHolder;
import com.example.android.clean_sunshine.app.R;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.domain.model.Forecast;

public class ForecastViewHolder extends EasyViewHolder<Forecast> {

  @Bind(R.id.icon_imageview) ImageView iconImageView;
  @Bind(R.id.date_textview) TextView dateTextView;
  @Bind(R.id.forecast_textview) TextView forecastTextView;
  @Bind(R.id.high_textview) TextView highTextView;
  @Bind(R.id.low_textview) TextView lowTextView;

  private final Context context;

  public ForecastViewHolder(Context context, ViewGroup parent) {
    this(context, parent, R.layout.list_item_forecast);
  }

  public ForecastViewHolder(Context context, ViewGroup parent, int layoutRes) {
    super(context, parent, layoutRes);
    this.context = context;
    ButterKnife.bind(this, itemView);
  }

  @Override public void bindTo(Forecast item) {
    dateTextView.setText(Utility.getFriendlyDayString(context, item.getDateTime()));
    forecastTextView.setText(item.getDescription());
    bindTemperatures(item);
    bindIcon(item);
  }

  private void bindTemperatures(Forecast item) {
    highTextView.setText(Utility.formatTemperature(context, item.getHigh()));
    lowTextView.setText(Utility.formatTemperature(context, item.getLow()));
  }

  private void bindIcon(Forecast item) {
    iconImageView.setContentDescription(item.getDescription());
    iconImageView.setImageResource(getIconImageRes(item));
  }

  protected @DrawableRes int getIconImageRes(Forecast item) {
    return Utility.getIconResourceForWeatherCondition(item.getId());
  }
}