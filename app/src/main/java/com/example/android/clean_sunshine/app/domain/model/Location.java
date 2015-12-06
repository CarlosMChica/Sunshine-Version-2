package com.example.android.clean_sunshine.app.domain.model;

public class Location {

  private String cityName;
  private double lat;
  private double lon;

  public String getCityName() {
    return cityName;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }

  private Location(Builder builder) {
    this.cityName = builder.cityName;
    this.lat = builder.lat;
    this.lon = builder.lon;
  }

  public static class Builder {
    private String cityName;
    private double lat;
    private double lon;

    public Builder cityName(String cityName) {
      this.cityName = cityName;
      return this;
    }

    public Builder lat(double lat) {
      this.lat = lat;
      return this;
    }

    public Builder lon(double lon) {
      this.lon = lon;
      return this;
    }

    public Location build() {
      return new Location(this);
    }
  }
}
