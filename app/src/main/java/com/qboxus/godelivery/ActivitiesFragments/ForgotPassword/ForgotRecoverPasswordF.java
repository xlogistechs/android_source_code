package com.qboxus.godelivery.ActivitiesFragments.ForgotPassword;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotRecoverPasswordF extends RootFragment implements View.OnClickListener {

    private EditText etNewPassword, etConfirmPassword;
    private View view;
    private ImageView ivHide, ivConfirmHide;
    private Boolean check = true, confirmCheck = true;


    public ForgotRecoverPasswordF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_forgot_recover_password, container, false);
        METHOD_init_views();

        return view;
    }


    private void METHOD_init_views(){

        etNewPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ivHide = view.findViewById(R.id.iv_hide);
        ivConfirmHide = view.findViewById(R.id.iv_confirm_hide);

        view.findViewById(R.id.ll_hide).setOnClickListener(this);
        view.findViewById(R.id.ll_confirm_hide).setOnClickListener(this);

        view.findViewById(R.id.btn_reset_pass).setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;

            case R.id.btn_reset_pass:
                if(TextUtils.isEmpty(etNewPassword.getText().toString()))
                {
                    etNewPassword.setError(getResources().getString(R.string.cant_empty));
                    etNewPassword.setFocusable(true);
                    return;
                }
                if (etNewPassword.getText().toString().length()<6)
                {
                    etNewPassword.setError(view.getContext().getString(R.string.invalid_password));
                    etNewPassword.setFocusable(true);
                    return;
                }
                if(TextUtils.isEmpty(etConfirmPassword.getText().toString()))
                {
                    etConfirmPassword.setError(getResources().getString(R.string.cant_empty));
                    etConfirmPassword.setFocusable(true);
                    return;
                }
                if(!(etNewPassword.getText().toString().equalsIgnoreCase(etConfirmPassword.getText().toString())))
                {
                    etConfirmPassword.setError(getResources().getString(R.string.password_must_match));
                    etNewPassword.setFocusable(true);
                    return;
                }
                CallApi_changeForgotPassword();
                break;
            case R.id.ll_hide:
                if (check){
                    etNewPassword.setTransformationMethod(null);
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    check = false;
                    etNewPassword.setSelection(etNewPassword.length());
                }else {
                    etNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    check = true;
                    etNewPassword.setSelection(etNewPassword.length());
                }
                break;
            case R.id.ll_confirm_hide:
                if (confirmCheck){
                    etConfirmPassword.setTransformationMethod(null);
                    ivConfirmHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    confirmCheck = false;
                    etConfirmPassword.setSelection(etConfirmPassword.length());
                }else {
                    etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivConfirmHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    confirmCheck = true;
                    etConfirmPassword.setSelection(etConfirmPassword.length());
                }
                break;


            default:
                break;
        }
    }


    private void CallApi_changeForgotPassword() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", getArguments().getString("email"));
            sendobj.put("password", etNewPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.changeForgotPassword, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                           Functions.clearFragment(getActivity().getSupportFragmentManager());

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
