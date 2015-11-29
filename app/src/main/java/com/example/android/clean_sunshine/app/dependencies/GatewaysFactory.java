package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.app.data.local.LocalGatewayImp;
import com.example.android.clean_sunshine.app.data.network.NetworkGatewayImp;
import com.example.android.clean_sunshine.app.data.network.OpenWeatherApiClient;
import com.example.android.clean_sunshine.app.domain.model.LocalGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkGateway;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.example.android.clean_sunshine.app.BuildConfig.API_URL;
import static com.squareup.okhttp.logging.HttpLoggingInterceptor.Level.BODY;

public class GatewaysFactory {

  public static LocalGateway makeLocalGateway(Context context) {
    return new LocalGatewayImp(context);
  }

  public static NetworkGateway makeNetworkGateway(Context context) {
    return new NetworkGatewayImp(context, makeApiClient(makeHttpClient()));
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
