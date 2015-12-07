package com.example.android.clean_sunshine.dependencies;

import android.content.Context;
import android.os.Handler;
import com.example.android.clean_sunshine.presentation.InteractorExecutor;
import com.example.android.clean_sunshine.presentation.InteractorExecutorImp;
import com.example.android.clean_sunshine.presentation.SunshineViewInjector;
import com.example.android.clean_sunshine.presentation.ViewInjectorImp;
import com.example.android.clean_sunshine.presentation.detail.DetailPresenter;
import com.example.android.clean_sunshine.presentation.detail.DetailView;
import com.example.android.clean_sunshine.presentation.forecast.ForecastPresenter;
import com.example.android.clean_sunshine.presentation.forecast.ForecastView;
import java.util.concurrent.Executors;
import me.panavtec.threaddecoratedview.views.ThreadSpec;

import static com.example.android.clean_sunshine.dependencies.InteractorFactory.makeLoadForecastByIdInteractor;
import static com.example.android.clean_sunshine.dependencies.InteractorFactory.makeLoadForecastInteractor;
import static com.example.android.clean_sunshine.dependencies.InteractorFactory.makeRefreshForecastInteractor;

public class PresenterFactory {

  public static final int THREADS = 3;

  public static ForecastPresenter makeForecast(Context context, ForecastView view) {
    return new ForecastPresenter(view, makeViewInjector(), makeLoadForecastInteractor(context),
        makeRefreshForecastInteractor(context),
        InteractorFactory.makeRefreshManualLocationForecastInteractor(context),
        makeInteractorExecutor());
  }

  private static InteractorExecutor makeInteractorExecutor() {
    return new InteractorExecutorImp(Executors.newFixedThreadPool(THREADS));
  }

  private static SunshineViewInjector makeViewInjector() {
    return new ViewInjectorImp(makeThreadSpec());
  }

  private static ThreadSpec makeThreadSpec() {
    return new MainThreadSpec();
  }

  public static DetailPresenter makeDetail(Context context, DetailView view) {
    return new DetailPresenter(view, makeThreadSpec(), makeLoadForecastByIdInteractor(context),
        makeInteractorExecutor());
  }

  private static class MainThreadSpec implements ThreadSpec {
    final Handler handler = new Handler();

    @Override public void execute(Runnable action) {
      handler.post(action);
    }
  }
}
