package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.data.gps.AndroidLocationProvider;
import com.example.android.clean_sunshine.domain.interactor.LoadForecastByIdInteractor;
import com.example.android.clean_sunshine.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.LoadTodayForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshCurrentLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.interactor.RefreshManualLocationForecastInteractor;
import com.example.android.clean_sunshine.domain.model.LocationProvider;

import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeLocalGateway;
import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeNetworkGateway;

public class InteractorFactory {

  public static LoadForecastInteractor makeLoadForecastInteractor(Context context) {
    return new LoadForecastInteractor(makeLocalGateway(context), makeNetworkGateway(context));
  }

  public static RefreshCurrentLocationForecastInteractor makeRefreshForecastInteractor(
      Context context) {
    return new RefreshCurrentLocationForecastInteractor(makeLocationProvider(context),
        makeLocalGateway(context), makeNetworkGateway(context));
  }

  public static LoadTodayForecastInteractor makeLoadTodayForecastInteractor(Context context) {
    return new LoadTodayForecastInteractor(makeLocalGateway(context));
  }

  public static LocationProvider makeLocationProvider(Context context) {
    return new AndroidLocationProvider(context);
  }

  public static LoadForecastByIdInteractor makeLoadForecastByIdInteractor(Context context) {
    return new LoadForecastByIdInteractor(makeLocalGateway(context));
  }

  public static RefreshManualLocationForecastInteractor makeRefreshManualLocationForecastInteractor(
      Context context) {
    return new RefreshManualLocationForecastInteractor(makeLocalGateway(context),
        makeNetworkGateway(context));
  }
}
