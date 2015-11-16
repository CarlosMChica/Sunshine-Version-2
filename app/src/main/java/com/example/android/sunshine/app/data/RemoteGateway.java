package com.example.android.sunshine.app.data;

import android.content.Context;
import android.net.Uri;
import android.text.format.Time;
import com.example.android.sunshine.app.BuildConfig;
import com.example.android.sunshine.app.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RemoteGateway {

  private Context context;

  public RemoteGateway(Context context) {
    this.context = context;
  }

  public List<Weather> refresh() {

    String locationQuery = Utility.getPreferredLocation(context);

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;

    String format = "json";
    String units = "metric";
    int numDays = 14;

    try {
      // Construct the URL for the OpenWeatherMap query
      // Possible parameters are avaiable at OWM's forecast API page, at
      // http://openweathermap.org/API#forecast
      final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
      final String QUERY_PARAM = "q";
      final String FORMAT_PARAM = "mode";
      final String UNITS_PARAM = "units";
      final String DAYS_PARAM = "cnt";
      final String APPID_PARAM = "APPID";

      Uri builtUri = Uri.parse(FORECAST_BASE_URL)
          .buildUpon()
          .appendQueryParameter(QUERY_PARAM, locationQuery)
          .appendQueryParameter(FORMAT_PARAM, format)
          .appendQueryParameter(UNITS_PARAM, units)
          .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
          .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
          .build();

      URL url = new URL(builtUri.toString());

      // Create the request to OpenWeatherMap, and open the connection
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // Read the input stream into a String
      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        // Nothing to do.
        return null;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // But it does make debugging a *lot* easier if you print out the completed
        // buffer for debugging.
        buffer.append(line + "\n");
      }

      if (buffer.length() == 0) {
        // Stream was empty.  No point in parsing.
        return null;
      }
      forecastJsonStr = buffer.toString();
      return parseResponse(forecastJsonStr, locationQuery);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
    return new ArrayList<>();
  }

  private List<Weather> parseResponse(String forecastJsonStr, String locationQuery)
      throws JSONException {
    // Now we have a String representing the complete forecast in JSON Format.
    // Fortunately parsing is easy:  constructor takes the JSON string and converts it
    // into an Object hierarchy for us.

    // These are the names of the JSON objects that need to be extracted.

    // Location information
    final String OWM_CITY = "city";
    final String OWM_CITY_NAME = "name";
    final String OWM_COORD = "coord";

    // Location coordinate
    final String OWM_LATITUDE = "lat";
    final String OWM_LONGITUDE = "lon";

    // Weather information.  Each day's forecast info is an element of the "list" array.
    final String OWM_LIST = "list";

    final String OWM_PRESSURE = "pressure";
    final String OWM_HUMIDITY = "humidity";
    final String OWM_WINDSPEED = "speed";
    final String OWM_WIND_DIRECTION = "deg";

    // All temperatures are children of the "temp" object.
    final String OWM_TEMPERATURE = "temp";
    final String OWM_MAX = "max";
    final String OWM_MIN = "min";

    final String OWM_WEATHER = "weather";
    final String OWM_DESCRIPTION = "main";
    final String OWM_WEATHER_ID = "id";

    //try {
    JSONObject forecastJson = new JSONObject(forecastJsonStr);
    JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

    JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
    String cityName = cityJson.getString(OWM_CITY_NAME);

    JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
    double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
    double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

    // Insert the new weather information into the database
    List<Weather> weatherList = new ArrayList<>(weatherArray.length());

    // OWM returns daily forecasts based upon the local time of the city that is being
    // asked for, which means that we need to know the GMT offset to translate this data
    // properly.

    // Since this data is also sent in-order and the first day is always the
    // current day, we're going to take advantage of that to get a nice
    // normalized UTC date for all of our weather.

    Time dayTime = new Time();
    dayTime.setToNow();

    // we start at the day returned by local time. Otherwise this is a mess.
    int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

    // now we work exclusively in UTC
    dayTime = new Time();

    for (int i = 0; i < weatherArray.length(); i++) {
      // These are the values that will be collected.
      long dateTime;
      double pressure;
      int humidity;
      double windSpeed;
      double windDirection;

      double high;
      double low;

      String description;
      int weatherId;

      // Get the JSON object representing the day
      JSONObject dayForecast = weatherArray.getJSONObject(i);

      // Cheating to convert this to UTC time, which is what we want anyhow
      dateTime = dayTime.setJulianDay(julianStartDay + i);

      pressure = dayForecast.getDouble(OWM_PRESSURE);
      humidity = dayForecast.getInt(OWM_HUMIDITY);
      windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
      windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

      // Description is in a child array called "weather", which is 1 element long.
      // That element also contains a weather code.
      JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
      description = weatherObject.getString(OWM_DESCRIPTION);
      weatherId = weatherObject.getInt(OWM_WEATHER_ID);

      // Temperatures are in a child object called "temp".  Try not to name variables
      // "temp" when working with temperature.  It confuses everybody.
      JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
      high = temperatureObject.getDouble(OWM_MAX);
      low = temperatureObject.getDouble(OWM_MIN);

      Location location = new Location.Builder().cityName(cityName)
          .lat(cityLatitude)
          .lon(cityLongitude)
          .locationSetting(locationQuery)
          .build();
      Weather weather = new Weather.Builder().id(weatherId)
          .description(description)
          .dateTime(dateTime)
          .humidity(humidity)
          .pressure(pressure)
          .windDirection(windDirection)
          .windSpeed(windSpeed)
          .high(high)
          .low(low)
          .location(location)
          .build();
      weatherList.add(weather);
    }
    return weatherList;
  }
}
