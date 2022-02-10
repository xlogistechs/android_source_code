package com.qboxus.godelivery.MapClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.MapsInitializer;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapWorker {

    GoogleMap googleMap;
    Context context;

    public Bitmap destinationMarker, carMarker, pickupMarkerBitmap;

    public MapWorker(Context context){
        this.context = context;
    }

    public MapWorker(Context context, GoogleMap googleMap) {
        this.context=context;
        this.googleMap=googleMap;
        carMarker = getMarkerDriverPinView();
        destinationMarker = getMarkerDropOffPinView();
        pickupMarkerBitmap = getMarkerPickupPinView();
    }

    private Bitmap getMarkerPickupPinView() {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_pickup_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerDropOffPinView() {
        View customMarkerView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_dropoff_marker, null);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerDriverPinView() {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_rider_marker, null);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    public Marker add_marker(LatLng latLng,Bitmap markerImage){
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(markerImage)));
        return m;
    }



    long DURATION_MS = 6000;
    Runnable runnable;
    Handler handler;
    public void animateMarker_with_Map(final Marker marker, final double latitude, final double longitude) {

        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

        handler = new Handler();
        final LatLngInterpolator latLngInterpol = new LatLngInterpolator.LinearFixed();
        final Interpolator interpolator = new LinearInterpolator();

        final LatLng startPosition = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        final LatLng endPosition = new LatLng(latitude,longitude);
        final long start = SystemClock.uptimeMillis();

        runnable=new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);
                LatLng latLng = latLngInterpol.interpolate(v,startPosition,endPosition);
                marker.setPosition(latLng);
                if (t < 1)
                    handler.postDelayed(this,16);
            }
        };
        handler.post(runnable);
    }


    boolean isMarkerRotating = false;
    public void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / (DURATION_MS/2));

                    float rot = t * toRotation + (1 - t) * startRotation;
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 46);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }



    public void DrawRoute(Context context, LatLng pickup, LatLng dropoff, GoogleMap mMap, FragmentClickCallback callBack)
    {
        double totalDistence[]={0};
        MapsInitializer.initialize(context);
        GoogleDirection.withServerKey(context.getString(R.string.direction_api_key))
                .from(pickup)
                .to(dropoff)
                .transportMode(TransportMode.DRIVING)
                .optimizeWaypoints(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        ArrayList<LatLng> directionPositionList=new ArrayList<>();
                        for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(i);
                            directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            totalDistence[0] =(Double.valueOf(route.getLegList().get(0).getDistance().getValue())/1000);
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isDone",true);
                            bundle.putDouble("distence",totalDistence[0]);
                            callBack.OnItemClick(0,bundle);

                        }
                        Log.d(Variables.tag,"Check : "+directionPositionList.size());
                        if (directionPositionList.size()>0)
                        {
                            MapAnimator.getInstance().animateRoute(mMap, directionPositionList,true);
                        }else
                        {
                            List<LatLng> polyLineList=new ArrayList<>();
                            polyLineList.add(pickup);
                            polyLineList.add(dropoff);
                            MapAnimator.getInstance().animateRoute(mMap, polyLineList,true);
                            totalDistence[0]= Functions.getDistanceFromLatLonInKm(pickup,dropoff);
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isDone",true);
                            bundle.putDouble("distence",totalDistence[0]);
                            callBack.OnItemClick(0,bundle);
                        }
                        Log.d(Variables.tag,rawBody);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        List<LatLng> polyLineList=new ArrayList<>();
                        polyLineList.add(pickup);
                        polyLineList.add(dropoff);
                        MapAnimator.getInstance().animateRoute(mMap, polyLineList,true);
                        totalDistence[0]= Functions.getDistanceFromLatLonInKm(pickup,dropoff);
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isDone",true);
                        bundle.putDouble("distence",totalDistence[0]);
                        callBack.OnItemClick(0,bundle);
                    }
                });
    }


    public void DrawRoute(Context context, LatLng pickup, LatLng dropoff, FragmentClickCallback callBack)
    {
        double totalDistence[]={0};
        MapsInitializer.initialize(context);
        GoogleDirection.withServerKey(context.getString(R.string.direction_api_key))
                .from(pickup)
                .to(dropoff)
                .transportMode(TransportMode.DRIVING)
                .optimizeWaypoints(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        ArrayList<LatLng> directionPositionList=new ArrayList<>();
                        for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(i);
                            directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            totalDistence[0] =(Double.valueOf(route.getLegList().get(0).getDistance().getValue())/1000);
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isDone",true);
                            bundle.putDouble("distence",totalDistence[0]);
                            callBack.OnItemClick(0,bundle);

                        }
                        if (directionPositionList.size()>0)
                        {

                        }else
                        {
                            List<LatLng> polyLineList=new ArrayList<>();
                            polyLineList.add(pickup);
                            polyLineList.add(dropoff);
                            totalDistence[0]= Functions.getDistanceFromLatLonInKm(pickup,dropoff);
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isDone",true);
                            bundle.putDouble("distence",totalDistence[0]);
                            callBack.OnItemClick(0,bundle);
                        }
                        Log.d(Variables.tag,rawBody);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        List<LatLng> polyLineList=new ArrayList<>();
                        polyLineList.add(pickup);
                        polyLineList.add(dropoff);
                        totalDistence[0]= Functions.getDistanceFromLatLonInKm(pickup,dropoff);
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isDone",true);
                        bundle.putDouble("distence",totalDistence[0]);
                        callBack.OnItemClick(0,bundle);
                    }
                });
    }

    public void Remove_polyline_with_animation(){
        MapAnimator.getInstance().Clear_map_route();
    }



}
