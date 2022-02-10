package com.qboxus.godelivery.ActivitiesFragments.SignUp;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.ModelClasses.RequestRegisterUserModel;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterPhoneVerificationF extends RootFragment implements View.OnClickListener  {

    private PinView etCode;
    TextView txtSubTitle;
    private View view;
    RequestRegisterUserModel model;


    public RegisterPhoneVerificationF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_register_phone_verification, container, false);
        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views(){
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");

        txtSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);

        view.findViewById(R.id.iv_back).setOnClickListener(this);

        view.findViewById(R.id.btn_verify_code).setOnClickListener(this);

        SetupScreenData();
    }

    private void SetupScreenData() {
        txtSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve)+" "+model.getPhoneNumber());
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
                CallApi_verifyforgotPasswordCode(etCode.getText().toString());
                break;
            default:
                break;
        }
    }

    private void ShowRegisterAuth() {
        RegisterDOBF frg_ment = new RegisterDOBF();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("UserData",model);
        frg_ment.setArguments(args);
        transaction.addToBackStack("Register_DOB_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.auth_fl_id, frg_ment,"Register_DOB_F").commit();
    }

    private void CallApi_verifyforgotPasswordCode(String code) {
        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("phone", model.getPhoneNumber());
            sendobj.put("verify", "1");
            sendobj.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
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
                            ShowRegisterAuth();
                        }else {
                            Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.verification_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}
