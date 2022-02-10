package com.qboxus.godelivery.ActivitiesFragments.MainHome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.qboxus.godelivery.ActivitiesFragments.Setting.NoInternetA;
import com.qboxus.godelivery.ActivitiesFragments.SignIn.LoginF;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.InternetCheckCallback;
import com.qboxus.godelivery.R;

import java.util.Locale;

public class GetStartedA extends AppCompatActivity implements View.OnClickListener {


    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);

//        activity level nightmode apply
        preferences=new Preferences(GetStartedA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setLocale(preferences.getKeyLocale());

        METHOD_init_views();



    }

//        activity level locale apply
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

    private void METHOD_init_views(){
        findViewById(R.id.tv_get_started).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_started:
            {
                LoginF frg_ment = new LoginF();
                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                frg_ment.setArguments(args);
                transaction.addToBackStack("Login");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, frg_ment,"Login").commit();
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
            @Override
            public void GetResponse(String requestType, String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(GetStartedA.this, NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functions.unRegisterConnectivity(GetStartedA.this);
    }

}
