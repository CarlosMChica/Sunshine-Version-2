package com.example.android.clean_sunshine.app.domain.model;

import java.util.List;

public interface NetworkGateway {
  List<Forecast> refresh();
}
