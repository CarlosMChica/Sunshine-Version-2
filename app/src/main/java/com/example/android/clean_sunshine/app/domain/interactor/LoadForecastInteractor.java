package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.app.domain.model.NetworkForecastGateway;
import java.util.List;

public class LoadForecastInteractor implements Interactor {

  private LocalForecastGateway localForecastGateway;
  private NetworkForecastGateway networkForecastGateway;
  private LoadForecastInteractorOutput output;

  public LoadForecastInteractor(LocalForecastGateway localForecastGateway,
      NetworkForecastGateway networkForecastGateway) {
    this.localForecastGateway = localForecastGateway;
    this.networkForecastGateway = networkForecastGateway;
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
    List<Forecast> forecastList = localForecastGateway.load();
    if (forecastList.isEmpty()) {
      forecastList = networkForecastGateway.refresh();
      output.onForecastLoaded(forecastList);
      localForecastGateway.update(forecastList);
    } else {
      output.onForecastLoaded(forecastList);
    }
  }

  public interface LoadForecastInteractorOutput {
    void onForecastLoaded(List<Forecast> forecastList);

    void onLoadForecastError();
  }
}
