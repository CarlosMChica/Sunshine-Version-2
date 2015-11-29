package com.example.android.clean_sunshine.app.presenter.forecast;

import com.example.android.clean_sunshine.app.domain.interactor.LoadWeekForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshWeekForecastInteractor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import java.util.List;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class ForecastPresenter implements LoadWeekForecastInteractor.LoadForecastInteractorOutput,
    RefreshWeekForecastInteractor.RefreshForecastInteractorOutput {

  private ForecastView view;
  private LoadWeekForecastInteractor loadWeekForecastInteractor;
  private RefreshWeekForecastInteractor refreshWeekForecastInteractor;
  private InteractorExecutor interactorExecutor;

  public ForecastPresenter(ForecastView view, LoadWeekForecastInteractor loadWeekForecastInteractor,
      RefreshWeekForecastInteractor refreshWeekForecastInteractor, InteractorExecutor interactorExecutor) {
    this.view = view;
    this.loadWeekForecastInteractor = loadWeekForecastInteractor;
    this.refreshWeekForecastInteractor = refreshWeekForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(List<Forecast> forecastList) {
    updateViewItems(forecastList);
  }

  @Override public void onForecastRefreshed(List<Forecast> forecastList) {
    updateViewItems(forecastList);
  }

  @Override public void onLoadForecastError() {
    view.showLoadForecastError();
  }

  @Override public void onRefreshForecastError() {
    view.showRefreshForecastError();
  }

  public void onUiReady() {
    loadForecast();
  }

  public void onLocationChanged() {
    refreshForecast();
  }

  private void loadForecast() {
    loadWeekForecastInteractor.setOutput(this);
    interactorExecutor.execute(loadWeekForecastInteractor);
  }

  private void refreshForecast() {
    refreshWeekForecastInteractor.setOutput(this);
    interactorExecutor.execute(refreshWeekForecastInteractor);
  }

  private void updateViewItems(List<Forecast> forecastList) {
    view.updateForecast(forecastList);
  }

  public void detachView() {
    this.view = ViewInjector.nullObjectPatternView(view);
  }
}
