package com.example.android.clean_sunshine.app.domain.model;

import java.util.List;

public interface NetworkForecastGateway {

  List<Forecast> refresh(String manualLocation);

  List<Forecast> refresh(Double lat, Double lon);
}
