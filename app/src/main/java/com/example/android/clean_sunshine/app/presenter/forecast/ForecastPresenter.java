package com.example.android.clean_sunshine.app.presenter.forecast;

import com.example.android.clean_sunshine.app.domain.interactor.LoadTwoWeeksForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshWeekForecastInteractor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import java.util.List;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class ForecastPresenter implements LoadTwoWeeksForecastInteractor.LoadForecastInteractorOutput,
    RefreshWeekForecastInteractor.RefreshForecastInteractorOutput {

  private ForecastView view;
  private LoadTwoWeeksForecastInteractor loadTwoWeeksForecastInteractor;
  private RefreshWeekForecastInteractor refreshWeekForecastInteractor;
  private InteractorExecutor interactorExecutor;
  private List<Forecast> forecastList;

  public ForecastPresenter(ForecastView view, LoadTwoWeeksForecastInteractor loadTwoWeeksForecastInteractor,
      RefreshWeekForecastInteractor refreshWeekForecastInteractor, InteractorExecutor interactorExecutor) {
    this.view = view;
    this.loadTwoWeeksForecastInteractor = loadTwoWeeksForecastInteractor;
    this.refreshWeekForecastInteractor = refreshWeekForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(List<Forecast> forecastList) {
    this.forecastList = forecastList;
    updateViewItems(forecastList);
  }

  @Override public void onLoadForecastError() {
    view.showLoadForecastError();
  }

  @Override public void onForecastRefreshed(List<Forecast> forecastList) {
    this.forecastList = forecastList;
    updateViewItems(forecastList);
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

  public void detachView() {
    this.view = ViewInjector.nullObjectPatternView(view);
  }

  public void onRefreshClick() {
    refreshForecast();
  }

  public void onOpenMapOptionClick() {
    if (!forecastList.isEmpty()) {
      Location location = forecastList.get(0).getLocation();
      view.goToMapScreen(location.getLat(), location.getLon());
    }
  }

  private void loadForecast() {
    view.showLoading();
    loadTwoWeeksForecastInteractor.setOutput(this);
    interactorExecutor.execute(loadTwoWeeksForecastInteractor);
  }

  private void refreshForecast() {
    view.showLoading();
    refreshWeekForecastInteractor.setOutput(this);
    interactorExecutor.execute(refreshWeekForecastInteractor);
  }

  private void updateViewItems(List<Forecast> forecastList) {
    view.hideLoading();
    view.updateForecast(forecastList);
  }
}
