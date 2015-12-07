package com.example.android.clean_sunshine.presenter;

import com.example.android.clean_sunshine.domain.interactor.Interactor;
import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshManualLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.presenter.forecast.ForecastPresenter;
import com.example.android.clean_sunshine.presenter.forecast.ForecastView;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) public class ForecastPresenterShould {

  private static final List<Forecast> FORECAST_LIST = singletonList(new Forecast.Builder().build());
  public static final String LOCATION = "location";

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

    verify(view).hideLoading();
    verify(view).updateForecast(FORECAST_LIST);
  }

  @Test public void hide_loading_and_show_load_forecast_error_when_load_forecast_fails()
      throws Exception {
    presenter.onLoadForecastError();

    verify(view).hideLoading();
    verify(view).showLoadForecastError();
  }

  @Test public void inject_view_and_load_forecast_when_ui_is_ready() throws Exception {
    presenter.onUiReady();

    verify(viewInjector).inject(view);
    verifyLoadForecast();
  }

  private void verifyLoadForecast() {
    verify(view).showLoading();
    verify(loadForecastInteractor).setOutput(presenter);
    verify(loadForecastInteractor).setLocation(LOCATION);
    verify(interactorExecutor).execute(loadForecastInteractor);
  }

  private void setUpInteractorExecutor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor) invocation.getArguments()[0]).run();
        return null;
      }
    }).when(interactorExecutor).execute(any(Interactor.class));
  }

  private void setUpPresenter() {
    presenter = new ForecastPresenter(view, viewInjector, loadForecastInteractor,
        refreshCurrentLocationForecastInteractor, refreshManualLocationForecastInteractor,
        interactorExecutor);
    presenter.setManualLocation(LOCATION);
  }

  private void setUpViewInjector() {
    when(viewInjector.inject(view)).thenReturn(view);
    when(viewInjector.nullObjectPatternView(view)).thenReturn(view);
  }
}
