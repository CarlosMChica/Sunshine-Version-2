package com.example.android.clean_sunshine.app.presenter.forecast;

import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastInteractor.LoadForecastInteractorOutput;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshForecastInteractor.RefreshForecastInteractorOutput;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import java.util.List;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class ForecastPresenter
    implements LoadForecastInteractorOutput, RefreshForecastInteractorOutput {

  private final ThreadSpec threadSpec;
  private final LoadForecastInteractor loadForecastInteractor;
  private final RefreshForecastInteractor refreshForecastInteractor;
  private final InteractorExecutor interactorExecutor;
  private ForecastView view;
  private Location location;

  public ForecastPresenter(ForecastView view, ThreadSpec threadSpec,
      LoadForecastInteractor loadForecastInteractor,
      RefreshForecastInteractor refreshForecastInteractor, InteractorExecutor interactorExecutor) {
    this.view = view;
    this.threadSpec = threadSpec;
    this.loadForecastInteractor = loadForecastInteractor;
    this.refreshForecastInteractor = refreshForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(List<Forecast> forecastList) {
    extractLocation(forecastList);
    updateViewItems(forecastList);
  }

  @Override public void onLoadForecastError() {
    view.hideLoading();
    view.showLoadForecastError();
  }

  @Override public void onForecastRefreshed(List<Forecast> forecastList) {
    extractLocation(forecastList);
    updateViewItems(forecastList);
  }

  @Override public void onRefreshForecastError() {
    view.hideLoading();
    view.showRefreshForecastError();
  }

  public void onUiReady() {
    view = ViewInjector.inject(view, threadSpec);
    loadForecast();
  }

  public void onLocationChanged(String location) {
    refreshForecast(location);
  }

  public void detachView() {
    this.view = ViewInjector.nullObjectPatternView(view);
  }

  public void onRefreshClick() {
    refreshForecast(null);
  }

  public void onOpenMapOptionClick() {
    if (location != null) {
      view.goToMapScreen(location.getLat(), location.getLon());
    }
  }

  private void loadForecast() {
    view.showLoading();
    loadForecastInteractor.setOutput(this);
    interactorExecutor.execute(loadForecastInteractor);
  }

  private void refreshForecast(String location) {
    view.showLoading();
    refreshForecastInteractor.setOutput(this);
    refreshForecastInteractor.setLocation(location);
    interactorExecutor.execute(refreshForecastInteractor);
  }

  private void updateViewItems(List<Forecast> forecastList) {
    view.hideLoading();
    view.updateForecast(forecastList);
  }

  private void extractLocation(List<Forecast> forecastList) {
    if (!forecastList.isEmpty()) {
      this.location = forecastList.get(0).getLocation();
    }
  }
}
