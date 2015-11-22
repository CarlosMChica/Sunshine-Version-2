package com.example.android.clean_sunshine.app.data.remote;

import com.google.gson.annotations.SerializedName;

public class ApiWeather {

  @SerializedName("main")
  private String description;
  private int id;

  public String getDescription() {
    return description;
  }

  public int getId() {
    return id;
  }
}
