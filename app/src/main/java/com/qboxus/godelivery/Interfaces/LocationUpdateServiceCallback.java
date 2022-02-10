package com.qboxus.godelivery.Interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface LocationUpdateServiceCallback {

    void onDataReceived(LatLng data);
}
