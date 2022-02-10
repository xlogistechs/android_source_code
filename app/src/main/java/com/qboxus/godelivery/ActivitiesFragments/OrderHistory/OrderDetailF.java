package com.qboxus.godelivery.ActivitiesFragments.OrderHistory;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.qboxus.godelivery.ActivitiesFragments.OrderTracking.OrderTrackingA;
import com.qboxus.godelivery.ChatModule.ChatA;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.OrderModel;
import com.qboxus.godelivery.R;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class OrderDetailF extends RootFragment implements View.OnClickListener {

    private View view;
    PopupWindow popupWindow;
    FrameLayout btnChat;
    LinearLayout btnTrackOrder, tabUnreadCount;
    ImageView ivBack, ivOptions;
    CircleImageView imgDriver;
    Preferences preferences;
    PorterShapeImageView imgDetailMap;
    TextView tvPickupLoc, tvDropoffLoc, tvDropoffTime, tvRideFare, tvDriverName, tvSenderName, tvReceiverName,
            tvSenderPhone, tvReceiverPhone, tvPackageType, tvPackageWeight, tvUnreadCount;
    ScaleRatingBar simpleRatingBar;
    RelativeLayout tabRating, tabTrackOrder;
    String orderStatus;
    View viewLine;
    int itemPos;
    boolean isRefreshApi=false;
    OrderModel model;

    Boolean isFromActive = true;
    FragmentClickCallback callBack;
    public OrderDetailF(Boolean isFromActive, FragmentClickCallback callBack) {
        this.isFromActive = isFromActive;
        this.callBack = callBack;
    }

    public OrderDetailF() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_detail, null);

        METHOD_init_views();


        return view;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("type"))
            {
                CallApi_showRiderOrderDetails(false);
            }

        }
    };


    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    model = (OrderModel) bundle.getSerializable("model");
                    itemPos = bundle.getInt("item_pos");
                    UpdateScreenData();
                    CallApi_showRiderOrderDetails(false);

                }
            }
        },200);
    }

    private void UpdateScreenData() {
        Glide.with(view.getContext())
                .load(ApiUrl.baseUrl +model.getMapImg())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgDetailMap);

        tvSenderName.setText(model.getSenderName());
        tvSenderPhone.setText(model.getSenderPhone());
        tvReceiverName.setText(model.getReceiverName());
        tvReceiverPhone.setText(model.getReceiverPhone());
        tvPackageType.setText(model.getPackageType());
        tvPackageWeight.setText(model.getPackageSize());

        if (model.getNotificationCount()==0)
        {
            tabUnreadCount.setVisibility(View.GONE);
        }
        else
        {
            tabUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(""+model.getNotificationCount());
        }
        int viewSize= (int) view.getContext().getResources().getDimension(R.dimen._65sdp);
        if (model.getPickupExtraDetail().equalsIgnoreCase("null"))
        {
            tvPickupLoc.setText(model.getPickupAddress());
        }
        else
        {
            viewSize= (int) (viewSize+view.getContext().getResources().getDimension(R.dimen._15sdp));
            tvPickupLoc.setText(model.getPickupAddress() +" , "+model.getPickupExtraDetail());
        }

        if (model.getDropoffExtraDetail().equalsIgnoreCase("null"))
        {
            tvDropoffLoc.setText(model.getDropoffAddress());
        }
        else
        {
            viewSize= (int) (viewSize+view.getContext().getResources().getDimension(R.dimen._15sdp));
            tvDropoffLoc.setText(model.getDropoffAddress() +" , "+model.getDropoffExtraDetail());
        }
        int width= (int) view.getContext().getResources().getDimension(R.dimen._2sdp);
        Functions.setWidthAndHeight(view.getContext(), (View) viewLine,width, (int) viewSize);

        tvDropoffTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","dd MMM hh:mm a",model.getCreatedTime()));
        tvRideFare.setText(preferences.getKeyCurrencySymbol()+" "+model.getFinalFare());
        if ((TextUtils.isEmpty(model.getDriverImg()) || model.getDriverImg().equalsIgnoreCase("null"))
                && (TextUtils.isEmpty(model.getDriverName()) || model.getDriverName().equalsIgnoreCase("null"))
                && (TextUtils.isEmpty(model.getDriverId()) || model.getDriverId().equalsIgnoreCase("null") || model.getDriverId().equalsIgnoreCase("0")))
        {
            tabRating.setVisibility(View.GONE);
        }
        else
        {
            tabRating.setVisibility(View.VISIBLE);
            Glide.with(view.getContext())
                    .load(ApiUrl.baseUrl +model.getDriverImg())
                    .placeholder(R.drawable.ic_profile_gray)
                    .error(R.drawable.ic_profile_gray)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgDriver);
            tvDriverName.setText(""+model.getDriverName());
        }
        orderStatus = ""+model.getOrderStatus();
        if (isFromActive){
            simpleRatingBar.setVisibility(View.GONE);
            if (orderStatus.equals("1"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("2"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("3"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("4"))
            {
                tabTrackOrder.setVisibility(View.GONE);
            }
        }
        else
        {
            simpleRatingBar.setVisibility(View.VISIBLE);
            tabTrackOrder.setVisibility(View.GONE);

            try {
                if (Float.valueOf(model.getDriverRating())>0)
                {
                    simpleRatingBar.setRating(Float.valueOf(model.getDriverRating()));
                }
            }
            catch (Exception e){}
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("request_responce");
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
    }

    public void CallApi_showRiderOrderDetails(boolean isprogress) {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", "" + model.getOrderId());
            sendobj.put("user_id", "" + preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isprogress)
            Functions.ShowProgressLoader(view.getContext(), false, false);
        ApiRequest.CallApi(view.getContext(), ApiUrl.showRiderOrderDetails, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (isprogress)
                    Functions.CancelProgressLoader();
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")) {
                            isRefreshApi=true;
                            JSONObject respmsg=respobj.getJSONObject("msg");
                            METHOD_setRiderdetails(respmsg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    public void METHOD_setRiderdetails(JSONObject respobj) {
        try {

            JSONObject Order_Obj = respobj.getJSONObject("Order");
            JSONObject Rider_Order_Obj=respobj.getJSONObject("RiderOrder");
            JSONObject Rider_Obj=respobj.getJSONObject("Rider");

            model.setMapImg(Order_Obj.optString("map"));
            model.setDropoffTime(Rider_Order_Obj.optString("delivered"));

            model.setSenderLat(Order_Obj.optDouble("sender_location_lat",0.0));
            model.setSenderLng(Order_Obj.optDouble("sender_location_long",0.0));
            model.setReceiverLat(Order_Obj.optDouble("receiver_location_lat",0.0));
            model.setReceiverLng(Order_Obj.optDouble("receiver_location_long",0.0));

            model.setOrderId(Integer.valueOf(Order_Obj.optString("id")));
            model.setOrderStatus(Order_Obj.optString("status"));
            model.setPickupAddress(Order_Obj.optString("sender_location_string"));
            model.setPickupTime(Order_Obj.optString("pickup_datetime"));
            model.setCreatedTime(Order_Obj.optString("created"));
            model.setDropoffAddress(Order_Obj.optString("receiver_location_string"));
            model.setFinalFare(Order_Obj.optString("total"));
            model.setSenderName(Order_Obj.optString("sender_name"));
            model.setSenderPhone(Order_Obj.optString("sender_phone"));
            model.setReceiverName(Order_Obj.optString("receiver_name"));
            model.setReceiverPhone(Order_Obj.optString("receiver_phone"));
            model.setDriverId(Rider_Obj.optString("id"));
            model.setDriverName(Rider_Obj.optString("first_name")+" "+Rider_Obj.optString("last_name"));
            model.setDriverImg(Rider_Obj.optString("image"));
            model.setNotificationCount(Order_Obj.getJSONArray("OrderNotification").length());

        }
        catch (Exception e)
        {
            Log.d(Variables.tag,"Exception : "+e);
        }


        JSONObject riderOrder = respobj.optJSONObject("RiderOrder");
        if (!riderOrder.optString("delivered").equals("0000-00-00 00:00:00")) {
            orderStatus = "4";
        } else if (!riderOrder.optString("on_the_way_to_dropoff").equals("0000-00-00 00:00:00")) {
            orderStatus = "3";
        } else if (!riderOrder.optString("pickup_datetime").equals("0000-00-00 00:00:00")) {
            orderStatus = "2";
        } else if (!riderOrder.optString("on_the_way_to_pickup").equals("0000-00-00 00:00:00")) {
            orderStatus = "1";
        } else {
            orderStatus = "0";
        }

        if (isFromActive){
            simpleRatingBar.setVisibility(View.GONE);
            if (orderStatus.equals("1"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("2"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("3"))
            {
                tabTrackOrder.setVisibility(View.VISIBLE);
            }
            else
            if (orderStatus.equals("4"))
            {
                tabTrackOrder.setVisibility(View.GONE);
            }
        }else {
            simpleRatingBar.setVisibility(View.VISIBLE);
            tabTrackOrder.setVisibility(View.GONE);
        }


        UpdateScreenData();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver!=null)
            getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void METHOD_init_views() {
        preferences=new Preferences(view.getContext());
        tabRating =view.findViewById(R.id.tab_rating);
        ivOptions =view.findViewById(R.id.ic_option);
        ivOptions.setOnClickListener(this);
        ivBack =view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        viewLine =view.findViewById(R.id.view_line);
        imgDriver =view.findViewById(R.id.img_driver);
        tvDriverName =view.findViewById(R.id.tv_driver_name);
        tvSenderName =view.findViewById(R.id.tv_sender_name);
        tvSenderPhone =view.findViewById(R.id.tv_sender_phone);
        tvReceiverName =view.findViewById(R.id.tv_dropoff_name);
        tvReceiverPhone =view.findViewById(R.id.tv_dropoff_phone);
        tvPackageType =view.findViewById(R.id.tv_goods_type);
        tvPackageWeight =view.findViewById(R.id.tv_weight);
        imgDetailMap =view.findViewById(R.id.img_my_job);
        imgDetailMap.setOnClickListener(this);
        tvPickupLoc =view.findViewById(R.id.tv_pickup_loc);
        tvDropoffLoc =view.findViewById(R.id.tv_dropoff_loc);
        tvDropoffTime =view.findViewById(R.id.tv_dropoff_time);
        tvRideFare =view.findViewById(R.id.tv_ride_fare);
        simpleRatingBar=view.findViewById(R.id.simpleRatingBar);
        tabTrackOrder =view.findViewById(R.id.tab_track_order);
        btnChat =view.findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(this);
        btnTrackOrder = view.findViewById(R.id.btn_track_order);
        btnTrackOrder.setOnClickListener(this);
        tabUnreadCount =view.findViewById(R.id.tab_unread_count);
        tvUnreadCount =view.findViewById(R.id.tv_unread_count);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.ic_option:
                if (popupWindow !=null)
                {
                    if (!(popupWindow.isShowing()))
                    {
                        initiatePopupWindow();
                    }
                    else
                    {
                        popupWindow.dismiss();
                    }
                }
                else
                {
                    initiatePopupWindow();
                }
                break;
            case R.id.btn_track_order:
                METHOD_openTrackScreen();
                break;
            case R.id.btn_chat:
                METHOD_openChat(model.getDriverId(),model.getDriverName(),
                        model.getDriverImg(),""+model.getOrderId(),preferences.getKeyUserId());
                break;
            case R.id.img_my_job:
                OpenGoogleMapWithDirection();
                break;

        }
    }

    private void OpenGoogleMapWithDirection() {
        String uri = "http://maps.google.com/maps?saddr=" + model.getSenderLat() + "," + model.getSenderLng() + "&daddr=" + model.getReceiverLat() + "," + model.getReceiverLng();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void initiatePopupWindow() {

        try {


            LayoutInflater mInflater= (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mInflater.inflate(R.layout.order_popup_window, null);
            popupWindow = new PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            popupWindow.setOutsideTouchable(true);
            if (preferences.getKeyLocale().equalsIgnoreCase("en"))
            {
                popupWindow.showAtLocation(ivOptions,Gravity.TOP|Gravity.END,0,0);
            }
            else
            {
                popupWindow.showAtLocation(ivOptions,Gravity.TOP|Gravity.START,0,0);
            }
            final TextView tv_report_prob = (TextView) layout.findViewById(R.id.tv_report_problem);
            final TextView tv_cancel_order = (TextView) layout.findViewById(R.id.tv_cancel_order);

            tv_report_prob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    METHOD_openChat("0",view.getContext().getString(R.string.support),
                            "",""+model.getOrderId(),preferences.getKeyUserId());
                    popupWindow.dismiss();
                }
            });

            tv_cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    METHOD_cancelOrder();
                    popupWindow.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void METHOD_openChat(String receiveId,String receiveName,String imgUrl,String orderId,String senderId) {
        ChatA f = new ChatA(new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle.getBoolean("isRead"))
                {
                    CallApiReadMessage();
                }
            }
        },true);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Receiverid", receiveId);
        bundle.putString("Receiver_name", receiveName);
        bundle.putString("Receiver_pic", ApiUrl.baseUrl +imgUrl);
        bundle.putString("Order_id", orderId);
        bundle.putString("senderid", senderId );
        f.setArguments(bundle);
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, f).commit();
    }

    private void CallApiReadMessage() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("order_id", model.getOrderId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(getContext(), ApiUrl.readNotification, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            CallApi_showRiderOrderDetails(false);
                        }
                        else
                        {
                            CallApi_showRiderOrderDetails(false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void METHOD_openTrackScreen() {
        Intent intent = new Intent(getActivity(), OrderTrackingA.class);
        intent.putExtra("order_id", ""+model.getOrderId());
        intent.putExtra("rider_id", ""+model.getDriverId());
        intent.putExtra("UserData", model);
        startActivity(intent);
    }

    private void METHOD_cancelOrder(){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cancel_order_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView txt_yes,txt_no;
        CheckBox ch_verify;
        txt_no=dialog.findViewById(R.id.txt_order_cancel_no);
        txt_yes=dialog.findViewById(R.id.txt_order_cancel_yes);
        ch_verify=dialog.findViewById(R.id.ch_order_cancel_verify);

        txt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ch_verify.isChecked())
                {
                    dialog.dismiss();
                    CallApi_updateOrderStatus();
                }
                else
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.required_check_tick_for_verification));
                }
            }
        });

        ch_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ch_verify.isChecked())
                {
                    txt_yes.setTextColor(ContextCompat.getColor(view.getContext(),R.color.newColorPrimary));
                }
                else
                {
                    txt_yes.setTextColor(ContextCompat.getColor(view.getContext(),R.color.newColorLightHint));
                }
            }
        });

        txt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void CallApi_updateOrderStatus(){
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("order_id", ""+model.getOrderId());
            sendobj.put("status", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false, false);
        ApiRequest.CallApi(getContext(), ApiUrl.updateOrderStatus, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();

                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            Bundle bundle = new Bundle();
                            bundle.putString("status", "cancel");
                            bundle.putInt("item_pos", itemPos);
                            callBack.OnItemClick(0, bundle);
                            getFragmentManager().popBackStackImmediate();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if (isRefreshApi)
        {
            Bundle bundle = new Bundle();
            bundle.putString("status","refresh");
            callBack.OnItemClick(0,bundle);
        }
        super.onDestroy();
        if (popupWindow !=null)
            popupWindow.dismiss();
    }
}
