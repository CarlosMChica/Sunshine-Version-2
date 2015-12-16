package com.example.android.clean_sunshine.presentation;

public class TestViewInjector implements SunshineViewInjector {
  @Override public <V> V inject(V view) {
    return view;
  }

  @Override public <V> V nullObjectPatternView(V view) {
    return view;
  }
}
