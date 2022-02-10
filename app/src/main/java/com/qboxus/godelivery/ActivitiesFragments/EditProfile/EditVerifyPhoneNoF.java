package com.qboxus.godelivery.ActivitiesFragments.EditProfile;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;
import org.json.JSONObject;


public class EditVerifyPhoneNoF extends RootFragment implements View.OnClickListener  {

    private PinView etCode;
    TextView txtSubTitle;
    private View view;
    String countryId ="", fullPhoneNo ="";
    Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_verify_phone_no_, container, false);
        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views(){
        txtSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        preferences=new Preferences(view.getContext());
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_verify_code).setOnClickListener(this);
        SetupScreenData();
    }

    private void SetupScreenData() {
        fullPhoneNo =getArguments().getString("fullphoneNo");
        countryId =getArguments().getString("countryid");

        txtSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve)+" "+ fullPhoneNo);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_verify_code:
                Functions.HideSoftKeyboard(getActivity());
                if(TextUtils.isEmpty(etCode.getText().toString()))
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.enter_verification_code));
                    return;
                }
                if(etCode.getText().toString().length()!=4)
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.incomplete_verification_code));
                    return;
                }
                CallApi_verifyEditPhoneNo(etCode.getText().toString());
                break;
            default:
                break;
        }
    }


    private void CallApi_verifyEditPhoneNo(String code) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id",preferences.getKeyUserId());
            sendobj.put("phone", fullPhoneNo);
            sendobj.put("verify", "1");
            sendobj.put("code",code);
        } catch (Exception e) {
            Log.d(Variables.tag,"Exception "+e);
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.verifyRegisterphoneAuthcode, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            etCode.setText("");
                            CallApi_editProfile();
                        }else {
                            Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.verification_status),""+respobj.getString("msg"));
                        }

                    } catch (Exception e) {
                        Log.d(Variables.tag,"Exception "+e);
                    }
                }
            }
        });
    }


    private void CallApi_editProfile(){
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id",preferences.getKeyUserId());
            sendobj.put("country_id", ""+ countryId);
            sendobj.put("dob", ""+preferences.getKeyUserDateOfBirth());

        } catch (Exception e) {
            Log.d(Variables.tag,"Exception "+e);
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.editProfile, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                            METHOD_saveUserDetails(userobj,countryobj);
                        }else {
                            Functions.ShowToast(view.getContext(),respobj.optString("msg"));
                        }

                    } catch (Exception e) {
                        Log.d(Variables.tag,"Exception "+e);
                    }
                }
            }
        });
    }


    private void METHOD_saveUserDetails(JSONObject userobj,JSONObject countryobj) {
        String fname = ""+userobj.optString("first_name");
        String lname = ""+userobj.optString("last_name");
        String email = ""+userobj.optString("email");
        String username = ""+userobj.optString("username");
        String dob = ""+userobj.optString("dob");
        String country = ""+countryobj.optString("name");
        String country_id = ""+countryobj.optString("id");
        String country_Ios = ""+countryobj.optString("iso");
        String country_Code = ""+countryobj.optString("country_code");
        String phone = ""+userobj.optString("phone");


        preferences.setKeyUserFirstName(fname);
        preferences.setKeyUserLastName(lname);
        preferences.setKeyUserEmail(email);
        preferences.setKeyUserName(username);
        preferences.setKeyUserDateOfBirth(dob);
        preferences.setKeyUserCountry(country);
        preferences.setKeyUserCountryId(country_id);
        preferences.setKeyUserCountryIOS(country_Ios);
        preferences.setKeyCountryCode(country_Code);
        preferences.setKeyUserPhone(phone);
        getActivity().onBackPressed();
    }


    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}
