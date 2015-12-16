package com.example.android.clean_sunshine.presentation;

import com.example.android.clean_sunshine.domain.interactor.Interactor;
import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshManualLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.presentation.forecast.ForecastPresenter;
import com.example.android.clean_sunshine.presentation.forecast.ForecastView;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) public class ForecastPresenterShould {

  private static final List<Forecast> FORECAST_LIST = Collections.singletonList(new Forecast.Builder().build());
  private static final String LOCATION = "location";

  private ForecastPresenter presenter;

  @Mock SunshineViewInjector viewInjector;
  @Mock ForecastView view;
  @Mock InteractorExecutor interactorExecutor;
  @Mock RefreshManualLocationForecastInteractor refreshManualLocationForecastInteractor;
  @Mock RefreshCurrentLocationForecastInteractor refreshCurrentLocationForecastInteractor;
  @Mock LoadForecastInteractor loadForecastInteractor;

  @Before public void setUp() throws Exception {
    setUpPresenter();
    setUpInteractorExecutor();
    setUpViewInjector();
  }

  @Test public void update_view_when_forecast_is_loaded() throws Exception {
    presenter.onForecastLoaded(FORECAST_LIST);

    Mockito.verify(view).hideLoading();
    Mockito.verify(view).updateForecast(FORECAST_LIST);
  }

  @Test public void hide_loading_and_show_load_forecast_error_when_load_forecast_fails()
      throws Exception {
    presenter.onLoadForecastError();

    Mockito.verify(view).hideLoading();
    Mockito.verify(view).showLoadForecastError();
  }

  @Test public void inject_view_and_load_forecast_when_ui_is_ready() throws Exception {
    presenter.onUiReady();

    Mockito.verify(viewInjector).inject(view);
    verifyLoadForecast();
  }

  private void verifyLoadForecast() {
    Mockito.verify(view).showLoading();
    Mockito.verify(loadForecastInteractor).setOutput(presenter);
    Mockito.verify(loadForecastInteractor).setLocation(LOCATION);
    Mockito.verify(interactorExecutor).execute(loadForecastInteractor);
  }

  private void setUpInteractorExecutor() {
    Mockito.doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor) invocation.getArguments()[0]).run();
        return null;
      }
    }).when(interactorExecutor).execute(Matchers.any(Interactor.class));
  }

  private void setUpPresenter() {
    presenter = new ForecastPresenter(view, viewInjector, loadForecastInteractor,
        refreshCurrentLocationForecastInteractor, refreshManualLocationForecastInteractor,
        interactorExecutor);
    presenter.setManualLocation(LOCATION);
  }

  private void setUpViewInjector() {
    Mockito.when(viewInjector.inject(view)).thenReturn(view);
    Mockito.when(viewInjector.nullObjectPatternView(view)).thenReturn(view);
  }
}
