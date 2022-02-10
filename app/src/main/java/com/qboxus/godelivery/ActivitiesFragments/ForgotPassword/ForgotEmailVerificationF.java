package com.qboxus.godelivery.ActivitiesFragments.ForgotPassword;

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
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotEmailVerificationF extends RootFragment implements View.OnClickListener {

    private PinView etCode;
    TextView txtSubTitle;
    private View view;

    public ForgotEmailVerificationF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_forgot_email_verification, container, false);
        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views(){

        txtSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_verify_code).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);

        SetupScreenData();
    }

    private void SetupScreenData() {
        txtSubTitle.setText(view.getContext().getString(R.string.check_your_email_message_we_have)+" "+getArguments().getString("email"));
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
                    Functions.ShowToast(view.getContext(),view.getContext().getString(R.string.enter_verification_code));
                    return;
                }
                if(etCode.getText().toString().length()!=4)
                {
                    Functions.ShowToast(view.getContext(),view.getContext().getString(R.string.incomplete_verification_code));
                    return;
                }

                CallApi_verifyforgotPasswordCode();
                break;
            default:
                break;
        }
    }

    private void ShowChangeForgot() {
        ForgotRecoverPasswordF frg_ment = new ForgotRecoverPasswordF();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("email",getArguments().getString("email"));
        frg_ment.setArguments(args);
        transaction.addToBackStack("ChangePassword_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.fl_id, frg_ment,"ChangePassword_F").commit();
    }

    private void CallApi_verifyforgotPasswordCode() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", getArguments().getString("email"));
            sendobj.put("code", etCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.verifyForgotPasswordcode, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            ShowChangeForgot();
                        }else {
                            Functions.ShowToast(view.getContext(),""+respobj.getString("msg"));
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
