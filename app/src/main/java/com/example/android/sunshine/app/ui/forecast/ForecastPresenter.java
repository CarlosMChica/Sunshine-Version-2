package com.example.android.sunshine.app.ui.forecast;

import com.example.android.sunshine.app.data.LocalGateway;
import com.example.android.sunshine.app.data.RemoteGateway;
import com.example.android.sunshine.app.data.Weather;
import java.util.List;

public class ForecastPresenter {

  private ForecastView view;
  private LocalGateway localGateway;
  private RemoteGateway remoteGateway;

  public ForecastPresenter(LocalGateway localGateway, RemoteGateway remoteGateway,
      ForecastView view) {
    this.localGateway = localGateway;
    this.remoteGateway = remoteGateway;
    this.view = view;
  }

  public void onUiReady() {
    initData();
  }

  private void initData() {
    final List<Weather> data = localGateway.load();
    if (data.isEmpty()) {
      updateForecastFromRemote();
    } else {
      view.updateForecast(data);
    }
  }

  private void updateForecastFromRemote() {
    new Thread(new Runnable() {
      @Override public void run() {
        view.updateForecast(remoteGateway.refresh());
      }
    }).start();
  }

  public interface ForecastView {

    void updateForecast(List<Weather> localData);
  }
}
