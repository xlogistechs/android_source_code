package com.qboxus.godelivery.ActivitiesFragments.SignUp;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.ModelClasses.RequestRegisterUserModel;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterUserNameF extends RootFragment implements View.OnClickListener {

    View view;
    RequestRegisterUserModel model;
    EditText etUserName;
    Preferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_register_username, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);
        view.findViewById(R.id.btn_submit_registration).setOnClickListener(this);
    }

    private void InitControl() {
        preferences=new Preferences(view.getContext());
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        etUserName =view.findViewById(R.id.et_user_name);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_submit_registration:
                Functions.HideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etUserName.getText().toString()))
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.cant_empty));
                    return;
                }
                if(etUserName.getText().toString().contains(" "))
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.spaces_not_allow));
                    return;
                }

                Call_RegisterUserApi();

            }
            break;
            default:
                break;
        }
    }

    private void Call_RegisterUserApi() {
        if (model.getSocialType().equalsIgnoreCase("email"))
        {
            CallApi_registerUserWithEmail();
        }
        else
        {
            CallApi_registerUserWithSocial();
        }
    }


    private void CallApi_registerUserWithEmail() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", model.getEmail());
            sendobj.put("password", model.getPassword());
            sendobj.put("dob", model.getDateOfBirthday());
            sendobj.put("username", etUserName.getText().toString());
            sendobj.put("country_id", ""+model.getCountryId());
            sendobj.put("phone", model.getPhoneNumber());
            sendobj.put("first_name",model.getFirstName());
            sendobj.put("last_name", model.getLastName());
            sendobj.put("device_token", model.getDeviceToken());
            sendobj.put("role", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getActivity(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.registerUser, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                if (resp != null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                            METHOD_saveUserDetails(userobj,countryobj);
                        }else {
                            Log.d(Variables.tag, ""+respobj.getString("msg"));
                            Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.signup_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void CallApi_registerUserWithSocial() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", model.getEmail());
            sendobj.put("social_id", ""+model.getSocialId());
            sendobj.put("social", ""+model.getSocialType());
            sendobj.put("dob", model.getDateOfBirthday());
            sendobj.put("device_token", model.getDeviceToken());
            sendobj.put("auth_token", model.getAuthToken());
            sendobj.put("phone", model.getPhoneNumber());
            sendobj.put("username", etUserName.getText().toString());
            sendobj.put("country_id", ""+model.getCountryId());
            sendobj.put("first_name",model.getFirstName());
            sendobj.put("last_name", model.getLastName());
            sendobj.put("role", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getActivity(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.registerUser, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                if (resp != null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                            METHOD_saveUserDetails(userobj,countryobj);
                        }else {
                            Log.d(Variables.tag, ""+respobj.getString("msg"));
                            Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.signup_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }



    private void METHOD_saveUserDetails(JSONObject userobj,JSONObject countryobj) {
        String id = ""+userobj.optString("id");
        String fname = ""+userobj.optString("first_name");
        String lname = ""+userobj.optString("last_name");
        String email = ""+userobj.optString("email");
        String image = ""+userobj.optString("image");
        String phone = ""+userobj.optString("phone");
        String username = ""+userobj.optString("username");
        String dob = ""+userobj.optString("dob");
        String country = ""+countryobj.optString("name");
        String countryId = ""+countryobj.optString("id");
        String countryIos = ""+countryobj.optString("iso");
        String socialType = ""+userobj.optString("social");
        String countryCode = ""+countryobj.optString("country_code");

        preferences.setKeyUserId(id);
        preferences.setKeyUserFirstName(fname);
        preferences.setKeyUserLastName(lname);
        preferences.setKeyUserEmail(email);
        preferences.setKeyUserImage(image);
        preferences.setKeyUserPhone(phone);
        preferences.setKeyUserName(username);
        preferences.setKeyUserDateOfBirth(dob);
        preferences.setKeySocialType(socialType);
        preferences.setKeyUserCountry(country);
        preferences.setKeyUserCountryId(countryId);
        preferences.setKeyUserCountryIOS(countryIos);
        preferences.setKeyCountryCode(countryCode);
        if (model.getSocialType().equalsIgnoreCase("email"))
        {
            preferences.setKeyUserAuthToken(userobj.optString("auth_token"));
        }
        else
        {
            preferences.setKeyUserAuthToken(model.getAuthToken());
        }
        preferences.setKeyIsLogin(true);


        Intent intent=new Intent(view.getContext(), MainA.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}