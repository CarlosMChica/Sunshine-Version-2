package com.example.android.clean_sunshine.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiForecast {

  private ApiCity city;
  @SerializedName("list") private List<ApiForecastItem> items;

  public ApiCity getCity() {
    return city;
  }

  public List<ApiForecastItem> getItems() {
    return items;
  }
}
