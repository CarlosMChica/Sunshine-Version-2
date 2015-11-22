package com.example.android.clean_sunshine.app.data.remote;

import android.content.Context;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.app.data.domain.Forecast;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.example.android.clean_sunshine.app.BuildConfig.API_URL;
import static com.example.android.clean_sunshine.app.BuildConfig.OPEN_WEATHER_MAP_API_KEY;

public class RemoteGateway {

  private final Context context;
  private final OpenWeatherApiClient apiClient;
  private final ApiForecastMapper apiForecastMapper = new ApiForecastMapper();

  public RemoteGateway(Context context) {
    this.context = context;

    OkHttpClient client = new OkHttpClient();
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    client.interceptors().add(interceptor);

    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .baseUrl(API_URL)
        .client(client)
        .build();
    apiClient = retrofit.create(OpenWeatherApiClient.class);
  }

  public List<Forecast> refresh() {
    String locationQuery = Utility.getPreferredLocation(context);
    try {
      ApiForecast apiForecast =
          apiClient.dailyForecast(OPEN_WEATHER_MAP_API_KEY, "metric", locationQuery,
              String.valueOf("14")).execute().body();
      return apiForecastMapper.mapFromApi(apiForecast, locationQuery);
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
