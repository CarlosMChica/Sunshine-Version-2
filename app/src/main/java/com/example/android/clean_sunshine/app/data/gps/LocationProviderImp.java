package com.example.android.clean_sunshine.app.data.gps;

import android.content.Context;
import android.os.Bundle;
import com.example.android.clean_sunshine.app.domain.model.Location;
import com.example.android.clean_sunshine.app.domain.model.LocationProvider;
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

public class LocationProviderImp
    implements LocationProvider, ConnectionCallbacks, OnConnectionFailedListener {

  private final Context context;
  private GoogleApiClient googleApiClient;
  private LocationProviderListener listener;

  public LocationProviderImp(Context context) {
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
        initGoogleApiClient();
      }

      @Override public void onPermissionDenied(PermissionDeniedResponse response) {
        listener.onRetrieveLocationError();
      }
    }, ACCESS_FINE_LOCATION);
  }

  private void initGoogleApiClient() {
    googleApiClient = new Builder(context).addApi(API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
    googleApiClient.connect();
  }

  private void getLastLocation() {
    //This should not be done
    new Thread(new Runnable() {
      @Override public void run() {
        final android.location.Location lastLocation =
            FusedLocationApi.getLastLocation(googleApiClient);
        listener.onLocationRetrieved(convertToLocationModel(lastLocation));
      }
    }).start();
  }

  private Location convertToLocationModel(android.location.Location location) {
    return new Location.Builder().lat(location.getLatitude()).lon(location.getLongitude()).build();
  }
}
