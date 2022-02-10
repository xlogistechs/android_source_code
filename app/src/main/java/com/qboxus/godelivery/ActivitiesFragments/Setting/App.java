package com.qboxus.godelivery.ActivitiesFragments.Setting;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        Fresco.initialize(getApplicationContext());
    }
}
