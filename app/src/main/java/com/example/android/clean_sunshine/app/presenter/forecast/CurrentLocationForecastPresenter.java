package com.example.android.clean_sunshine.app.presenter.forecast;

import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastInteractor.LoadForecastInteractorOutput;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshCurrentLocationForecastInteractor.RefreshCurrentLocationForecastInteractorOutput;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshManualLocationForecastInteractor;
import com.example.android.clean_sunshine.app.domain.model.Forecast;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import java.util.List;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

import static com.example.android.clean_sunshine.app.domain.interactor.RefreshManualLocationForecastInteractor.RefreshManualLocationForecastInteractorOutput;

public class CurrentLocationForecastPresenter
    implements LoadForecastInteractorOutput, RefreshCurrentLocationForecastInteractorOutput,
    RefreshManualLocationForecastInteractorOutput {

  private final ThreadSpec threadSpec;
  private final LoadForecastInteractor loadForecastInteractor;
  private final RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor;
  private final RefreshManualLocationForecastInteractor refreshManualLocationForecastInteractor;
  private final InteractorExecutor interactorExecutor;
  private ForecastView view;
  private Location currentForecastLocation;
  private String manualLocation;

  public CurrentLocationForecastPresenter(ForecastView view, ThreadSpec threadSpec,
      LoadForecastInteractor loadForecastInteractor,
      RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor,
      RefreshManualLocationForecastInteractor refreshManualLocationForecastInteractor,
      InteractorExecutor interactorExecutor) {
    this.view = view;
    this.threadSpec = threadSpec;
    this.loadForecastInteractor = loadForecastInteractor;
    this.refreshCurrentLocationForecastInteractor = refreshCurrentLocationForecastInteractor;
    this.refreshManualLocationForecastInteractor = refreshManualLocationForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(List<Forecast> forecastList) {
    updateView(forecastList);
  }

  @Override public void onLoadForecastError() {
    view.hideLoading();
    view.showLoadForecastError();
  }

  @Override public void onCurrentLocationForecastRefreshed(List<Forecast> forecastList) {
    updateView(forecastList);
  }

  @Override public void onRefreshCurrentLocationForecastError() {
    view.hideLoading();
    view.showRefreshForecastError();
  }

  @Override public void onManualLocationForecastRefreshed(List<Forecast> forecastList) {
    updateView(forecastList);
  }

  @Override public void onRefreshManualLocationForecastError() {
    view.hideLoading();
    view.showRefreshForecastError();
  }

  public void onUiReady() {
    view = ViewInjector.inject(view, threadSpec);
    loadForecast();
  }

  public void setManualLocation(String manualLocation) {
    this.manualLocation = manualLocation;
  }

  public void onLocationChanged(String location) {
    manualLocation = location;
    refreshManualLocationForecast(location);
  }

  public void detachView() {
    this.view = ViewInjector.nullObjectPatternView(view);
  }

  public void onRefreshClick() {
    refreshCurrentLocationForecast();
  }

  public void onOpenMapOptionClick() {
    if (currentForecastLocation != null) {
      view.goToMapScreen(currentForecastLocation.getLat(), currentForecastLocation.getLon());
    }
  }

  private void loadForecast() {
    view.showLoading();
    loadForecastInteractor.setOutput(this);
    loadForecastInteractor.setLocation(manualLocation);
    interactorExecutor.execute(loadForecastInteractor);
  }

  private void refreshManualLocationForecast(String location) {
    view.showLoading();
    refreshManualLocationForecastInteractor.setOutput(this);
    refreshManualLocationForecastInteractor.setLocation(location);
    interactorExecutor.execute(refreshCurrentLocationForecastInteractor);
  }

  private void refreshCurrentLocationForecast() {
    view.showLoading();
    refreshCurrentLocationForecastInteractor.setOutput(this);
    interactorExecutor.execute(refreshCurrentLocationForecastInteractor);
  }

  private void updateViewItems(List<Forecast> forecastList) {
    view.hideLoading();
    view.updateForecast(forecastList);
  }

  private void extractLocation(List<Forecast> forecastList) {
    if (!forecastList.isEmpty()) {
      this.currentForecastLocation = forecastList.get(0).getLocation();
    }
  }

  private void updateView(List<Forecast> forecastList) {
    extractLocation(forecastList);
    updateViewItems(forecastList);
  }
}
