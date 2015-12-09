package com.example.android.clean_sunshine.data.network;

import android.content.Context;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.data.network.model.ApiForecast;
import com.example.android.clean_sunshine.data.network.model.ApiForecastMapper;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.NetworkForecastGateway;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.clean_sunshine.app.BuildConfig.OPEN_WEATHER_MAP_API_KEY;

public class NetworkForecastGatewayImp implements NetworkForecastGateway {

  private static final String DAYS = String.valueOf("14");

  private final Context context;
  private final OpenWeatherApiClient apiClient;
  private final ApiForecastMapper apiForecastMapper = new ApiForecastMapper();

  public NetworkForecastGatewayImp(Context context, OpenWeatherApiClient apiClient) {
    this.context = context;
    this.apiClient = apiClient;
  }

  @Override public List<Forecast> refresh(String manualLocation) {
    return refresh(null, null);
  }

  @Override public List<Forecast> refresh(Double lat, Double lon) {
    try {
      String query = getQuery(lat, lon);
      ApiForecast apiForecast =
          apiClient.dailyForecast(OPEN_WEATHER_MAP_API_KEY, "metric", query, DAYS, lat, lon)
              .execute()
              .body();

      return apiForecastMapper.mapFromApi(apiForecast, query);
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private String getQuery(Double lat, Double lon) {
    if (lat == null || lon == null) {
      return Utility.getPreferredLocation(context);
    } else {
      return null;
    }
  }
}
