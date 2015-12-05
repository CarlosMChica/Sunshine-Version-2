package com.example.android.clean_sunshine.app.data.network.model;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.Location;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApiForecastMapper {

  public List<Forecast> mapFromApi(ApiForecast apiForecast, String locationQuery) {
    List<ApiForecastItem> items = apiForecast.getItems();
    List<Forecast> list = new ArrayList<>(items.size());
    for (int i = 0; i < items.size(); i++) {
      list.add(mapForecastItem(i, items.get(i), apiForecast.getCity(), locationQuery));
    }
    return list;
  }

  private Forecast mapForecastItem(int itemIndex, ApiForecastItem item, ApiCity city,
      String locationQuery) {
    return new Forecast.Builder().id(mapId(item))
        .dateTime(mapDate(itemIndex))
        .description(mapDescription(item))
        .high(mapMaxTemperature(item.getTemperature()))
        .low(mapMinTemperature(item.getTemperature()))
        .humidity(item.getHumidity())
        .pressure(item.getPressure())
        .location(mapLocation(city, locationQuery))
        .windDirection(item.getWindDirection())
        .windSpeed(item.getWindSpeed())
        .build();
  }

  private long mapDate(int itemIndex) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, itemIndex);
    return calendar.getTimeInMillis();
  }

  private Location mapLocation(ApiCity apiCity, String locationQuery) {
    return new Location.Builder().cityName(apiCity.getName())
        .lat(apiCity.getCoord().getLat())
        .lon(apiCity.getCoord().getLon())
        .locationSetting(locationQuery)
        .build();
  }

  private double mapMinTemperature(ApiTemperature apiTemperature) {
    return apiTemperature.getMin();
  }

  private double mapMaxTemperature(ApiTemperature apiTemperature) {
    return apiTemperature.getMax();
  }

  private String mapDescription(ApiForecastItem item) {
    return item.getWeatherList().get(0).getDescription();
  }

  private int mapId(ApiForecastItem item) {
    return item.getWeatherList().get(0).getId();
  }
}
