package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;

public class LoadForecastByIdInteractor implements Interactor {

  private LocalForecastGateway localForecastGateway;
  private LoadForecastByIdInteractorOutput output;
  private int forecastId;

  public LoadForecastByIdInteractor(LocalForecastGateway localForecastGateway) {
    this.localForecastGateway = localForecastGateway;
  }

  public void setForecastId(int forecastId) {
    this.forecastId = forecastId;
  }

  public void setOutput(LoadForecastByIdInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    loadForecast();
  }

  private void loadForecast() {
    output.onForecastLoaded(localForecastGateway.loadById(forecastId));
  }

  public interface LoadForecastByIdInteractorOutput {
    void onForecastLoaded(Forecast forecast);
  }
}
