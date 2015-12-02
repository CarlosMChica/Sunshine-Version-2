package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.app.domain.interactor.LoadTodayForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.LoadTwoWeeksForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshWeekForecastInteractor;

import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeLocalGateway;
import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeNetworkGateway;

public class InteractorFactory {

  public static LoadTwoWeeksForecastInteractor makeLoadForecastInteractor(Context context) {
    return new LoadTwoWeeksForecastInteractor(makeLocalGateway(context), makeNetworkGateway(context));
  }

  public static RefreshWeekForecastInteractor makeRefreshForecastInteractor(Context context) {
    return new RefreshWeekForecastInteractor(makeLocalGateway(context),
        makeNetworkGateway(context));
  }

  public static LoadTodayForecastInteractor makeLoadTodayForecastInteractor(Context context) {
    return new LoadTodayForecastInteractor(makeLocalGateway(context));
  }
}
