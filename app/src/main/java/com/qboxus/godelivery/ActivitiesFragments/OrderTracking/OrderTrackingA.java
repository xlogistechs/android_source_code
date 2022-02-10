package com.qboxus.godelivery.ActivitiesFragments.OrderTracking;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.CameraPosition;
import com.qboxus.godelivery.ActivitiesFragments.Setting.NoInternetA;
import com.qboxus.godelivery.ChatModule.ChatA;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.InternetCheckCallback;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.MapClasses.MapAnimator;
import com.qboxus.godelivery.MapClasses.MapWorker;
import com.qboxus.godelivery.ModelClasses.OrderModel;
import com.qboxus.godelivery.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class OrderTrackingA extends AppCompatActivity implements
        OnMapReadyCallback,
        OnClickListener {

    private BottomSheetBehavior btsBehavior;

    private LinearLayout llChatCall;
    private ImageView orderCreatedTick, pickupLocTick, orderPickedTick, dropoffLocationTick,
            orderDeliverdTick, btnRouteShow;
    private CircleImageView ivRiderProfile;

    private TextView tvRiderName, tvCreatedTime, tvPickupTime, tvOnTheToPickupCreatedTime, tvDeliveredCreatedTime, tvOnTheToDropoffCreatedTime, tvOnlineStatus;

    private String userId, riderId, riderFname, riderLname, riderPhoneno, riderPic, pickupLat,
            pickupLong, destinationLat, destinationLong, driverLat, driverLong, orderStatus = "0";

    private String orderId;

    Preferences preferences;
    private MapView mapView;
    private GoogleMap googleMap;
    private MapWorker mapWorker;
    private Marker pickupMarker, dropoffMarker, driverMarker;
    private LatLng pickupLatlng, dropoffLatlng, driverLatlng;
    OrderModel model;

//    tracking step
    boolean isRoutShow=false;
    int trackingStep=5;
    int trackingZoom=0;
    float zoom=0f;
    boolean isDefultZoom=false;

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("type"))
                if (!(intent.getExtras().getString("type").equalsIgnoreCase("rider")))
            CallApiShowRiderOrderDetails(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=new Preferences(OrderTrackingA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale(preferences.getKeyLocale());
        setContentView(R.layout.fragment_order_tracking);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_current_location).setOnClickListener(this);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        model=new OrderModel();
        METHOD_setupMap();
        METHOD_findviewbyId();
        orderId = "" + getIntent().getStringExtra("order_id");
        riderId = "" + getIntent().getStringExtra("rider_id");
        CallApiShowRiderOrderDetails(true);
    }




    public void CallApiShowRiderOrderDetails(boolean is_progress) {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", "" + orderId);
            sendobj.put("user_id", "" + riderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (is_progress)
        Functions.ShowProgressLoader(this, false, false);
        ApiRequest.CallApi(this, ApiUrl.showRiderOrderDetails, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (is_progress)
                Functions.CancelProgressLoader();
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")) {
                            METHOD_setRiderdetails(respobj);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        //this.recreate();
    }

    public void METHOD_setRiderdetails(JSONObject respobj) {
        JSONObject riderOrder = respobj.optJSONObject("msg").optJSONObject("RiderOrder");
        JSONObject rider = respobj.optJSONObject("msg").optJSONObject("Rider");
        JSONObject order = respobj.optJSONObject("msg").optJSONObject("Order");

        model.setOrderId(Integer.valueOf(orderId));
        model.setDriverId(rider.optString("id"));
        model.setDriverImg(rider.optString("image"));

        riderId =rider.optString("id");
        riderFname = rider.optString("first_name");
        riderLname = rider.optString("last_name");
        tvRiderName.setText(riderFname + " " + riderLname);
        riderPhoneno = rider.optString("phone");

        Uri uri = Uri.parse(ApiUrl.baseUrl + rider.optString("image"));
        riderPic = ApiUrl.baseUrl + rider.optString("image");
        Glide.with(OrderTrackingA.this)
                .load(uri)
                .placeholder(R.drawable.ic_profile_gray)
                .error(R.drawable.ic_profile_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(ivRiderProfile);
        tvCreatedTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","hh:mm aa",order.optString("created")));

        if (!(riderOrder.optString("on_the_way_to_pickup").equals("0000-00-00 00:00:00")))
            tvPickupTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","hh:mm aa",riderOrder.optString("on_the_way_to_pickup")));

        if (!(riderOrder.optString("pickup_datetime").equals("0000-00-00 00:00:00")))
            tvOnTheToPickupCreatedTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","hh:mm aa",riderOrder.optString("pickup_datetime")));

        if (!(riderOrder.optString("on_the_way_to_dropoff").equals("0000-00-00 00:00:00")))
            tvOnTheToDropoffCreatedTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","hh:mm aa",riderOrder.optString("on_the_way_to_dropoff")));

        if (!(riderOrder.optString("delivered").equals("0000-00-00 00:00:00")))
            tvDeliveredCreatedTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","hh:mm aa",riderOrder.optString("delivered")));

        if (rider.optString("online").equalsIgnoreCase("0"))
        {
            tvOnlineStatus.setText(OrderTrackingA.this.getString(R.string.offline));
            tvOnlineStatus.setTextColor(ContextCompat.getColor(OrderTrackingA.this,R.color.newColorRed));
        }
        else
        {
            tvOnlineStatus.setText(OrderTrackingA.this.getString(R.string.online));
            tvOnlineStatus.setTextColor(ContextCompat.getColor(OrderTrackingA.this,R.color.newColorGreen));
        }

        pickupLat = order.optString("sender_location_lat");
        pickupLong = order.optString("sender_location_long");
        destinationLat = order.optString("receiver_location_lat");
        destinationLong = order.optString("receiver_location_long");
        driverLat = rider.optString("lat");
        driverLong = rider.optString("long");
        userId =order.optString("user_id");

        pickupLatlng = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
        dropoffLatlng = new LatLng(Double.parseDouble(destinationLat), Double.parseDouble(destinationLong));
        driverLatlng = new LatLng(Double.parseDouble(driverLat), Double.parseDouble(driverLong));

        if (!riderOrder.optString("delivered").equals("0000-00-00 00:00:00")) {
            orderStatus = "4";
            RemoveAllPin();
            trackingStep=4;
            trackingZoom=0;
            UpdateRideSetp();
        } else if (!riderOrder.optString("on_the_way_to_dropoff").equals("0000-00-00 00:00:00")) {
            orderStatus = "3";
            RemoveAllPin();
            trackingStep=3;
            trackingZoom=0;
            UpdateRideSetp();
        } else if (!riderOrder.optString("pickup_datetime").equals("0000-00-00 00:00:00")) {
            orderStatus = "2";
            RemoveAllPin();
            trackingStep=2;
            trackingZoom=0;
            UpdateRideSetp();
        } else if (!riderOrder.optString("on_the_way_to_pickup").equals("0000-00-00 00:00:00")) {
            orderStatus = "1";
            RemoveAllPin();
            trackingStep=1;
            trackingZoom=0;
            UpdateRideSetp();
        } else {
            orderStatus = "0";
            RemoveAllPin();
            trackingStep=0;
            trackingZoom=0;
            UpdateRideSetp();
        }

        METHOD_changeRideStatus(orderStatus);

        if (getIntent().hasExtra("type"))
        {
            if (getIntent().getExtras().getString("type").equalsIgnoreCase("rider"))
            {
                METHOD_openChat();
            }
        }
    }

    private void METHOD_setupMap() {

        MapsInitializer.initialize(OrderTrackingA.this);
        mapView.onResume();

        mapView.getMapAsync(this);

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        mapWorker = new MapWorker(OrderTrackingA.this, googleMap);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success = false;
            if (preferences.getKeyIsNightMode()) {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_map));
            } else {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this, R.raw.gray_map));
            }

        } catch (Resources.NotFoundException e) {
            Log.e(Variables.tag, "Can't find style. Error: ", e);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

    }

    private void METHOD_findviewbyId() {
        FrameLayout captain_rkl = (FrameLayout) findViewById(R.id.fl_id);
        tvCreatedTime =findViewById(R.id.tv_created_time);
        btnRouteShow =findViewById(R.id.btn_route_show);
        btnRouteShow.setOnClickListener(this);
        tvPickupTime =findViewById(R.id.tv_pickup_created_time);
        tvRiderName = findViewById(R.id.tv_rider_name);
        ivRiderProfile = findViewById(R.id.iv_rider_profile);
        tvOnlineStatus = findViewById(R.id.tv_online_status);
        orderCreatedTick = findViewById(R.id.order_created_tick);
        pickupLocTick = findViewById(R.id.pickup_loc_tick);
        orderPickedTick = findViewById(R.id.order_picked_tick);
        dropoffLocationTick = findViewById(R.id.dropoff_location_tick);
        orderDeliverdTick = findViewById(R.id.order_deliverd_tick);
        tvOnTheToPickupCreatedTime =findViewById(R.id.tv_on_the_to_pickup_created_time);
        tvDeliveredCreatedTime =findViewById(R.id.tv_delivered_created_time);
        tvOnTheToDropoffCreatedTime =findViewById(R.id.tv_on_the_to_dropoff_created_time);
        findViewById(R.id.rl_chat).setOnClickListener(this);
        findViewById(R.id.rl_call).setOnClickListener(this);

        llChatCall =findViewById(R.id.ll_chat_call);


        btsBehavior = BottomSheetBehavior.from(captain_rkl);
        btsBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    btsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chat:
                METHOD_openChat();
                break;

            case R.id.rl_call:
                METHOD_calltorider();
                break;
            case R.id.iv_back:
                OrderTrackingA.super.onBackPressed();
                break;
            case R.id.iv_current_location:
            {
                if (trackingZoom==1)
                {
                    isDefultZoom=true;
                    trackingZoom=1;
                    UpdateRideSetp();
                }
                else
                {
                    trackingZoom=1;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatlng, Variables.mapZoomLevel));
                }
            }
            break;
            case R.id.btn_route_show:
            {
                if (isRoutShow)
                {
                    btnRouteShow.setImageResource(R.drawable.ic_route_black);
                    isRoutShow=false;
                    UpdateRideSetp();
                }
                else
                {
                    btnRouteShow.setImageResource(R.drawable.ic_route_blue);
                    isRoutShow=true;
                    UpdateRideSetp();
                }
            }
        }
    }

    private void METHOD_openChat() {

        ChatA f = new ChatA();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Receiverid", riderId);
        bundle.putString("Receiver_name", riderFname +" "+ riderLname);
        bundle.putString("Receiver_pic", riderPic);
        bundle.putString("Order_id", ""+ orderId);
        bundle.putString("senderid", userId);
        f.setArguments(bundle);
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_trackorder, f).commit();
    }

    private void METHOD_calltorider() {
        if (TextUtils.isEmpty(riderPhoneno)){
            Functions.ShowToast(OrderTrackingA.this, OrderTrackingA.this.getString(R.string.rider_has_no_number));
        }else {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ riderPhoneno));
            startActivity(callIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("request_responce");
        registerReceiver(broadcastReceiver,intentFilter);

        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
            @Override
            public void GetResponse(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(OrderTrackingA.this, NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver!=null)
        unregisterReceiver(broadcastReceiver);
        Functions.unRegisterConnectivity(OrderTrackingA.this);
    }





    //    draw poliline route between points
    private void drawRoute(Marker pickup, Marker dropoff){

        if(pickup != null && dropoff !=null && isRoutShow){

            mapWorker.DrawRoute(OrderTrackingA.this, pickup.getPosition(), dropoff.getPosition(), googleMap, new FragmentClickCallback() {
                @Override
                public void OnItemClick(int postion, Bundle bundle) {

                }
            });
        }else {
            MapAnimator.getInstance().Clear_map_route();
        }

    }

    public void METHOD_changeRideStatus(String rider_status) {

        switch (rider_status){
            case "0":
                orderCreatedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                pickupLocTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                orderPickedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                dropoffLocationTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                orderDeliverdTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                llChatCall.setVisibility(View.VISIBLE);
                tvOnlineStatus.setVisibility(View.VISIBLE);
                break;

            case "1":
                orderCreatedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                pickupLocTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderPickedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_processing));
                dropoffLocationTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                orderDeliverdTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                llChatCall.setVisibility(View.VISIBLE);
                tvOnlineStatus.setVisibility(View.VISIBLE);
                break;

            case "2":
                orderCreatedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                pickupLocTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderPickedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                dropoffLocationTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_processing));
                orderDeliverdTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_un_select));
                llChatCall.setVisibility(View.VISIBLE);
                tvOnlineStatus.setVisibility(View.VISIBLE);
                break;

            case "3":
                orderCreatedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                pickupLocTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderPickedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                dropoffLocationTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderDeliverdTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_processing));
                llChatCall.setVisibility(View.VISIBLE);
                tvOnlineStatus.setVisibility(View.VISIBLE);
                break;

            case "4":
                orderCreatedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                pickupLocTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderPickedTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                dropoffLocationTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                orderDeliverdTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_color));
                llChatCall.setVisibility(View.GONE);
                tvOnlineStatus.setVisibility(View.INVISIBLE);



                DeliveryCompleteFeedbackF f = new DeliveryCompleteFeedbackF();
                Bundle bundle=new Bundle();
                bundle.putSerializable("UserData",model);
                f.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_bottom,R.anim.out_to_top);
                ft.replace(R.id.fl_trackorder, f,"DeliveryCompleteFeedback_F").addToBackStack("DeliveryCompleteFeedback_F").commit();

                break;
        }


    }

    private void ShowLatLngBoundZoom(Marker... marker) {
        if (marker[0]==null && marker[1]==null)
        {
            return;
        }
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        for (Marker mrk:marker)
        {
            try {
                latlngBuilder.include(mrk.getPosition());
            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
        }


        LatLngBounds bounds = latlngBuilder.build();

        LatLng center = bounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);
        latlngBuilder.include(southWest);
        latlngBuilder.include(northEast);
        if (areBoundsTooSmall(bounds, 300)) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Variables.mapZoomLevel));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
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





    private void UpdateRideSetp() {

        try {

            pickupLatlng = new LatLng(Double.parseDouble(pickupLat),Double.parseDouble(pickupLong));
            dropoffLatlng = new LatLng(Double.parseDouble(destinationLat),Double.parseDouble(destinationLong));
            driverLatlng = new LatLng(Double.parseDouble(driverLat),Double.parseDouble(driverLong));
        }
        catch (Exception e)
        {Log.d(Variables.tag,"Exception : UpdateRideSetp "+e);}


        switch (trackingStep)
        {
            case 0:
            {
//show both points


                if(pickupMarker == null && dropoffMarker ==null)
                {
                    if (( pickupMarker ==null || pickupLatlng ==null ) && (dropoffMarker ==null || dropoffLatlng ==null))
                    {
                        pickupMarker = mapWorker.add_marker(pickupLatlng, mapWorker.pickupMarkerBitmap);
                        dropoffMarker = mapWorker.add_marker(dropoffLatlng, mapWorker.destinationMarker);

                        ShowLatLngBoundZoom(pickupMarker, dropoffMarker);
                        drawRoute(pickupMarker, dropoffMarker);
                    }

                }
                else
                {

                    switch (trackingZoom)
                    {
                        case 0:
                        {
                            ShowLatLngBoundZoom(pickupMarker, dropoffMarker);
                            drawRoute(pickupMarker, dropoffMarker);
                        }
                        break;
                        case 1:
                        {
                            ShowLatLngBoundZoom(pickupMarker, dropoffMarker);
                            drawRoute(pickupMarker, dropoffMarker);
                        }
                        break;

                    }

                }

            }
            break;
            case 1:
            {
//show pickup with driver


                if(driverMarker == null && pickupMarker ==null)
                {
                    if (( driverMarker ==null || driverLatlng ==null ) && (pickupMarker ==null || pickupLatlng ==null))
                    {
                        driverMarker = mapWorker.add_marker(driverLatlng, mapWorker.carMarker);
                        pickupMarker = mapWorker.add_marker(pickupLatlng, mapWorker.pickupMarkerBitmap);

                        ShowLatLngBoundZoom(driverMarker, pickupMarker);
                        mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                        drawRoute(driverMarker, pickupMarker);
                    }

                }
                else
                {

                    switch (trackingZoom)
                    {
                        case 0:
                        {

                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            ShowLatLngBoundZoom(driverMarker, pickupMarker);
                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, pickupMarker);
                        }
                        break;
                        case 1:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            if (isDefultZoom)
                            {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom=false;
                            }
                            else
                            {
                                zoom = googleMap.getCameraPosition().zoom;
                            }

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(driverLatlng)
                                            .zoom(zoom).build()));
                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, pickupMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 2:
            {
//show pickup with driver


                if(driverMarker == null && pickupMarker ==null)
                {
                    if (( driverMarker ==null || driverLatlng ==null ) && (pickupMarker ==null || pickupLatlng ==null))
                    {
                        driverMarker = mapWorker.add_marker(driverLatlng, mapWorker.carMarker);
                        pickupMarker = mapWorker.add_marker(pickupLatlng, mapWorker.pickupMarkerBitmap);

                        ShowLatLngBoundZoom(driverMarker, pickupMarker);
                        mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                        drawRoute(driverMarker, pickupMarker);
                    }

                }
                else
                {

                    switch (trackingZoom)
                    {
                        case 0:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            ShowLatLngBoundZoom(driverMarker, pickupMarker);
                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, pickupMarker);
                        }
                        break;
                        case 1:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            if (isDefultZoom)
                            {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom=false;
                            }
                            else
                            {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(driverLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, pickupMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 3:
            {
//show dropoff with driver


                if(driverMarker == null && dropoffMarker ==null)
                {
                    if (( driverMarker ==null || driverLatlng ==null ) && (dropoffMarker ==null || dropoffLatlng ==null))
                    {
                        driverMarker = mapWorker.add_marker(driverLatlng, mapWorker.carMarker);
                        dropoffMarker = mapWorker.add_marker(dropoffLatlng, mapWorker.destinationMarker);

                        ShowLatLngBoundZoom(driverMarker, dropoffMarker);
                        mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                        drawRoute(driverMarker, dropoffMarker);
                    }

                }
                else
                {

                    switch (trackingZoom)
                    {
                        case 0:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            ShowLatLngBoundZoom(driverMarker, dropoffMarker);
                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, dropoffMarker);
                        }
                        break;
                        case 1:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            if (isDefultZoom)
                            {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom=false;
                            }
                            else
                            {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(driverLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, dropoffMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 4:
            {
//show dropoff with driver

                if(driverMarker == null && dropoffMarker ==null)
                {
                    if (( driverMarker ==null || driverLatlng ==null ) && (dropoffMarker ==null || dropoffLatlng ==null))
                    {
                        driverMarker = mapWorker.add_marker(driverLatlng, mapWorker.carMarker);
                        dropoffMarker = mapWorker.add_marker(dropoffLatlng, mapWorker.destinationMarker);

                        ShowLatLngBoundZoom(driverMarker, dropoffMarker);
                        mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                        drawRoute(driverMarker, dropoffMarker);

                    }

                }
                else
                {

                    switch (trackingZoom)
                    {
                        case 0:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            ShowLatLngBoundZoom(driverMarker, dropoffMarker);
                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, dropoffMarker);
                        }
                        break;
                        case 1:
                        {
                            try {
                                mapWorker.rotateMarker(driverMarker,(float) Functions.getBearingBetweenTwoPoints1(driverMarker.getPosition(), driverLatlng));
                            }catch (Exception e){Log.d(Variables.tag,"Exception : "+e);}
                            if (isDefultZoom)
                            {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom=false;
                            }
                            else
                            {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(driverLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarker_with_Map(driverMarker, driverLatlng.latitude, driverLatlng.longitude);
                            drawRoute(driverMarker, dropoffMarker);
                        }
                        break;

                    }

                }
            }
            break;
        }

    }

    private void RemoveAllPin() {
        if(pickupMarker != null)
        {
            pickupMarker.remove();
            pickupMarker =null;
        }

        if(driverMarker != null)
        {
            driverMarker.remove();
            driverMarker =null;
        }
        if(dropoffMarker != null)
        {
            dropoffMarker.remove();
            dropoffMarker =null;
        }
    }

}