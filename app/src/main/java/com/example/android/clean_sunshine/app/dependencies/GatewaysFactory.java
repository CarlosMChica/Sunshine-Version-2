package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.api.NetworkForecastGatewayImp;
import com.example.android.clean_sunshine.api.OpenWeatherApiClient;
import com.example.android.clean_sunshine.app.BuildConfig;
import com.example.android.clean_sunshine.app.Utility;
import com.example.android.clean_sunshine.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.domain.model.NetworkForecastGateway;
import com.example.android.clean_sunshine.persistence.LocalForecastGatewayImp;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.example.android.clean_sunshine.app.BuildConfig.API_URL;
import static com.squareup.okhttp.logging.HttpLoggingInterceptor.Level.BODY;

public class GatewaysFactory {

  public static LocalForecastGateway makeLocalGateway(Context context) {
    return new LocalForecastGatewayImp(context, Utility.getPreferredLocation(context));
  }

  public static NetworkForecastGateway makeNetworkGateway(Context context) {
    return new NetworkForecastGatewayImp(makeApiClient(makeHttpClient()),
        Utility.getPreferredLocation(context), BuildConfig.OPEN_WEATHER_MAP_API_KEY);
  }

  private static OkHttpClient makeHttpClient() {
    OkHttpClient client = new OkHttpClient();
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(BODY);
    client.interceptors().add(interceptor);
    return client;
  }

  private static OpenWeatherApiClient makeApiClient(OkHttpClient client) {
    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .baseUrl(API_URL)
        .client(client)
        .build();
    return retrofit.create(OpenWeatherApiClient.class);
  }
}
