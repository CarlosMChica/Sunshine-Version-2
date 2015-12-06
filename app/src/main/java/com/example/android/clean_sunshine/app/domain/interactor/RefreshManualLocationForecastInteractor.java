package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import java.util.List;

public class RefreshManualLocationForecastInteractor implements Interactor {

  private final NetworkForecastGateway networkForecastGateway;
  private final LocalForecastGateway localForecastGateway;
  private RefreshManualLocationForecastInteractorOutput output;
  private String location;

  public RefreshManualLocationForecastInteractor(LocalForecastGateway localForecastGateway,
      NetworkForecastGateway networkForecastGateway) {
    this.networkForecastGateway = networkForecastGateway;
    this.localForecastGateway = localForecastGateway;
  }

  public void setOutput(RefreshManualLocationForecastInteractorOutput output) {
    this.output = output;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override public void run() {
    loadForecast();
  }

  private void loadForecast() {
    try {
      List<Forecast> networkData = networkForecastGateway.refresh(location);
      output.onManualLocationForecastRefreshed(networkData);
      localForecastGateway.update(networkData);
    } catch (Exception e) {
      output.onRefreshManualLocationForecastError();
    }
  }

  public interface RefreshManualLocationForecastInteractorOutput {
    void onManualLocationForecastRefreshed(List<Forecast> forecastList);

    void onRefreshManualLocationForecastError();
  }
}
