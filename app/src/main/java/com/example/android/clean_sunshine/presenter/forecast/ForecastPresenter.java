package com.example.android.clean_sunshine.presenter.forecast;

import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor.LoadForecastInteractorOutput;
import com.example.android.clean_sunshine.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshCurrentLocationForecastInteractor.RefreshCurrentLocationForecastInteractorOutput;
import com.example.android.clean_sunshine.domain.interactor.RefreshManualLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.Location;
import com.example.android.clean_sunshine.presenter.InteractorExecutor;
import com.example.android.clean_sunshine.presenter.SunshineViewInjector;
import java.util.List;

import static com.example.android.clean_sunshine.domain.interactor.RefreshManualLocationForecastInteractor.RefreshManualLocationForecastInteractorOutput;

public class ForecastPresenter
    implements LoadForecastInteractorOutput, RefreshCurrentLocationForecastInteractorOutput,
    RefreshManualLocationForecastInteractorOutput {

  private final SunshineViewInjector viewInjector;
  private final LoadForecastInteractor loadForecastInteractor;
  private final RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor;
  private final RefreshManualLocationForecastInteractor refreshManualLocationForecastInteractor;
  private final InteractorExecutor interactorExecutor;
  private ForecastView view;
  private String manualLocation;
  private List<Forecast> forecastList;

  public ForecastPresenter(ForecastView view, SunshineViewInjector viewInjector,
      LoadForecastInteractor loadForecastInteractor,
      RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor,
      RefreshManualLocationForecastInteractor refreshManualLocationForecastInteractor,
      InteractorExecutor interactorExecutor) {
    this.view = view;
    this.viewInjector = viewInjector;
    this.loadForecastInteractor = loadForecastInteractor;
    this.refreshCurrentLocationForecastInteractor = refreshCurrentLocationForecastInteractor;
    this.refreshManualLocationForecastInteractor = refreshManualLocationForecastInteractor;
    this.interactorExecutor = interactorExecutor;
  }

  @Override public void onForecastLoaded(List<Forecast> forecastList) {
    this.forecastList = forecastList;
    updateView(forecastList);
  }

  @Override public void onLoadForecastError() {
    view.hideLoading();
    view.showLoadForecastError();
  }

  @Override public void onCurrentLocationForecastRefreshed(List<Forecast> forecastList) {
    this.forecastList = forecastList;
    updateView(forecastList);
  }

  @Override public void onRefreshCurrentLocationForecastError() {
    view.hideLoading();
    view.showRefreshForecastError();
  }

  @Override public void onManualLocationForecastRefreshed(List<Forecast> forecastList) {
    this.forecastList = forecastList;
    updateView(forecastList);
  }

  @Override public void onRefreshManualLocationForecastError() {
    view.hideLoading();
    view.showRefreshForecastError();
  }

  public void onUiReady() {
    view = viewInjector.inject(view);
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
    this.view = viewInjector.nullObjectPatternView(view);
  }

  public void onRefreshClick() {
    refreshCurrentLocationForecast();
  }

  public void onOpenMapOptionClick() {
    if (canOpenMap()) {
      Location location = forecastList.get(0).getLocation();
      view.goToMapScreen(location.getLat(), location.getLon());
    }
  }

  private boolean canOpenMap() {
    return forecastList != null && !forecastList.isEmpty();
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

  private void updateView(List<Forecast> forecastList) {
    view.hideLoading();
    view.updateForecast(forecastList);
  }
}
