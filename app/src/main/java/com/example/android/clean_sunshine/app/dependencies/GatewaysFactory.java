package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.app.data.local.LocalForecastGatewayImp;
import com.example.android.clean_sunshine.app.data.network.NetworkForecastGatewayImp;
import com.example.android.clean_sunshine.app.data.network.OpenWeatherApiClient;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.example.android.clean_sunshine.app.BuildConfig.API_URL;
import static com.squareup.okhttp.logging.HttpLoggingInterceptor.Level.BODY;

public class GatewaysFactory {

  public static LocalForecastGateway makeLocalGateway(Context context) {
    return new LocalForecastGatewayImp(context);
  }

  public static NetworkForecastGateway makeNetworkGateway(Context context) {
    return new NetworkForecastGatewayImp(context, makeApiClient(makeHttpClient()));
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
