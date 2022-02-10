package com.qboxus.godelivery.ActivitiesFragments.ForgotPassword;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.FragmentTransaction;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotPasswordF extends RootFragment implements View.OnClickListener {

    private EditText etEmail;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views(){
        etEmail = view.findViewById(R.id.et_email);

        view.findViewById(R.id.iv_back).setOnClickListener(this);

        view.findViewById(R.id.btn_reset_pass).setOnClickListener(this);
        view.findViewById(R.id.clickless);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;

            case R.id.btn_reset_pass:
                Functions.HideSoftKeyboard(getActivity());
                if(TextUtils.isEmpty(etEmail.getText().toString()))
                    etEmail.setError(getResources().getString(R.string.cant_empty));
                else
                    CallApi_forgotPassword();
                break;

            default:
                break;
        }
    }

    private void CallApi_forgotPassword(){
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.forgotPassword, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            ShowForgotAuthenticate();

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

    private void ShowForgotAuthenticate() {
        ForgotEmailVerificationF frg_ment = new ForgotEmailVerificationF();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("email", etEmail.getText().toString());
        frg_ment.setArguments(args);
        transaction.addToBackStack("Forgot_Authentication_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.fl_id, frg_ment,"Forgot_Authentication_F").commit();
    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}
