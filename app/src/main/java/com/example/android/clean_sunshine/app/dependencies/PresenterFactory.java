package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import android.os.Handler;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutorImp;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastPresenter;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastView;
import java.util.concurrent.Executors;
import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadForecastInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadLocationInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class PresenterFactory {

  public static final int THREADS = 3;

  public static ForecastPresenter make(Context context, ForecastView view) {
    return new ForecastPresenter(getForecastView(view), makeLoadForecastInteractor(context),
        makeRefreshForecastInteractor(context), makeLoadLocationInteractor(context),
        makeInteractorExecutor());
  }

  private static InteractorExecutor makeInteractorExecutor() {
    return new InteractorExecutorImp(Executors.newFixedThreadPool(THREADS));
  }

  private static ForecastView getForecastView(ForecastView view) {
    return ViewInjector.inject(view, makeMainThreadSpec());
  }

  private static ThreadSpec makeMainThreadSpec() {
    return new MainThreadSpec();
  }

  private static class MainThreadSpec implements ThreadSpec {
    final Handler handler = new Handler();

    @Override public void execute(Runnable action) {
      handler.post(action);
    }
  }
}
