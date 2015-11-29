package com.example.android.clean_sunshine.app.domain.model;

import java.util.List;

public interface LocalGateway {
  Forecast loadToday();

  List<Forecast> load();

  void update(List<Forecast> forecastList);
}
