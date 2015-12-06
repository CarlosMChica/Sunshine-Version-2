package com.example.android.clean_sunshine.app.domain.model;

import java.util.List;

public interface LocalForecastGateway {
  Forecast loadToday();

  List<Forecast> load();

  void update(List<Forecast> forecastList);
}
