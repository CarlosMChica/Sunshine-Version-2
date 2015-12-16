package com.example.android.clean_sunshine.domain.interactor;

import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor.LoadForecastInteractorOutput;
import com.example.android.clean_sunshine.domain.model.Forecast;
import com.example.android.clean_sunshine.domain.model.LocalForecastGateway;
import com.example.android.clean_sunshine.domain.model.NetworkForecastGateway;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) public class LoadForecastInteractorShould {

  private static final List<Forecast> LOCAL_FORECAST = Collections.singletonList(buildForecast());
  private static final List<Forecast> NETWORK_FORECAST = asList(buildForecast(), buildForecast());
  private static final List<Forecast> EMPTY_FORECAST = Collections.emptyList();
  private static final String LOCATION = "location";
  private static final Exception EXCEPTION = new RuntimeException();

  private LoadForecastInteractor interactor;

  @Mock LocalForecastGateway localForecastGateway;
  @Mock NetworkForecastGateway networkForecastGateway;
  @Mock LoadForecastInteractorOutput output;

  @Before public void setUp() throws Exception {
    interactor = new LoadForecastInteractor(localForecastGateway, networkForecastGateway);
    interactor.setOutput(output);
    interactor.setLocation(LOCATION);
  }

  @Test public void return_local_forecast_when_local_gateway_returns_data() {
    given(localForecastGateway.load()).willReturn(LOCAL_FORECAST);

    interactor.run();

    verify(output).onForecastLoaded(LOCAL_FORECAST);
  }

  @Test public void return_network_forecast_and_update_local_gateway_when_local_gateway_is_empty() {
    given(localForecastGateway.load()).willReturn(EMPTY_FORECAST);
    given(networkForecastGateway.refresh(LOCATION)).willReturn(NETWORK_FORECAST);

    interactor.run();

    verify(output).onForecastLoaded(NETWORK_FORECAST);
    verify(localForecastGateway).update(NETWORK_FORECAST);
  }

  @Test public void return_error_when_exception_is_thrown_loading_forecast() throws Exception {
    given(localForecastGateway.load()).willThrow(EXCEPTION);

    interactor.run();

    verify(output).onLoadForecastError();
  }

  private static Forecast buildForecast() {
    return new Forecast.Builder().build();
  }
}
