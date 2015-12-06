package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.domain.model.LocationProvider;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import java.util.List;

public class RefreshForecastInteractor
    implements Interactor, LocationProvider.LocationProviderListener {

  private final LocationProvider locationProvider;
  private final NetworkForecastGateway networkForecastGateway;
  private final LocalForecastGateway localForecastGateway;
  private RefreshForecastInteractorOutput output;
  private String location;

  public RefreshForecastInteractor(LocationProvider locationProvider,
      LocalForecastGateway localForecastGateway, NetworkForecastGateway networkForecastGateway) {
    this.locationProvider = locationProvider;
    this.networkForecastGateway = networkForecastGateway;
    this.localForecastGateway = localForecastGateway;
  }

  public void setOutput(RefreshForecastInteractorOutput output) {
    this.output = output;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override public void run() {
    if (location != null) {
      loadForecast(null, null);
    } else {
      locationProvider.requestCurrentLocation(this);
    }
  }

  @Override public void onLocationRetrieved(Location location) {
    loadForecast(location.getLat(), location.getLon());
  }

  @Override public void onRetrieveLocationError() {
    loadForecast(null, null);
  }

  private void loadForecast(final Double lat, final Double lon) {
    try {
      List<Forecast> networkData = networkForecastGateway.refresh(lat, lon);
      output.onForecastRefreshed(networkData);
      localForecastGateway.update(networkData);
    } catch (Exception e) {
      output.onRefreshForecastError();
    }
  }

  public interface RefreshForecastInteractorOutput {
    void onForecastRefreshed(List<Forecast> forecastList);

    void onRefreshForecastError();
  }
}
