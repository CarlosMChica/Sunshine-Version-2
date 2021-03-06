package com.example.android.clean_sunshine.data.gps;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.android.clean_sunshine.domain.model.Location;
import com.example.android.clean_sunshine.domain.model.LocationProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.EmptyPermissionListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static com.google.android.gms.location.LocationServices.API;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class AndroidLocationProvider
    implements LocationProvider, ConnectionCallbacks, OnConnectionFailedListener {

  private final Context context;
  private GoogleApiClient googleApiClient;
  private LocationProviderListener listener;

  public AndroidLocationProvider(Context context) {
    this.context = context;
  }

  @Override public void requestCurrentLocation(final LocationProviderListener listener) {
    this.listener = listener;
    checkLocationPermission();
  }

  @Override public void onConnected(Bundle bundle) {
    getLastLocation();
  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    listener.onRetrieveLocationError();
  }

  private void checkLocationPermission() {
    Dexter.checkPermission(new EmptyPermissionListener() {
      @Override public void onPermissionGranted(PermissionGrantedResponse response) {
        //TODO this should not be done but Dexter returns this method on the UI thread
        new Thread(new Runnable() {
          @Override public void run() {
            initGoogleApiClient();
          }
        }).start();
      }

      @Override public void onPermissionDenied(PermissionDeniedResponse response) {
        listener.onRetrieveLocationError();
      }
    }, ACCESS_FINE_LOCATION);
  }

  private void initGoogleApiClient() {
    Looper.prepare();
    Handler handler = new Handler();
    googleApiClient = new Builder(context).addApi(API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .setHandler(handler)
        .build();
    googleApiClient.connect();
    Looper.loop();
  }

  private void getLastLocation() {
    listener.onLocationRetrieved(
        convertToLocationModel(FusedLocationApi.getLastLocation(googleApiClient)));
  }

  private Location convertToLocationModel(android.location.Location location) {
    return new Location.Builder().lat(location.getLatitude()).lon(location.getLongitude()).build();
  }
}
