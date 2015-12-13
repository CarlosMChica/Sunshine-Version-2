package com.example.android.clean_sunshine.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiForecastItem {

  private double pressure;
  private float humidity;
  @SerializedName("weather") private List<ApiWeather> weatherList;
  @SerializedName("temp") private ApiTemperature temperature;
  @SerializedName("speed") private double windSpeed;
  @SerializedName("deg") private double windDirection;

  public double getPressure() {
    return pressure;
  }

  public float getHumidity() {
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
