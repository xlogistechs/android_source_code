package com.qboxus.godelivery.ActivitiesFragments.Setting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.R;

import java.util.Locale;

public class SettingF extends RootFragment implements View.OnClickListener {

    private ImageView btnBack;
    private RelativeLayout llSelLanguage, ivPrivacyPolicy;
    private RelativeLayout rlDarkmode;
    Preferences preferences;
    private Switch switchMode;
    private TextView tvDarkmode;
    FrameLayout frameLayout;
    View view;
    View.OnClickListener navClickListener;

    public SettingF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    public SettingF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);


        METHOD_initViews();

        return view;
    }

    private void METHOD_initViews() {
        preferences=new Preferences(view.getContext());
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(navClickListener);
        llSelLanguage = view.findViewById(R.id.ll_language);
        llSelLanguage.setOnClickListener(this);
        ivPrivacyPolicy = view.findViewById(R.id.ll_privacy_policy);
        ivPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(frameLayout.getId(), new PrivacyPolicyF(),"Privacy_Policy_F").addToBackStack("Privacy_Policy_F").commit();
            }
        });
        rlDarkmode = view.findViewById(R.id.rl_dark_mode);
        rlDarkmode.setOnClickListener(this);
        frameLayout=view.findViewById(R.id.fragment_privacy_policy);
        frameLayout.setOnClickListener(this);
        switchMode = view.findViewById(R.id.switch_mode);
        tvDarkmode = view.findViewById(R.id.tv_darkmode);
        if (preferences.getKeyIsNightMode()) {
            switchMode.setChecked(true);
        }else {
            switchMode.setChecked(false);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_language:
                dialouge_language();
                break;

            case R.id.rl_dark_mode:
                METHOD_changeAppcolorMode();
                if (switchMode.isChecked())
                    switchMode.setChecked(false);
                else
                    switchMode.setChecked(true);
                break;
            default:
                break;
        }
    }



    public void dialouge_language() {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.language_dialouge);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView english = dialog.findViewById(R.id.txt_language_english);
        TextView arabic = dialog.findViewById(R.id.txt_language_arabic);
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                dialog.dismiss();
            }
        });
        arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setLocale(String lang) {
        preferences.setKeyLocale(lang);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        //getActivity().recreate();
        updateActivity();
    }

    public void updateActivity() {
        Intent intent = new Intent(getActivity(), MainA.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void METHOD_changeAppcolorMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            preferences.setKeyIsNightMode(false);
            switchMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        }else {
            preferences.setKeyIsNightMode(true);
            switchMode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }


}
