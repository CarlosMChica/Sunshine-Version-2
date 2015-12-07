package com.example.android.clean_sunshine.presentation;

import me.panavtec.threaddecoratedview.views.ThreadSpec;
import me.panavtec.threaddecoratedview.views.ViewInjector;

public class ViewInjectorImp implements SunshineViewInjector {

  private ThreadSpec threadSpec;

  public ViewInjectorImp(ThreadSpec threadSpec) {
    this.threadSpec = threadSpec;
  }

  @Override public <V> V inject(V view) {
    return ViewInjector.inject(view, threadSpec);
  }

  @Override public <V> V nullObjectPatternView(V view) {
    return ViewInjector.nullObjectPatternView(view);
  }
}
