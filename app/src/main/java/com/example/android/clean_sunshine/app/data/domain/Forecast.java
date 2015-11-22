package com.example.android.clean_sunshine.app.data.domain;

public class Forecast {

  private int id;
  private String description;
  private long dateTime;
  private double pressure;
  private int humidity;
  private double windSpeed;
  private double windDirection;
  private double high;
  private double low;
  private Location location;

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public long getDateTime() {
    return dateTime;
  }

  public double getPressure() {
    return pressure;
  }

  public int getHumidity() {
    return humidity;
  }

  public double getWindSpeed() {
    return windSpeed;
  }

  public double getWindDirection() {
    return windDirection;
  }

  public double getHigh() {
    return high;
  }

  public double getLow() {
    return low;
  }

  public Location getLocation() {
    return location;
  }

  private Forecast(Builder builder) {
    this.id = builder.id;
    this.description = builder.description;
    this.dateTime = builder.dateTime;
    this.pressure = builder.pressure;
    this.humidity = builder.humidity;
    this.windSpeed = builder.windSpeed;
    this.windDirection = builder.windDirection;
    this.high = builder.high;
    this.low = builder.low;
    this.location = builder.location;
  }

  public static class Builder {
    private int id;
    private String description;
    private long dateTime;
    private double pressure;
    private int humidity;
    private double windSpeed;
    private double windDirection;
    private double high;
    private double low;
    private Location location;

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder dateTime(long dateTime) {
      this.dateTime = dateTime;
      return this;
    }

    public Builder pressure(double pressure) {
      this.pressure = pressure;
      return this;
    }

    public Builder humidity(int humidity) {
      this.humidity = humidity;
      return this;
    }

    public Builder windSpeed(double windSpeed) {
      this.windSpeed = windSpeed;
      return this;
    }

    public Builder windDirection(double windDirection) {
      this.windDirection = windDirection;
      return this;
    }

    public Builder high(double high) {
      this.high = high;
      return this;
    }

    public Builder low(double low) {
      this.low = low;
      return this;
    }

    public Builder location(Location location) {
      this.location = location;
      return this;
    }

    public Forecast build() {
      return new Forecast(this);
    }
  }
}
