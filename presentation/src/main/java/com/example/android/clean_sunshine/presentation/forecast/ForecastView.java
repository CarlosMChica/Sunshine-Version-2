package com.example.android.clean_sunshine.presentation.forecast;

import com.example.android.clean_sunshine.domain.model.Forecast;
import java.util.List;
import me.panavtec.threaddecoratedview.views.qualifiers.ThreadDecoratedView;

@ThreadDecoratedView public interface ForecastView {

  void updateForecast(List<Forecast> forecastList);

  void showRefreshForecastError();

  void showLoadForecastError();

  void showLoading();

  void hideLoading();

  void goToMapScreen(double lat, double lon);

}
