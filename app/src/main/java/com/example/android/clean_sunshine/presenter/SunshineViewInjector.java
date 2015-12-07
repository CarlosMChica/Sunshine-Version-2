package com.example.android.clean_sunshine.presenter;

public interface SunshineViewInjector {
  <V> V inject(V view);

  <V> V nullObjectPatternView(V view);
}
