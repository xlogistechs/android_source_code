package com.qboxus.godelivery.ActivitiesFragments.SignUp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.CountryAndGoodsF;
import com.qboxus.godelivery.ModelClasses.RequestRegisterUserModel;
import com.qboxus.godelivery.R;
import com.google.android.gms.tasks.OnCompleteListener;


public class RegisterF extends RootFragment implements View.OnClickListener {

    private EditText tvCountry;
    private EditText etFname, etLname, etEmail, etPass;
    private ImageView ivHide;
    private String deviceToken, countryId ="", countryCode ="", countryIos ="";
    private Boolean check = true;
    private View view;
    private RelativeLayout llHide;
    RequestRegisterUserModel model;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registeration, container, false);

        //Get the unique device token from Firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(Variables.tag, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        deviceToken = task.getResult();
                        Log.d(Variables.tag, deviceToken);
                    }
                });

        METHOD_init_views();


        return view;
    }



    private void METHOD_init_views(){

        model=new RequestRegisterUserModel();
        view.findViewById(R.id.btn_register).setOnClickListener(this);

        tvCountry = view.findViewById(R.id.tv_country);
        tvCountry.setOnClickListener(this);

        etFname = view.findViewById(R.id.et_fname);
        etFname.setSelection(etFname.length());
        etLname = view.findViewById(R.id.et_lname);
        etLname.setSelection(etLname.length());
        etEmail = view.findViewById(R.id.et_email);
        etEmail.setSelection(etEmail.length());
        etPass = view.findViewById(R.id.et_password);
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPass.setSelection(etPass.length());

        ivHide = view.findViewById(R.id.iv_hide);
        llHide = view.findViewById(R.id.ll_hide);
        llHide.setOnClickListener(this);

        view.findViewById(R.id.iv_back).setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getFragmentManager().popBackStackImmediate();
                break;


            case R.id.ll_hide:
                if (check){
                    etPass.setTransformationMethod(null);
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    check = false;
                    etPass.setSelection(etPass.length());
                }else {
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    check = true;
                    etPass.setSelection(etPass.length());
                }
                break;


            case R.id.tv_country:
                Functions.HideSoftKeyboard(getActivity());
                etPass.clearFocus();
                etEmail.clearFocus();
                etFname.clearFocus();
                etLname.clearFocus();
                METHOD_selectCountry();
                break;


            case R.id.btn_register:
                Functions.HideSoftKeyboard(getActivity());
            {
                if (METHOD_inputValidation())
                {
                    model.setEmail( etEmail.getText().toString());
                    model.setFirstName( etFname.getText().toString());
                    model.setLastName( etLname.getText().toString());
                    model.setPassword( etPass.getText().toString());
                    model.setCountryId( ""+ countryId);
                    model.setCountryCode(""+ countryCode);
                    model.setCountryIos(""+ countryIos);
                    model.setDeviceToken(""+ deviceToken);
                    model.setSocialType("email");

                    RegisterPhoneNoF frg_ment = new RegisterPhoneNoF();
                    FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putSerializable("UserData",model);
                    frg_ment.setArguments(args);
                    transaction.addToBackStack("RegisterPhoneNo_A");
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.replace(R.id.fl_id, frg_ment,"RegisterPhoneNo_A").commit();

                }
            }
                break;

        }
    }


    private void METHOD_selectCountry() {
        METHOD_openTypes_of_Goods_F();
    }

    private void ClearFocusAllTextField()
    {
        Functions.HideSoftKeyboard(getActivity());
        etFname.setError(null);
        etLname.setError(null);
        etEmail.setError(null);
        etPass.setError(null);
        tvCountry.setError(null);
    }

    private Boolean METHOD_inputValidation(){
        if (TextUtils.isEmpty(etFname.getText().toString()))
        {
            etFname.setError(view.getContext().getString(R.string.required_first_name));
            etFname.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etLname.getText().toString()))
        {
            etLname.setError(view.getContext().getString(R.string.required_last_name));
            etLname.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText().toString()))
        {
            etEmail.setError(view.getContext().getString(R.string.required_email));
            etEmail.setFocusable(true);
            return false;
        }
        if (!(Functions.isValidEmail(etEmail.getText().toString())))
        {
            etEmail.setError(view.getContext().getString(R.string.invalid_email));
            etEmail.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(etPass.getText().toString()))
        {
            etPass.setError(view.getContext().getString(R.string.required_password));
            etPass.setFocusable(true);
            return false;
        }
        if (etPass.getText().toString().length()<6)
        {
            etPass.setError(view.getContext().getString(R.string.invalid_password));
            etPass.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(""+ countryId))
        {
            tvCountry.setError(view.getContext().getString(R.string.required_country));
            return false;
        }

        return true;
    }



    private void METHOD_openTypes_of_Goods_F() {
        ClearFocusAllTextField();
        CountryAndGoodsF f = new CountryAndGoodsF(true, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle!=null){
                    tvCountry.setText(bundle.getString("selected_country"));
                    countryId = bundle.getString("selected_country_id");
                    countryCode = bundle.getString("selected_country_code");
                    countryIos = bundle.getString("selected_country_ios");

                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.reg_fl_id, f).addToBackStack(null).commit();
    }


    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}
