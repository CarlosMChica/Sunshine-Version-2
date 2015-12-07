package com.example.android.clean_sunshine.domain.interactor;

import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.domain.model.NetworkForecastGateway;
import java.util.List;

public class LoadForecastInteractor implements Interactor {

  private LocalForecastGateway localForecastGateway;
  private NetworkForecastGateway networkForecastGateway;
  private LoadForecastInteractorOutput output;
  private String location;

  public LoadForecastInteractor(LocalForecastGateway localForecastGateway,
      NetworkForecastGateway networkForecastGateway) {
    this.localForecastGateway = localForecastGateway;
    this.networkForecastGateway = networkForecastGateway;
  }

  public void setOutput(LoadForecastInteractorOutput output) {
    this.output = output;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override public void run() {
    try {
      loadForecast();
    } catch (Exception e) {
      e.printStackTrace();
      output.onLoadForecastError();
    }
  }

  private void loadForecast() {
    List<Forecast> forecastList = localForecastGateway.load();
    if (forecastList.isEmpty()) {
      forecastList = networkForecastGateway.refresh(location);
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
