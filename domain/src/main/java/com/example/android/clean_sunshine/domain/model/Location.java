package com.example.android.clean_sunshine.domain.model;

public class Location {

  private String locationSetting;
  private String cityName;
  private double lat;
  private double lon;

  public String getLocationSetting() {
    return locationSetting;
  }

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
    this.locationSetting = builder.locationSetting;
    this.cityName = builder.cityName;
    this.lat = builder.lat;
    this.lon = builder.lon;
  }

  public static class Builder {
    private String locationSetting;
    private String cityName;
    private double lat;
    private double lon;

    public Builder locationSetting(String locationSetting) {
      this.locationSetting = locationSetting;
      return this;
    }

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

