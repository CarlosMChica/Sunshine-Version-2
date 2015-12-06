package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import com.example.android.clean_sunshine.app.data.gps.AndroidLocationProvider;
import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastByIdInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.LoadForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.LoadTodayForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshForecastInteractor;
import com.example.android.clean_sunshine.app.domain.model.LocationProvider;

import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeLocalGateway;
import static com.example.android.clean_sunshine.app.dependencies.GatewaysFactory.makeNetworkGateway;

public class InteractorFactory {

  public static LoadForecastInteractor makeLoadForecastInteractor(Context context) {
    return new LoadForecastInteractor(makeLocalGateway(context), makeNetworkGateway(context));
  }

  public static RefreshForecastInteractor makeRefreshForecastInteractor(Context context) {
    return new RefreshForecastInteractor(makeLocationProvider(context), makeLocalGateway(context),
        makeNetworkGateway(context));
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
}
