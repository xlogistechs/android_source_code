package com.qboxus.godelivery.ActivitiesFragments.EditProfile;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.CountryAndGoodsF;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;


public class EditPhoneNoF extends RootFragment implements View.OnClickListener {

    FragmentClickCallback callBack;
    View view;
    String countryId="";
    EditText etCountryCode, etPhoneNumber;
    CountryCodePicker ccp;
    Preferences preferences;
    public EditPhoneNoF(FragmentClickCallback callBack) {
        this.callBack = callBack;
    }

    public EditPhoneNoF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_edit_phone_no_, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.edit_send_code).setOnClickListener(this);
    }

    private void InitControl() {
        etCountryCode =view.findViewById(R.id.et_country_code);
        etPhoneNumber =view.findViewById(R.id.et_phoneno);
        ccp=view.findViewById(R.id.ccp);
        preferences=new Preferences(view.getContext());
        ccp.registerPhoneNumberTextView(etPhoneNumber);

        view.findViewById(R.id.iv_back).setOnClickListener(this);
        etCountryCode.setOnClickListener(this);
        SetupScreenData();
    }

    private void SetupScreenData() {
        countryId=preferences.getKeyUserCountryId();
        ccp.setFullNumber(""+preferences.getKeyUserPhone());
        etCountryCode.setText(""+ccp.getSelectedCountryCodeWithPlus());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.edit_send_code:
                Functions.HideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etPhoneNumber.getText().toString()))
                {
                    etPhoneNumber.setError(""+getResources().getString(R.string.cant_empty));
                    etPhoneNumber.setFocusable(true);
                    return;
                }
                if(!ccp.isValid())
                {
                    etPhoneNumber.setError(""+getResources().getString(R.string.invalid_phone_no));
                    etPhoneNumber.setFocusable(true);
                    return;
                }

                String phoneNo= etPhoneNumber.getText().toString();
                if (phoneNo.charAt(0)=='0')
                {
                    phoneNo=phoneNo.substring(1);
                }
                phoneNo=phoneNo.replace("+","");
                phoneNo=phoneNo.replace(ccp.getSelectedCountryCode(),"");
                phoneNo=ccp.getSelectedCountryCodeWithPlus()+phoneNo;
                phoneNo=phoneNo.replace(" ","");
                phoneNo=phoneNo.replace("(","");
                phoneNo=phoneNo.replace(")","");
                phoneNo=phoneNo.replace("-","");



                CallApi_verifyforgotPasswordCode(phoneNo);

            }
            break;
            case R.id.et_country_code:
            {
                METHOD_openTypes_of_Goods_F();
            }
            break;
            default:
                break;
        }
    }

    private void ShowVerifyEditPhoneAuth(String phoneNo) {
        EditVerifyPhoneNoF frg_ment = new EditVerifyPhoneNoF();
        FragmentTransaction transaction =getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("fullphoneNo",""+phoneNo);
        args.putString("countryid",""+countryId);
        frg_ment.setArguments(args);
        transaction.addToBackStack("VerifyPhoneNo_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.edit_phone_no_container, frg_ment,"VerifyPhoneNo_F").commit();
    }

    private void CallApi_verifyforgotPasswordCode(String phoneNo) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("phone", phoneNo);
            sendobj.put("verify", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.changePhoneNo, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            ShowVerifyEditPhoneAuth(phoneNo);
                        }else {
                            Functions.ShowToast(view.getContext(), ""+respobj.optString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void METHOD_openTypes_of_Goods_F() {
        CountryAndGoodsF f = new CountryAndGoodsF(true, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle!=null){
                    String country_code = bundle.getString("selected_country_code");
                    String country_id = bundle.getString("selected_country_id");
                    String country_ios = bundle.getString("selected_country_ios");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countryId=country_id;
                            etCountryCode.setText(""+country_code);
                            ccp.setDefaultCountryUsingNameCode(""+country_ios);
                            ccp.setCountryForNameCode(""+country_ios);
                        }
                    },200);
                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.edit_phone_no_container, f).addToBackStack(null).commit();
    }


    @Override
    public void onDetach() {
        callBack.OnItemClick(0,new Bundle());
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


