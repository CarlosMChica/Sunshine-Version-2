package com.example.android.clean_sunshine.presentation;

public interface SunshineViewInjector {
  <V> V inject(V view);

  <V> V nullObjectPatternView(V view);
}
