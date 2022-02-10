package com.qboxus.godelivery.ActivitiesFragments.OrderTracking;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.ModelClasses.OrderModel;
import com.qboxus.godelivery.R;
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class DeliveryCompleteFeedbackF extends Fragment implements View.OnClickListener{


    View view;
    ImageView ivClose;
    LinearLayout btnDone;
    EditText etFeedback;
    RotationRatingBar simpleRatingBar;
    CircleImageView ivProfile;
    Preferences preferences;
    OrderModel orderModel;

    public DeliveryCompleteFeedbackF() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_delivery_complete_feedback_, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        ivClose.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    private void InitControl() {
        preferences=new Preferences(view.getContext());
        orderModel = (OrderModel) getArguments().getSerializable("UserData");
        ivClose =view.findViewById(R.id.iv_close);
        btnDone =view.findViewById(R.id.btn_done);
        etFeedback =view.findViewById(R.id.et_feedback);
        simpleRatingBar=view.findViewById(R.id.simpleRatingBar);
        ivProfile =view.findViewById(R.id.iv_profile);


        SetupScreenData();
    }

    private void SetupScreenData() {


        Glide.with(view.getContext())
                .load(ApiUrl.baseUrl +""+ orderModel.getDriverImg())
                .placeholder(R.drawable.ic_profile_gray)
                .error(R.drawable.ic_profile_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(ivProfile);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_close:
            {

                give_rating("0",""+ etFeedback.getText().toString());

            }
            break;

            case R.id.btn_done:
            {

                give_rating(""+simpleRatingBar.getRating(),""+ etFeedback.getText().toString());
            }
            break;
        }
    }


    public void give_rating(String value,String comment){
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", ""+ orderModel.getOrderId());
            sendobj.put("user_id", ""+preferences.getKeyUserId());
            sendobj.put("driver_id", ""+ orderModel.getDriverId());
            sendobj.put("star", value);
            sendobj.put("comment",comment);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        ApiRequest.CallApi(view.getContext(), ApiUrl.giveRatingsToDriver, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")){

                            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.successfully_give_rating));
                            Functions.clearFragment(getActivity().getSupportFragmentManager());
                            Intent intent=new Intent(view.getContext(), MainA.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }else {
                            Functions.ShowToast(view.getContext(), ""+respobj.optString("msg"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
}