package com.example.android.clean_sunshine.app.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiForecastItem {

  private double pressure;
  private int humidity;
  @SerializedName("weather") private List<ApiWeather> weatherList;
  @SerializedName("temp") private ApiTemperature temperature;
  @SerializedName("speed") private double windSpeed;
  @SerializedName("deg") private double windDirection;

  public double getPressure() {
    return pressure;
  }

  public int getHumidity() {
    return humidity;
  }

  public List<ApiWeather> getWeatherList() {
    return weatherList;
  }

  public ApiTemperature getTemperature() {
    return temperature;
  }

  public double getWindSpeed() {
    return windSpeed;
  }

  public double getWindDirection() {
    return windDirection;
  }
}
