package com.example.android.clean_sunshine.presentation.detail;

import com.example.android.clean_sunshine.domain.interactor.LoadSingleForecastInteractor;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.presentation.InteractorExecutor;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class DetailPresenter
    implements LoadSingleForecastInteractor.LoadForecastByIdInteractorOutput {

  private final ThreadSpec threadSpec;
  private final LoadSingleForecastInteractor loadSingleForecastInteractor;
  private final InteractorExecutor interactorExecutor;
  private DetailView view;
  private Forecast forecast;
  private long dateTime;
  private String locationSetting;

  public DetailPresenter(DetailView view, ThreadSpec threadSpec,
      LoadSingleForecastInteractor loadSingleForecastInteractor,
      InteractorExecutor interactorExecutor) {
    this.view = view;
    this.threadSpec = threadSpec;
    this.loadSingleForecastInteractor = loadSingleForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(Forecast forecast) {
    this.forecast = forecast;
    updateView(forecast);
  }

  public void setForecastData(long dateTime, String locationSetting) {
    this.dateTime = dateTime;
    this.locationSetting = locationSetting;
  }

  public void onUiReady() {
    view = ViewInjector.inject(view, threadSpec);
    loadForecast();
  }

  public void detachView() {
    view = ViewInjector.nullObjectPatternView(view);
  }

  private void loadForecast() {
    loadSingleForecastInteractor.setForecastData(dateTime, locationSetting);
    loadSingleForecastInteractor.setOutput(this);
    interactorExecutor.execute(loadSingleForecastInteractor);
  }

  public void onShareClicked() {
    String shareText = String.format("%s - %s/%s", forecast.getDescription(), forecast.getHigh(),
        forecast.getLow());
    view.showShareOptions(shareText);
  }

  private void updateView(Forecast forecast) {
    //view.showIcon(forecast.getId());
    //view.showFriendDate(forecast.getDateTime());
    //view.showDate(forecast.getDateTime());
    //view.showDescription(forecast.getDescription());
    //view.showHighTemp(forecast.getHigh());
    //view.showLowTemp(forecast.getLow());
    //view.showHumidity(forecast.getHumidity());
    //view.showWind(((float) forecast.getWindDirection()), ((float) forecast.getWindSpeed()));
    //view.showPressure(forecast.getPressure());
  }
}
