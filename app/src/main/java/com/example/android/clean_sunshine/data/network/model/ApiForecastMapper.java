package com.example.android.clean_sunshine.data.network.model;

import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.Location;
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
    ApiWeather apiWeather = item.getWeatherList().get(0);
    return new Forecast.Builder().id(apiWeather.getId())
        .dateTime(mapDate(itemIndex))
        .description(apiWeather.getDescription())
        .high(item.getTemperature().getMax())
        .low(item.getTemperature().getMin())
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
}
