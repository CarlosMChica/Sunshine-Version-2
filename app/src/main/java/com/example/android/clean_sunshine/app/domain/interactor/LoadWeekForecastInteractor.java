package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkGateway;
import java.util.List;

public class LoadWeekForecastInteractor implements Interactor {

  private LocalGateway localGateway;
  private NetworkGateway networkGateway;
  private LoadForecastInteractorOutput output;

  public LoadWeekForecastInteractor(LocalGateway localGateway, NetworkGateway networkGateway) {
    this.localGateway = localGateway;
    this.networkGateway = networkGateway;
  }

  public void setOutput(LoadForecastInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    try {
      loadForecast();
    } catch (Exception e) {
      output.onLoadForecastError();
    }
  }

  private void loadForecast() {
    final List<Forecast> forecastList = localGateway.load();
    if (forecastList.isEmpty()) {
      output.onForecastLoaded(networkGateway.refresh());
    } else {
      output.onForecastLoaded(forecastList);
    }
  }

  public interface LoadForecastInteractorOutput {
    void onForecastLoaded(List<Forecast> forecastList);

    void onLoadForecastError();
  }
}