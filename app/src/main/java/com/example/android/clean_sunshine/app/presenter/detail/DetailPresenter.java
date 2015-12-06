package com.example.android.clean_sunshine.app.presenter.detail;

import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastByIdInteractor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class DetailPresenter
    implements LoadForecastByIdInteractor.LoadForecastByIdInteractorOutput {

  private final ThreadSpec threadSpec;
  private final LoadForecastByIdInteractor loadForecastByIdInteractor;
  private final InteractorExecutor interactorExecutor;
  private DetailView view;
  private int forecastId;
  private Forecast forecast;

  public DetailPresenter(DetailView view, ThreadSpec threadSpec,
      LoadForecastByIdInteractor loadForecastByIdInteractor,
      InteractorExecutor interactorExecutor) {
    this.view = view;
    this.threadSpec = threadSpec;
    this.loadForecastByIdInteractor = loadForecastByIdInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(Forecast forecast) {
    this.forecast = forecast;
    view.showIcon(forecast.getId());
    view.showFriendDate(forecast.getDateTime());
    view.showDate(forecast.getDateTime());
    view.showDescription(forecast.getDescription());
    view.showHighTemp(forecast.getHigh());
    view.showLowTemp(forecast.getLow());
    view.showHumidity(forecast.getHumidity());
    view.showWind(((float) forecast.getWindDirection()), ((float) forecast.getWindSpeed()));
    view.showPressure(forecast.getPressure());
  }

  public void setForecastId(int forecastId) {
    this.forecastId = forecastId;
  }

  public void onUiReady() {
    view = ViewInjector.inject(view, threadSpec);
    loadForecast();
  }

  public void detachView() {
    view = ViewInjector.nullObjectPatternView(view);
  }

  private void loadForecast() {
    loadForecastByIdInteractor.setForecastId(forecastId);
    loadForecastByIdInteractor.setOutput(this);
    interactorExecutor.execute(loadForecastByIdInteractor);
  }

  public void onShareClicked() {
    String shareText = String.format("%s - %s/%s", forecast.getDescription(), forecast.getHigh(),
        forecast.getLow());
    view.showShareOptions(shareText);
  }
}
