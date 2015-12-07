package com.example.android.clean_sunshine.data.network;

import com.example.android.clean_sunshine.data.network.model.ApiForecast;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface OpenWeatherApiClient {

  String QUERY_PARAM = "q";
  String UNITS_PARAM = "units";
  String DAYS_PARAM = "cnt";
  String API_KEY_PARAM = "APPID";
  String LON_PARAM = "lon";
  String LAT_PARAM = "lat";

  @GET("forecast/daily") Call<ApiForecast> dailyForecast(@Query(API_KEY_PARAM) String apiKey,
      @Query(UNITS_PARAM) String units, @Query(QUERY_PARAM) String query,
      @Query(DAYS_PARAM) String days, @Query(LAT_PARAM) Double lat, @Query(LON_PARAM) Double lon);
}
