package com.qboxus.godelivery.ActivitiesFragments.EditProfile;

import android.os.Bundle;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePasswordF extends RootFragment implements View.OnClickListener {

    View view;
    ImageView btnBack;
    LinearLayout btnDone;
    RelativeLayout llOldPass, llNewPass, llConfirmPass;
    EditText etOldpass, etNewpass, etConfirmpass;
    private ImageView ivOldHide, ivNewHide, ivConfirmHide;
    private Boolean oldCheck = true, newCheck = true, confirmCheck = true;
    View.OnClickListener navClickListener;
    Preferences preferences;

    public UpdatePasswordF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    public UpdatePasswordF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_password, container, false);
        METHOD_initViews();
        return view;
    }

    private void METHOD_initViews() {
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(navClickListener);
        preferences=new Preferences(view.getContext());
        etOldpass = view.findViewById(R.id.et_old_password);
        etNewpass = view.findViewById(R.id.et_new_password);
        etConfirmpass = view.findViewById(R.id.et_confirm_password);

//        set inputtype here for show/unshow password
        etOldpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNewpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirmpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        btnDone = view.findViewById(R.id.btn_reset_pass);
        btnDone.setOnClickListener(this);

        ivOldHide = view.findViewById(R.id.iv_old_hide);
        ivNewHide = view.findViewById(R.id.iv_new_hide);
        ivConfirmHide = view.findViewById(R.id.iv_confirm_hide);

        llOldPass = view.findViewById(R.id.ll_old_hide);
        llOldPass.setOnClickListener(this);
        llNewPass = view.findViewById(R.id.ll_new_hide);
        llNewPass.setOnClickListener(this);
        llConfirmPass = view.findViewById(R.id.ll_confirm_hide);
        llConfirmPass.setOnClickListener(this);

    }


    private void CallApi_changePassword(){
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("old_password", etOldpass.getText().toString());
            sendobj.put("new_password", etNewpass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(), false, false);
        ApiRequest.CallApi(getContext(), ApiUrl.changePassword, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            getActivity().onBackPressed();
                        }
                        else
                        {
                            Functions.ShowToast(view.getContext(),respobj.optString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_old_hide:
            {
                if (oldCheck){
                    etOldpass.setTransformationMethod(null);
                    ivOldHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    oldCheck = false;
                    etOldpass.setSelection(etOldpass.length());
                }else {
                    etOldpass.setTransformationMethod(new PasswordTransformationMethod());
                    ivOldHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    oldCheck = true;
                    etOldpass.setSelection(etOldpass.length());
                }
            }
                break;
            case R.id.ll_new_hide:
            {
                if (newCheck){
                    etNewpass.setTransformationMethod(null);
                    ivNewHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    newCheck = false;
                    etNewpass.setSelection(etNewpass.length());
                }else {
                    etNewpass.setTransformationMethod(new PasswordTransformationMethod());
                    ivNewHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    newCheck = true;
                    etNewpass.setSelection(etNewpass.length());
                }
            }
            break;
            case R.id.ll_confirm_hide:
            {
                if (confirmCheck){
                    etConfirmpass.setTransformationMethod(null);
                    ivConfirmHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    confirmCheck = false;
                    etConfirmpass.setSelection(etConfirmpass.length());
                }else {
                    etConfirmpass.setTransformationMethod(new PasswordTransformationMethod());
                    ivConfirmHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    confirmCheck = true;
                    etConfirmpass.setSelection(etConfirmpass.length());
                }
            }
                break;
            case R.id.btn_reset_pass:
                if (TextUtils.isEmpty(etOldpass.getText().toString())){
                    etOldpass.setError(getResources().getString(R.string.cant_empty));
                    etOldpass.setFocusable(true);
                    return;
                }
                if (etOldpass.getText().toString().length()<6)
                {
                    etOldpass.setError(view.getContext().getString(R.string.invalid_password));
                    etOldpass.setFocusable(true);
                    return;
                }
                if(etNewpass.getText().toString().equalsIgnoreCase(etOldpass.getText().toString()))
                {
                    etNewpass.setError(getResources().getString(R.string.new_password_must_differ));
                    etNewpass.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etNewpass.getText().toString())){
                    etNewpass.setError(getResources().getString(R.string.cant_empty));
                    etNewpass.setFocusable(true);
                    return;
                }
                if (etNewpass.getText().toString().length()<6)
                {
                    etNewpass.setError(view.getContext().getString(R.string.invalid_password));
                    etNewpass.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etConfirmpass.getText().toString())){
                    etConfirmpass.setError(getResources().getString(R.string.cant_empty));
                    etConfirmpass.setFocusable(true);
                    return;
                }
                if(!(etNewpass.getText().toString().equalsIgnoreCase(etConfirmpass.getText().toString())))
                {
                    etConfirmpass.setError(getResources().getString(R.string.password_must_match));
                    etNewpass.setFocusable(true);
                    return;
                }

                    CallApi_changePassword();
                break;
        }
    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}
