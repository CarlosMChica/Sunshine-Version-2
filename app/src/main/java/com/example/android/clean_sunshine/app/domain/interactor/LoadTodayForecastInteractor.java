package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalForecastGateway;

public class LoadTodayForecastInteractor implements Interactor {

  private LocalForecastGateway localForecastGateway;
  private LoadTodayForecastInteractorOutput output;

  public LoadTodayForecastInteractor(LocalForecastGateway localForecastGateway) {
    this.localForecastGateway = localForecastGateway;
  }

  public void setOutput(LoadTodayForecastInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    loadTodayForecast();
  }

  private void loadTodayForecast() {
    output.onTodayForecastLoaded(localForecastGateway.loadToday());
  }

  public interface LoadTodayForecastInteractorOutput {
    void onTodayForecastLoaded(Forecast todayForecast);
  }
}
