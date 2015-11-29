package com.example.android.clean_sunshine.app.domain.interactor;

import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.LocalGateway;

public class LoadTodayForecastInteractor implements Interactor {

  private LocalGateway localGateway;
  private LoadTodayForecastInteractorOutput output;

  public LoadTodayForecastInteractor(LocalGateway localGateway) {
    this.localGateway = localGateway;
  }

  public void setOutput(LoadTodayForecastInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    loadTodayForecast();
  }

  private void loadTodayForecast() {
    output.onTodayForecastLoaded(localGateway.loadToday());
  }

  public interface LoadTodayForecastInteractorOutput {
    void onTodayForecastLoaded(Forecast todayForecast);
  }
}
