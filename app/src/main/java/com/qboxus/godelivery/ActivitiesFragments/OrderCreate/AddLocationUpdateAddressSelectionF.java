package com.qboxus.godelivery.ActivitiesFragments.OrderCreate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.SearchLocationF;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.Interfaces.LocationUpdateServiceCallback;
import com.qboxus.godelivery.MapClasses.GpsUtils;
import com.qboxus.godelivery.MapClasses.MapWorker;
import com.qboxus.godelivery.R;

import java.io.IOException;
import java.util.List;


public class AddLocationUpdateAddressSelectionF extends RootFragment implements View.OnClickListener,
        OnMapReadyCallback,
        LocationUpdateServiceCallback {


    private FusedLocationProviderClient mFusedLocationClient;
    private ImageView ivCurrentLoc, ivBack;
    private LinearLayout btnConfirm;
    private MapView mapFragment;
    private GoogleMap mMap;
    private LatLng latLng;
    boolean isLatLngBound=false;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private View view;
    private TextView tvTitle;
    private LinearLayout llLoc;
    String title;

    private TextView tvAddress,
            tvAddressTitle;
    FragmentClickCallback callback;
    Preferences preferences;

    public AddLocationUpdateAddressSelectionF() {
    }


    public AddLocationUpdateAddressSelectionF(String title, FragmentClickCallback callback) {
        this.title = title;
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_add_location_address_selection_, container, false);

        mapFragment = view.findViewById(R.id.map);
        mapFragment.onCreate(savedInstanceState);

        METHOD_setupMap();
        METHOD_init_views();


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success = false;

            if (preferences.getKeyIsNightMode()) {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.dark_map));
            } else {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.gray_map));
            }

            if (!success) {
                Log.e(Variables.tag, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Variables.tag, "Can't find style. Error: ", e);
        }


        if (onCameraIdleListener != null)
            mMap.setOnCameraIdleListener(onCameraIdleListener);

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        Zoom_to_Current_location();
    }

    @Override
    public void onDataReceived(LatLng data) {
        if (mMap != null)
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }mMap.setMyLocationEnabled(true);

        if(data==null)
            Zoom_to_Current_location();
    }

    private void Zoom_to_Current_location(){
        tvAddressTitle.setText(OrderCreateF.pre_address);
        tvAddress.setText(OrderCreateF.pre_address);
        double lat=Double.parseDouble(OrderCreateF.pre_lat);
        double lng=Double.parseDouble(OrderCreateF.pre_lng);
        if((lat!=0.0 && lng!=0.0)) {
            latLng = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    private void METHOD_init_views() {
        preferences=new Preferences(view.getContext());
        tvTitle = view.findViewById(R.id.tv_title);
        ivBack =view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        llLoc =view.findViewById(R.id.ll_loc);
        llLoc.setOnClickListener(this);
        tvTitle.setText(""+title);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        ivCurrentLoc = view.findViewById(R.id.iv_current_location);
        ivCurrentLoc.setOnClickListener(this);
        tvAddressTitle = view.findViewById(R.id.tv_address_title);
        tvAddress = view.findViewById(R.id.tv_address);

    }






    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.ll_loc:
                if (TextUtils.isEmpty(tvAddressTitle.getText().toString()))
                {
                    return;
                }
                METHOD_selectLocation(title, tvAddressTitle.getText().toString());
                break;
            case R.id.btn_confirm:
            {
                if (latLng!=null)
                {
                    Bundle bundle=new Bundle();
                    bundle.putString("lat",""+latLng.latitude);
                    bundle.putString("lng",""+latLng.longitude);
                    bundle.putString("address",""+locality);
                    callback.OnItemClick(0,bundle);
                    getActivity().onBackPressed();
                }
            }
                break;
            case R.id.iv_current_location:
                if (latLng==null)
                {
                    GetCurrentLocation();
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.need_to_select_your_pickup_poin));
                    return;
                }
                if (isLatLngBound)
                {
                    ShowLatLngBoundZoom();
                }
                else
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
                break;
        }
    }


    private void METHOD_selectLocation(String title,String query) {
        Fragment f = new SearchLocationF(query,title, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                Log.d(Variables.tag,"title "+bundle.getString("title",""));
                Log.d(Variables.tag,"address "+bundle.getString("address",""));
                Log.d(Variables.tag,"latlong "+bundle.getDouble("lat",0.0)+","+bundle.getDouble("lng",0.0));

                latLng=new LatLng(bundle.getDouble("lat",0.0),bundle.getDouble("lng",0.0));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fl_id, f,"SearchLocation_F").addToBackStack("SearchLocation_F").commit();
    }


    //    apply zoom on map according to pin bounds of map
    private void ShowLatLngBoundZoom() {
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        LatLngBounds bounds = latlngBuilder.build();

        LatLng center = bounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);
        latlngBuilder.include(southWest);
        latlngBuilder.include(northEast);
        if (areBoundsTooSmall(bounds, 300)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private double EARTHRADIUS = 6366198;
    private LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }


    private double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }




    //     when map moving stop it pick address and pick latlng from that address smothly
    String locality= "";
    String locality_two= "";
    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onCameraIdle() {

                latLng = mMap.getCameraPosition().target;


                new AsyncTask<LatLng, Void, Address>() {
                    @SuppressLint("WrongThread")
                    @Override
                    protected Address doInBackground(LatLng... geoPoints) {

                        try {
                            Geocoder geoCoder = new Geocoder(getContext());
                            double latitude = geoPoints[0].latitude;
                            double longitude = geoPoints[0].longitude;
                            List<Address> addresses = geoCoder
                                    .getFromLocation(latitude, longitude, 1);

                            if (addresses != null && addresses.size() > 0)
                                return addresses.get(0);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Address address) {

                        if (address!=null)
                        {
                            locality = ""+address.getAddressLine(0);
                            locality_two = ""+address.getFeatureName();
                            tvAddressTitle.setVisibility(View.VISIBLE);
                            tvAddress.setVisibility(View.VISIBLE);
                        }

                        tvAddressTitle.setText(locality_two);
                        tvAddress.setText(locality);
                    }

                }.execute(latLng);

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        enable_permission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    private void enable_permission(){
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {
            new GpsUtils(getContext()).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                Variables.permissionLocation);
                    }

                }
            });
        }
        else if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(),R.color.newColorWhite));
                  GetCurrentLocation();

        }else {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(),R.color.newColorPrimary));
          }
    }

    //    phone defult navigation bar color handle
    private void SetStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(color);
        }
    }




    //    get current location by using phone GPS
    private void GetCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(Variables.tag, "GetCurrentlocation: inside Not Permissioned");
            enable_permission();
            return;
        }else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        double lat = (latLng.latitude);
                        double lon = (latLng.longitude);
                        preferences.setKeyUserLat(""+lat);
                        preferences.setKeyUserLng(""+lon);
                        mapFragment.onResume();
                        mapFragment.getMapAsync(AddLocationUpdateAddressSelectionF.this);
                        METHOD_setupMap();
                    }
                }
            });


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==Variables.permissionLocation)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(),R.color.newColorWhite));
                    enable_permission();
            }
            else
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(),R.color.newColorPrimary));
              }
        }
    }

    //    init map by custom classes smothly
    private void METHOD_setupMap() {

        MapsInitializer.initialize(view.getContext());
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        configureCameraIdle();
    }


}



