package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import java.util.List;

public class RefreshWeekForecastInteractor implements Interactor {

  private NetworkForecastGateway networkForecastGateway;
  private LocalForecastGateway localForecastGateway;
  private RefreshForecastInteractorOutput output;

  public RefreshWeekForecastInteractor(LocalForecastGateway localForecastGateway, NetworkForecastGateway networkForecastGateway) {
    this.networkForecastGateway = networkForecastGateway;
    this.localForecastGateway = localForecastGateway;
  }
  public void setOutput(RefreshForecastInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    try {
      loadForecast();
    } catch (Exception e) {
      output.onRefreshForecastError();
    }
  }

  private void loadForecast() {
    List<Forecast> networkData = networkForecastGateway.refresh();
    output.onForecastRefreshed(networkData);
    localForecastGateway.update(networkData);
  }

  public interface RefreshForecastInteractorOutput {
    void onForecastRefreshed(List<Forecast> forecastList);

    void onRefreshForecastError();
  }
}
