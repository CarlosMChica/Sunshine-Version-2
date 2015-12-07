package com.example.android.clean_sunshine.presentation.detail;

import me.panavtec.threaddecoratedview.views.qualifiers.NotDecorated;
import me.panavtec.threaddecoratedview.views.qualifiers.ThreadDecoratedView;

@ThreadDecoratedView public interface DetailView {
  void showFriendDate(long dateTime);

  void showIcon(int id);

  void showDate(long dateTime);

  void showDescription(String description);

  void showHighTemp(double high);

  void showLowTemp(double low);

  void showHumidity(int humidity);

  void showWind(float windDirection, float windSpeed);

  void showPressure(double pressure);

  @NotDecorated void showShareOptions(String shareText);
}
