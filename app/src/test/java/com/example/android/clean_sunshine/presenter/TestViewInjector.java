package com.example.android.clean_sunshine.presenter;

public class TestViewInjector implements SunshineViewInjector {
  @Override public <V> V inject(V view) {
    return view;
  }

  @Override public <V> V nullObjectPatternView(V view) {
    return view;
  }
}
