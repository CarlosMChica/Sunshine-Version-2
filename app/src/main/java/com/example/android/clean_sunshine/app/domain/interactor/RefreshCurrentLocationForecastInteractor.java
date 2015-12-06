package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.domain.model.LocationProvider;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import java.util.List;

import static com.example.android.clean_sunshine.app.domain.model.LocationProvider.LocationProviderListener;

public class RefreshCurrentLocationForecastInteractor
    implements Interactor, LocationProviderListener {

  private final LocationProvider locationProvider;
  private final NetworkForecastGateway networkForecastGateway;
  private final LocalForecastGateway localForecastGateway;
  private RefreshCurrentLocationForecastInteractorOutput output;

  public RefreshCurrentLocationForecastInteractor(LocationProvider locationProvider,
      LocalForecastGateway localForecastGateway, NetworkForecastGateway networkForecastGateway) {
    this.locationProvider = locationProvider;
    this.networkForecastGateway = networkForecastGateway;
    this.localForecastGateway = localForecastGateway;
  }

  public void setOutput(RefreshCurrentLocationForecastInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    locationProvider.requestCurrentLocation(this);
  }

  @Override public void onLocationRetrieved(Location location) {
    loadForecast(location.getLat(), location.getLon());
  }

  @Override public void onRetrieveLocationError() {
    output.onRefreshCurrentLocationForecastError();
  }

  private void loadForecast(final Double lat, final Double lon) {
    try {
      List<Forecast> networkData = networkForecastGateway.refresh(lat, lon);
      output.onCurrentLocationForecastRefreshed(networkData);
      localForecastGateway.update(networkData);
    } catch (Exception e) {
      output.onRefreshCurrentLocationForecastError();
    }
  }

  public interface RefreshCurrentLocationForecastInteractorOutput {
    void onCurrentLocationForecastRefreshed(List<Forecast> forecastList);

    void onRefreshCurrentLocationForecastError();
  }
}
