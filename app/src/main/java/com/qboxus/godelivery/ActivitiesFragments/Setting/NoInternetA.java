package com.qboxus.godelivery.ActivitiesFragments.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.InternetCheckCallback;
import com.qboxus.godelivery.R;

import java.util.Locale;

public class NoInternetA extends AppCompatActivity {

    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        preferences=new Preferences(NoInternetA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale(""+preferences.getKeyLocale());
        InitControl();
    }

    private void InitControl() {
        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
            @Override
            public void GetResponse(String requestType, String response) {
                if(response.equalsIgnoreCase("connected")) {
                    finish();
                    overridePendingTransition(R.anim.in_from_top,R.anim.out_from_bottom);
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
    }
}