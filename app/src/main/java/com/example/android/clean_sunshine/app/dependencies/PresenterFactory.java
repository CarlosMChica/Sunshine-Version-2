package com.example.android.clean_sunshine.app.dependencies;

import android.content.Context;
import android.os.Handler;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutor;
import com.example.android.clean_sunshine.app.presenter.InteractorExecutorImp;
import com.example.android.clean_sunshine.app.presenter.detail.DetailPresenter;
import com.example.android.clean_sunshine.app.presenter.detail.DetailView;
import com.example.android.clean_sunshine.app.presenter.forecast.CurrentLocationForecastPresenter;
import com.example.android.clean_sunshine.app.presenter.forecast.ForecastView;
import java.util.concurrent.Executors;
import me.panavtec.threaddecoratedview.views.ThreadSpec;

import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadForecastByIdInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeLoadForecastInteractor;
import static com.example.android.clean_sunshine.app.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class PresenterFactory {

  public static final int THREADS = 3;

  public static CurrentLocationForecastPresenter makeForecast(Context context, ForecastView view) {
    return new CurrentLocationForecastPresenter(view, makeMainThreadSpec(), makeLoadForecastInteractor(context),
        makeRefreshForecastInteractor(context), InteractorFactory.makeRefreshManualLocationForecastInteractor(context),
        makeInteractorExecutor());
  }

  private static InteractorExecutor makeInteractorExecutor() {
    return new InteractorExecutorImp(Executors.newFixedThreadPool(THREADS));
  }

  private static ThreadSpec makeMainThreadSpec() {
    return new MainThreadSpec();
  }

  public static DetailPresenter makeDetail(Context context, DetailView view) {
    return new DetailPresenter(view, makeMainThreadSpec(), makeLoadForecastByIdInteractor(context),
        makeInteractorExecutor());
  }

  private static class MainThreadSpec implements ThreadSpec {
    final Handler handler = new Handler();

    @Override public void execute(Runnable action) {
      handler.post(action);
    }
  }
}
