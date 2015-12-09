package com.example.android.clean_sunshine.domain.interactor;

import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.LocalForecastGateway;

public class LoadSingleForecastInteractor implements Interactor {

  private LocalForecastGateway localForecastGateway;
  private LoadForecastByIdInteractorOutput output;
  private long dateTime;
  private String locationSetting;

  public LoadSingleForecastInteractor(LocalForecastGateway localForecastGateway) {
    this.localForecastGateway = localForecastGateway;
  }

  public void setForecastData(long dateTime, String locationSetting) {
    this.dateTime = dateTime;
    this.locationSetting = locationSetting;
  }

  public void setOutput(LoadForecastByIdInteractorOutput output) {
    this.output = output;
  }

  @Override public void run() {
    loadForecast();
  }

  private void loadForecast() {
    output.onForecastLoaded(localForecastGateway.load(dateTime, locationSetting));
  }

  public interface LoadForecastByIdInteractorOutput {
    void onForecastLoaded(Forecast forecast);
  }
}
