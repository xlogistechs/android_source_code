package com.qboxus.godelivery.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.LocationUpdateServiceCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class CurrentLocationService extends Service{

    private final IBinder binder = new LocalBinder();
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private  int UPDATE_INTERVAL = 5000;
    private  int FATEST_INTERVAL = 5000;
    private  int DISPLACEMENT = 0;
    Preferences preferences;
    double latitude;
    double longitude;
    LatLng latLng;





    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public class LocalBinder extends Binder {
        public CurrentLocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CurrentLocationService.this;
        }
    }

    final class Mythreadclass implements Runnable {
        @Override
        public void run() {
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        }
    }


    @Override
    public void onCreate() {
        preferences=new Preferences(this);
        createLocationRequest();
    }


    private LocationUpdateServiceCallback serviceCallbacks;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new CurrentLocationService.Mythreadclass());
        thread.start();

        return Service.START_STICKY;
    }


    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }



    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback locationCallback;

    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                System.out.println("Check : "+locationResult.getLastLocation().getLatitude());
                System.out.println("Check : "+locationResult.getLastLocation().getLongitude());
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLastLocation = location;
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();

                        latLng = new LatLng(latitude,longitude);

                        upload_to_sharedPreference(latLng);

                         if(serviceCallbacks!=null)
                             serviceCallbacks.onDataReceived(new LatLng(latitude,longitude));
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback
                , Looper.myLooper());

    }





    public void upload_to_sharedPreference(LatLng latLng){
        double lat = (latLng.latitude);
        double lon = (latLng.longitude);
        preferences.setKeyUserLat(""+lat);
        preferences.setKeyUserLng(""+lon);
    }




    @Override
    public void onDestroy() {
        if (mFusedLocationClient!=null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
        super.onDestroy();
    }

}

