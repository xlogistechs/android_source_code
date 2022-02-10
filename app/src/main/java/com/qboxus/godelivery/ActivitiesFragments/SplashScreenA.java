package com.qboxus.godelivery.ActivitiesFragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.GetStartedA;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.R;

import org.json.JSONObject;

import java.util.Locale;

public class SplashScreenA extends AppCompatActivity {


    private final int SPLASH_DISPLAY_LENGTH = 1000;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences=new Preferences(SplashScreenA.this);

        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setLocale(""+preferences.getKeyLocale());
        setContentView(R.layout.activity_splash_screen);


        Functions.PrintHashKey(SplashScreenA.this);
        Variables.downloadSharedPreferences = getSharedPreferences(Variables.downloadPref, MODE_PRIVATE);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {

                try {
                    Boolean check = preferences.getKeyIsLogin();

                    if (check){
                        /* Create an Intent that will start the Menu-Activity. */

                        if (getIntent().hasExtra("type")) {

                            try {

                                String orderId="";
                                String riderId="";
                                String type="";
                                type=getIntent().getStringExtra("type");
                                Log.d(Variables.tag,"Log "+type);
                                if (type.equalsIgnoreCase("rider"))
                                {
                                    orderId=new JSONObject(""+getIntent().getStringExtra("order")).optString("id");

                                    Log.d(Variables.tag,"Log "+orderId);

                                    try {
                                        riderId=new JSONObject(getIntent().getStringExtra("rider_order")).optString("id");
                                        Log.d(Variables.tag,"Log rider order "+riderId);
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(Variables.tag,"Exception rider_order"+e);
                                    }

                                    try {
                                        riderId=new JSONObject(getIntent().getStringExtra("sender")).optString("id");
                                        Log.d(Variables.tag,"Log reciver "+riderId);
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(Variables.tag,"Exception receiver"+e);
                                    }

                                }
                                else
                                {
                                    orderId=getIntent().getStringExtra("order_id");

                                    Log.d(Variables.tag,"Log "+orderId);
                                }

                                Intent intent = new Intent(SplashScreenA.this, MainA.class);
                                intent.putExtra("order_id", ""+orderId);
                                intent.putExtra("rider_id", ""+riderId);
                                intent.putExtra("type", ""+type);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            catch (Exception e)
                            {
                                startActivity(new Intent(SplashScreenA.this, MainA.class));
                                Log.d(Variables.tag,"Exception : "+e);
                            }

                        }else {
                            startActivity(new Intent(SplashScreenA.this, MainA.class));
                        }
                    }else {
                        startActivity(new Intent(SplashScreenA.this, GetStartedA.class));
                    }

                    handler.removeCallbacks(this);
                    finish();

                }
                catch (Exception e)
                {
                    Log.d(Variables.tag,"error "+e);
                }



            }
        }, SPLASH_DISPLAY_LENGTH);

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

}
