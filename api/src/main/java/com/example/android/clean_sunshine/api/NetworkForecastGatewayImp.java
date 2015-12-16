package com.example.android.clean_sunshine.api;

import com.example.android.clean_sunshine.api.model.ApiForecast;
import com.example.android.clean_sunshine.api.model.ApiForecastMapper;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.NetworkForecastGateway;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkForecastGatewayImp implements NetworkForecastGateway {

  private static final String DAYS = String.valueOf("14");

  private final OpenWeatherApiClient apiClient;
  private final String apiKey;
  private final ApiForecastMapper apiForecastMapper = new ApiForecastMapper();
  private final String preferredLocation;

  public NetworkForecastGatewayImp(OpenWeatherApiClient apiClient, String preferredLocation,
      String apiKey) {
    this.preferredLocation = preferredLocation;
    this.apiClient = apiClient;
    this.apiKey = apiKey;
  }

  @Override public List<Forecast> refresh(String manualLocation) {
    return refresh(null, null);
  }

  @Override public List<Forecast> refresh(Double lat, Double lon) {
    try {
      String query = getQuery(lat, lon);
      ApiForecast apiForecast =
          apiClient.dailyForecast(apiKey, "metric", query, DAYS, lat, lon).execute().body();

      return apiForecastMapper.mapFromApi(apiForecast, query);
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private String getQuery(Double lat, Double lon) {
    if (lat == null || lon == null) {
      return preferredLocation;
    } else {
      return null;
    }
  }
}
