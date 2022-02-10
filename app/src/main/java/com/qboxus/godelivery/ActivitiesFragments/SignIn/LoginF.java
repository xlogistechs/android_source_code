package com.qboxus.godelivery.ActivitiesFragments.SignIn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.godelivery.ActivitiesFragments.ForgotPassword.ForgotPasswordF;
import com.qboxus.godelivery.ActivitiesFragments.SignUp.RegisterPhoneNoF;
import com.qboxus.godelivery.ActivitiesFragments.SignUp.RegisterF;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.ModelClasses.RequestRegisterUserModel;
import com.qboxus.godelivery.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginF extends RootFragment implements View.OnClickListener {

    private EditText etEmail, etPass;
    private ImageView ivHide, ivBack, googleIcon;
    private RelativeLayout llHide;
    private CallbackManager callbackManager;
    TextView tvRegister;
    private int code = 7;
    Preferences preferences;
    private String devicetoken;

    private Boolean check = false;
    private View view;



    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        getfirebase token for Notification
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(Variables.tag, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        devicetoken = task.getResult();
                        Log.d(Variables.tag, devicetoken);
                    }
                });

        view = inflater.inflate(R.layout.fragment_login, null);

        METHOD_init_views();

        return view;
    }



    private void METHOD_init_views(){
        preferences=new Preferences(view.getContext());
        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        googleIcon =view.findViewById(R.id.google_icon);
        Glide.with(view.getContext())
                .load(R.drawable.ic_google)
                .placeholder(R.drawable.ic_google)
                .error(R.drawable.ic_google)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(googleIcon);
        etEmail = view.findViewById(R.id.et_email);
        etPass = view.findViewById(R.id.et_password);
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ivHide = view.findViewById(R.id.iv_hide);
        llHide = view.findViewById(R.id.ll_hide);
        llHide.setOnClickListener(this);

        view.findViewById(R.id.btn_login).setOnClickListener(this);

        TextView tv_forgot_pass = view.findViewById(R.id.tv_forgot_pass);
        tv_forgot_pass.setOnClickListener(this);

        tvRegister = view.findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(this);


        view.findViewById(R.id.ll_fb).setOnClickListener(this);
        view.findViewById(R.id.ll_google).setOnClickListener(this);

        SetUpScreenData();
    }

    private void SetUpScreenData() {
        tvRegister.setText(Html.fromHtml(view.getContext().getString(R.string.dont_have_account)+" "+"<font color='#F54D6E'>" + view.getContext().getString(R.string.signup) + "</font>"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;


            case R.id.ll_hide:
                if (check ){
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_hide));
                    check = false;
                    etPass.setSelection(etPass.length());
                }else if(!check ) {
                    etPass.setTransformationMethod(null);
                    ivHide.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_show));
                    check = true;
                    etPass.setSelection(etPass.length());
                }
                break;


            case R.id.tv_forgot_pass:
            {

                ForgotPasswordF fragment = new ForgotPasswordF();
                FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                transaction.addToBackStack("Forgot_pass_F");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, fragment,"Forgot_pass_F").commit();
            }

                break;

            case R.id.tv_register:
            {
                RegisterF fragment = new RegisterF();
                FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                transaction.addToBackStack("Register_F");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, fragment,"Register_F").commit();
            }
                break;


            case R.id.btn_login:
            {
                if (METHOD_inputValidation())
                    CallApi_Login(etEmail.getText().toString(),
                            etPass.getText().toString(), "");
            }
                break;


            case R.id.ll_fb:
            {
                METHOD_facebooksignin();
            }
            break;


            case R.id.ll_google:
            {

                METHOD_GoogleSignin();
            }
                break;
            default:
                break;
        }
    }


    GoogleSignInClient mGoogleSignInClient;
    private void METHOD_GoogleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);
        if (mGoogleSignInClient!=null)
        {
            mGoogleSignInClient.signOut();
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(view.getContext());

        if (account != null) {

            String id=account.getId();
            String fname=""+account.getGivenName();
            String lname=""+account.getFamilyName();
            String email = account.getEmail();
            String image = ""+account.getPhotoUrl();
            String type = "google";


            String auth_token = ""+account.getIdToken();

            Log.d(Variables.tag, "signInResult:auth_token===" + auth_token);

            RegisterUserIntoApp(id,fname,lname,email,image,auth_token,type);

        }
        else {

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, code);

        }

    }


    private void CallApi_Login(String email, String pass, String token) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", email);
            sendobj.put("password", pass);
            sendobj.put("device_token", devicetoken);
            sendobj.put("role", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.login, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                ParseLoginResponce(resp,false,"");
            }
        });

    }

    private void ParseLoginResponce(String resp,boolean isSocial,String authToken) {
        if (resp!=null){

            try {
                JSONObject respobj = new JSONObject(resp);

                if (respobj.getString("code").equals("200")){

                    JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                    JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                    METHOD_saveUserDetails(userobj,countryobj,isSocial,authToken);

                }else{
                    Log.d(Variables.tag, ""+respobj.getString("msg"));
                    Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.login_status),""+respobj.getString("msg"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private Boolean METHOD_inputValidation(){
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
        return true;
    }



    private void METHOD_saveUserDetails(JSONObject userobj,JSONObject countryobj,boolean isSocial,String authToken) {
        String id = ""+userobj.optString("id");
        String fname = ""+userobj.optString("first_name");
        String lname = ""+userobj.optString("last_name");
        String email = ""+userobj.optString("email");
        String image = ""+userobj.optString("image");
        String phone = ""+userobj.optString("phone");
        String username = ""+userobj.optString("username");
        String dob = ""+userobj.optString("dob");
        String social_type = ""+userobj.optString("social");
        String country = ""+countryobj.optString("name");
        String countryId = ""+countryobj.optString("id");
        String countryIos = ""+countryobj.optString("iso");
        String countryCode = ""+countryobj.optString("country_code");

        preferences.setKeyUserId(id);
        preferences.setKeyUserFirstName(fname);
        preferences.setKeyUserLastName(lname);
        preferences.setKeyUserEmail(email);
        preferences.setKeyUserImage(image);
        preferences.setKeyUserPhone(phone);
        preferences.setKeyUserName(username);
        preferences.setKeyUserDateOfBirth(dob);
        preferences.setKeySocialType(social_type);
        preferences.setKeyUserCountry(country);
        preferences.setKeyUserCountryId(countryId);
        preferences.setKeyUserCountryIOS(countryIos);
        preferences.setKeyCountryCode(countryCode);
        if (isSocial)
        {
            preferences.setKeyUserAuthToken(authToken);
        }
        else
        {
            preferences.setKeyUserAuthToken(userobj.optString("auth_token"));
        }

        preferences.setKeyIsLogin(true);


        startActivity(new Intent(getActivity(), MainA.class));
        getActivity().finish();
    }



    public void METHOD_facebooksignin(){
        LoginManager.getInstance().logOut();
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"));
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.d(Variables.tag, ""+object.toString());

                                try {
                                    String firstname = object.getString("first_name");
                                    String lastname = object.getString("last_name");
                                    String email = object.getString("email");
                                    String user_id = object.getString("id");
                                    String type = "facebook";

                                    String image = "https://graph.facebook.com/"+user_id+"/picture?width=500";

                                   RegisterUserIntoApp(user_id,firstname,lastname,email,image,""+loginResult.getAccessToken().getToken(),type);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                Bundle parametrs = new Bundle();
                parametrs.putString("fields","first_name,last_name,email,id");
                request.setParameters(parametrs);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(Variables.tag, "cancel" );
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.facebook_login_cancel));
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp", "error" );
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.facebook_login_cancel));
            }
        });

    }

    private void RegisterUserIntoApp(String user_id,String firstname, String lastname, String email, String image,String token,String type) {

        RequestRegisterUserModel register_model=new RequestRegisterUserModel();

        register_model.setSocialId(""+user_id);
        register_model.setSocialType(""+type);
        register_model.setFirstName(""+firstname);
        register_model.setLastName(""+lastname);
        register_model.setEmail(""+email);
        register_model.setImage(""+image);
        register_model.setAuthToken(""+token);
        register_model.setDeviceToken(""+devicetoken);

        call_api_for_login(register_model);

    }


    private void call_api_for_login(RequestRegisterUserModel model) {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("social_id", model.getSocialId());
            parameters.put("social",""+model.getSocialType());
            parameters.put("auth_token", ""+model.getAuthToken());
            parameters.put("role", "user");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(view.getContext(),false,false);
        ApiRequest.CallApi(view.getContext(), ApiUrl.registerUser, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                parse_login_data(resp,model);

            }
        });
    }

    public void parse_login_data(String loginData, RequestRegisterUserModel model){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                ParseLoginResponce(loginData,true,model.getAuthToken());

            }else if(code.equals("201")){
                PhoneNumberAuth_fragment(model);
            }else{
                Functions.ShowToast(view.getContext(), ""+jsonObject.optString("msg"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void PhoneNumberAuth_fragment(RequestRegisterUserModel model) {
        RegisterPhoneNoF fragment = new RegisterPhoneNoF();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("UserData",model);
        fragment.setArguments(args);
        transaction.addToBackStack("RegisterPhoneNo_A");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.fl_id, fragment,"RegisterPhoneNo_A").commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Signin
        if (requestCode == code) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id=account.getId();
                String fname=""+account.getGivenName();
                String lname=""+account.getFamilyName();
                String auth_token =  account.getIdToken();
                String email = account.getEmail();
                String image= ""+account.getPhotoUrl();
                String type= "google";

                Log.d(Variables.tag, "signInResult:auth_token =" + auth_token);
                // if we do not get the picture of user then we will use default profile picture


                RegisterUserIntoApp(id,fname,lname,email,image,auth_token,type);
            }
        } catch (ApiException e) {
            Log.d(Variables.tag, "" + e.getStatusCode());
        }

    }


    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}
