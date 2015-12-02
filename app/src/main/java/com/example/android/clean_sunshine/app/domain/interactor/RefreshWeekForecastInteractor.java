package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkGateway;
import java.util.List;

public class RefreshWeekForecastInteractor implements Interactor {

  private NetworkGateway networkGateway;
  private LocalGateway localGateway;
  private RefreshForecastInteractorOutput output;

  public RefreshWeekForecastInteractor(LocalGateway localGateway, NetworkGateway networkGateway) {
    this.networkGateway = networkGateway;
    this.localGateway = localGateway;
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
    List<Forecast> networkData = networkGateway.refresh();
    output.onForecastRefreshed(networkData);
    localGateway.update(networkData);
  }

  public interface RefreshForecastInteractorOutput {
    void onForecastRefreshed(List<Forecast> forecastList);

    void onRefreshForecastError();
  }
}
