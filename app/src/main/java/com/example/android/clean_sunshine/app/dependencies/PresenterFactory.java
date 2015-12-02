package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import android.os.Handler;
import com.example.android.clean_sunshine.app.domain.interactor.LoadWeekForecastInteractor;
import com.example.android.clean_sunshine.app.domain.interactor.RefreshWeekForecastInteractor;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutorImp;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastPresenter;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastView;
import java.util.concurrent.Executors;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadForecastInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class PresenterFactory {

  public static final int THREADS = 3;

  public static ForecastPresenter make(Context context, ForecastView view) {
    return new ForecastPresenter(getForecastView(view), getLoadForecastInteractor(context),
        getRefreshForecastInteractor(context), getInteractorExecutor());
  }

  private static InteractorExecutor getInteractorExecutor() {
    return new InteractorExecutorImp(Executors.newFixedThreadPool(THREADS));
  }

  private static ForecastView getForecastView(ForecastView view) {
    return ViewInjector.inject(view, getMainThreadSpec());
  }

  private static ThreadSpec getMainThreadSpec() {
    return new MainThreadSpec();
  }

  private static LoadWeekForecastInteractor getLoadForecastInteractor(Context context) {
    return makeLoadForecastInteractor(context);
  }

  private static RefreshWeekForecastInteractor getRefreshForecastInteractor(Context context) {
    return makeRefreshForecastInteractor(context);
  }

  private static class MainThreadSpec implements ThreadSpec {
    final Handler handler = new Handler();

    @Override public void execute(Runnable action) {
      handler.post(action);
    }
  }
}
