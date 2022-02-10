package com.qboxus.godelivery.ActivitiesFragments.OrderCreate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.CountryAndGoodsF;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainF;
import com.qboxus.godelivery.BottomSheetDialogsFragments.DeliveryTypesBottomSheet;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.MapClasses.MapWorker;
import com.qboxus.godelivery.ModelClasses.DeliveryTypesModel;
import com.qboxus.godelivery.ModelClasses.PackagesSizeSelectionModel;
import com.qboxus.godelivery.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class OrderCreateF extends RootFragment implements View.OnClickListener {

    private ImageView ivBack, ivDelivery;
    private PackagesSizeSelectionModel packageModel;
    private RelativeLayout tabSenderEmail, tabSenderLoc, tabPickupDateTime, tabTitlePickupDatetime;
    TextView tvSenderLoc;
    private TextView tvRecName, tvRecPh, tvRecLoc;
    private RelativeLayout rlRecEmail, tabRecLoc;
    private EditText etSenderName, etSenderPhoneno, etSenderEmail;
    private CountryCodePicker ccpSender, ccpReceiver;
    private TextView tvSenderName, tvSenderPhoneno;
    private EditText etRecName, etRecPhoneno, etRecEmail;
    private EditText etItemDesc;
    private TextView tvDeliveryType, tvGoodsType, tvPickupTime, tvPickupDate,
            tvItemsName, tvDiscount, tvDeliveryCharges;
    private RelativeLayout tabGoodsType;
    private View tabPromoTag;
    private LinearLayout btnNext;
    private TextView txtNext, aditionalDetailsTextCount;
    private Bundle bundle;
    private LinearLayout llFurtherItemDetails, llProceedDetails;
    private EditText etItemName, etPromo;
    private ImageView calandarHelpRight;
    private String goodsTypeId ="", pickupDate ="", pickupDateShow ="",  couponId ="" , couponCode ="", discountValue ="0";
    private ScrollView scrollView;
    private Boolean isBtnFurther = false;
    private View view;
    private TextView tvTotalPay;
    Preferences preferences;
    DatePicker datePicker;
    TimePicker timePicker;
    LinearLayout btnDateTime, btnOk;
    TextView txtDateTime;
    TextView textView;
    String startTime ="", startTimeShow ="";
    Calendar calandarCurrentTime, calandarStartTime;
    int state = 0;
    RelativeLayout tabDeliverySelection;

    private DeliveryTypesModel selectedModel;
    double cost_per_mile = 0;

    private List<DeliveryTypesModel> deliveryTypesList =new ArrayList<>();

    double reqMainTotalPrice =0.0, reqSubTotalPrice =0.0, reqSinglePrice =0.0, reqDeliveryPrice =0.0, reqDiscountPrice =0.0, reqTotalDistence =0.0;

    public OrderCreateF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_create, container, false);

        bundle = getArguments();

        METHOD_init_views();

        return view;
    }



    private void METHOD_init_views() {
        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tabDeliverySelection =view.findViewById(R.id.rl_delivery_selection);
        tabDeliverySelection.setOnClickListener(this);
        ivDelivery =view.findViewById(R.id.iv_delivery);
        tvDeliveryCharges =view.findViewById(R.id.tv_delivery_charges);
        scrollView = view.findViewById(R.id.scrollview_id);
        preferences=new Preferences(view.getContext());

        tvDeliveryType = view.findViewById(R.id.tv_delivery_type);
        tvTotalPay = view.findViewById(R.id.tv_total_payment);
        if (bundle!=null) {
            selectedModel = (DeliveryTypesModel) getArguments().getSerializable("Delivery_model");
            deliveryTypesList = (List<DeliveryTypesModel>) getArguments().getSerializable("Delivery_Type_List");
            packageModel = (PackagesSizeSelectionModel) getArguments().getSerializable("package_model");
            reqDeliveryPrice = bundle.getDouble("total_cost",0.0);
            reqTotalDistence =bundle.getDouble("total_distence",0.0);
            cost_per_mile=Double.valueOf(selectedModel.getChargesPerKM());

            Glide.with(view.getContext())
                    .load(selectedModel.getVehicleImage())
                    .placeholder(R.drawable.ic_delivery)
                    .error(R.drawable.ic_delivery)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(ivDelivery);
            tvDeliveryType.setText(""+ selectedModel.getVehicleName());

        }


        calandarHelpRight =view.findViewById(R.id.calandar_help_right);
        tabSenderEmail = view.findViewById(R.id.tab_sender_email);
        tvSenderLoc = view.findViewById(R.id.tv_sender_loc);
        tvSenderLoc.setText(preferences.getKeyPickupAdress());

        tabSenderLoc = view.findViewById(R.id.tab_sender_loc);
        tabSenderLoc.setOnClickListener(this);
        tabPickupDateTime = view.findViewById(R.id.tab_pickup_date_time);
        tabPickupDateTime.setOnClickListener(this);
        tabTitlePickupDatetime =view.findViewById(R.id.tab_title_pickup_datetime);

        tvRecName = view.findViewById(R.id.tv_rec_name);
        tvRecPh = view.findViewById(R.id.tv_rec_ph);
        tvRecLoc = view.findViewById(R.id.tv_rec_location);
        tvRecLoc.setText(preferences.getKeyDropoffAdress());

        rlRecEmail = view.findViewById(R.id.rl_rec_email);
        tabRecLoc = view.findViewById(R.id.tab_rec_loc);
        tabRecLoc.setOnClickListener(this);
        tabPromoTag =view.findViewById(R.id.tab_promo_tag);
        tabPromoTag.setOnClickListener(this);
        etSenderName = view.findViewById(R.id.et_sender_name);
        etSenderName.setText(preferences.getKeyUserName());
        etSenderPhoneno = view.findViewById(R.id.et_sender_phoneno);
        etSenderPhoneno.setText(preferences.getKeyUserPhone());
        etSenderEmail = view.findViewById(R.id.et_sender_email);
        etSenderEmail.setText(preferences.getKeyUserEmail());

        tvSenderName =view.findViewById(R.id.tv_sender_name);
        tvSenderPhoneno =view.findViewById(R.id.tv_sender_phoneno);

        etRecName = view.findViewById(R.id.et_rec_name);
        etRecPhoneno = view.findViewById(R.id.et_rec_ph);
        etRecEmail = view.findViewById(R.id.et_rec_email);


//        sender reciver number validation and verification handle
        ccpSender =new CountryCodePicker(view.getContext());
        ccpSender.registerPhoneNumberTextView(etSenderPhoneno);
        ccpSender.setCountryForNameCode(preferences.getKeyUserCountryIOS());
        ccpReceiver =new CountryCodePicker(view.getContext());
        ccpReceiver.registerPhoneNumberTextView(etRecPhoneno);
        ccpReceiver.setCountryForNameCode(preferences.getKeyUserCountryIOS());

        tabGoodsType = view.findViewById(R.id.tab_goods_type);
        tabGoodsType.setOnClickListener(this);
        tvGoodsType = view.findViewById(R.id.tv_goods_type);
        tvGoodsType.setTextColor(getResources().getColor(R.color.newColorDarkHint));

        etItemDesc = view.findViewById(R.id.et_item_desc);
        tvDiscount = view.findViewById(R.id.tv_discount);

        etItemName = view.findViewById(R.id.et_item_name);
        tvItemsName = view.findViewById(R.id.tv_items_name);

        tvPickupDate = view.findViewById(R.id.tv_pickup_date);
        tvPickupTime = view.findViewById(R.id.tv_pickup_time);

        etPromo = view.findViewById(R.id.et_promo);
        etPromo.setOnClickListener(this);

        llFurtherItemDetails = view.findViewById(R.id.ll_further_items_details);
        llProceedDetails = view.findViewById(R.id.ll_proceed_details);

        btnNext = view.findViewById(R.id.btn_next);
        txtNext = view.findViewById(R.id.txt_next);
        btnNext.setOnClickListener(this);
        aditionalDetailsTextCount =view.findViewById(R.id.aditional_details_text_count);


        etItemDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length=0;
                if (charSequence!=null)
                    length=charSequence.toString().length();
                setUpAddtionalInfoCount(length);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setUpAddtionalInfoCount(int length) {
        aditionalDetailsTextCount.setText(""+view.getContext().getString(R.string.maximum_character)+": "+"("+length+"/150)");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                if (txtNext.getText().toString().equals(view.getContext().getString(R.string.proceed)))
                {

                    METHOD_adjustViewsforFurther();
                }
                else
                {
                    if (IsSure)
                    {
                        getActivity().onBackPressed();
                    }
                    else
                    {
                        getFragmentManager().popBackStackImmediate();
                    }
                }
                break;
            case R.id.tab_promo_tag:
            {
                ShowPromoCodeDialuge();
            }
                break;
            case R.id.tab_sender_loc:
            {
                OpenAddLocationAddressSelectionView(view.getContext().getString(R.string.pickup_location),preferences.getKeyPickupLat(),preferences.getKeyPickupLng(),preferences.getKeyPickupAdress());
            }
                break;
            case R.id.tab_rec_loc:
            {
                OpenAddLocationAddressSelectionView(view.getContext().getString(R.string.dropoff_location),preferences.getKeyDropoffLat(),preferences.getKeyDropoffLng(),preferences.getKeyDropoffAdress());
            }
                break;
            case R.id.tab_goods_type:
                Functions.HideSoftKeyboard(getActivity());
                METHOD_openTypes_of_Goods_F();
                break;

            case R.id.tab_pickup_date_time:
                METHOD_openDateTimePicker();
                break;
            case R.id.rl_delivery_selection:
            {
                METHOD_showDeliveryTypes_Bts();
            }
                break;
            case R.id.btn_next:
                if (!isBtnFurther) {

                    Functions.HideSoftKeyboard(getActivity());
                    if (METHOD_checkValidations()){
                        METHOD_adjustViewsforProceed();
                    }
                }else
                    {
                        calandarCurrentTime = Calendar.getInstance();

                        if (calandarStartTime ==null )
                        {
                            calandarHelpRight.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.newColorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.smoothScrollTo(scrollView.FOCUS_UP, 0);
                                }
                            });
                            Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.order_create_status),
                                    view.getContext().getString(R.string.please_select_pickup_date));
                            return;
                        }

                        if (calandarCurrentTime.after(calandarStartTime) || calandarCurrentTime.compareTo(calandarStartTime)==0)
                        {
                            calandarHelpRight.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.newColorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.smoothScrollTo(scrollView.FOCUS_UP, 0);
                                }
                            });
                            Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.order_create_status),
                                    view.getContext().getString(R.string.please_select_pickup_date));
                            return;
                        }

                        if (reqTotalDistence ==0.0f)
                        {
                            Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.order_create_status),
                                    view.getContext().getString(R.string.total_distence_must_be_non_zero));
                            return;
                        }
                        if (reqMainTotalPrice ==0.0f)
                        {
                            Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.order_create_status),
                                    view.getContext().getString(R.string.total_payment_must_be_non_zero));
                            return;
                        }
                        CallApi_placeOrder();
                }
                break;
        }
    }

    public static String pre_lat,pre_lng,pre_address;

    private void OpenAddLocationAddressSelectionView(String title,String lat,String lng,String address) {
        Functions.HideSoftKeyboard(getActivity());
        pre_lat=lat;pre_lng=lng;pre_address=address;
        AddLocationUpdateAddressSelectionF f = new AddLocationUpdateAddressSelectionF(title, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (bundle!=null){

                            if (title.equals(view.getContext().getString(R.string.pickup_location)))
                            {
                                preferences.setKeyPickupLat(bundle.getString("lat"));
                                preferences.setKeyPickupLng(bundle.getString("lng"));
                                preferences.setKeyPickupAdress(bundle.getString("address"));
                            }
                            else
                            {
                                preferences.setKeyDropoffLat(bundle.getString("lat"));
                                preferences.setKeyDropoffLng(bundle.getString("lng"));
                                preferences.setKeyDropoffAdress(bundle.getString("address"));
                            }

                            UpDateCalculatedPrice(true);
                        }
                    }
                },200);
            }
        });
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, f,"AddLocationAddressSelection_F").addToBackStack("AddLocationAddressSelection_F").commit();
    }

    private void UpDateCalculatedPrice(boolean isDistenceCalculation) {
        LatLng pickup_latlong=new LatLng(Double.valueOf(preferences.getKeyPickupLat()),Double.valueOf(preferences.getKeyPickupLng()));
        LatLng dropoff_latlng=new LatLng(Double.valueOf(preferences.getKeyDropoffLat()),Double.valueOf(preferences.getKeyDropoffLng()));

        if (isDistenceCalculation)
        {
            reqTotalDistence = Functions.getDistanceFromLatLonInKm(pickup_latlong,dropoff_latlng);
            new MapWorker(view.getContext()).DrawRoute(view.getContext(), pickup_latlong, dropoff_latlng, new FragmentClickCallback() {
                @Override
                public void OnItemClick(int postion, Bundle bundle) {
                    if (bundle.getBoolean("isDone",false))
                    {
                        reqTotalDistence =bundle.getDouble("distence",0.0);


                        reqDeliveryPrice =( (cost_per_mile* reqTotalDistence)+Double.valueOf(packageModel.getPrice()));

                        reqMainTotalPrice = reqDeliveryPrice;
                        reqSubTotalPrice = reqDeliveryPrice;
                        tvDeliveryCharges.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqDeliveryPrice));
                        tvTotalPay.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqSubTotalPrice));
                        tvRecLoc.setText(preferences.getKeyDropoffAdress());
                        tvSenderLoc.setText(preferences.getKeyPickupAdress());
                        reqDiscountPrice = ((reqMainTotalPrice *Double.parseDouble(discountValue))/100);
                        tvDiscount.setText(discountValue +" %");
                        reqSubTotalPrice = (double) (reqMainTotalPrice - reqDiscountPrice);
                        tvTotalPay.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqSubTotalPrice));
                    }
                }
            });
        }
        else
        {
            reqDeliveryPrice =( (cost_per_mile* reqTotalDistence)+Double.valueOf(packageModel.getPrice()));
            reqMainTotalPrice = reqDeliveryPrice;
            reqSubTotalPrice = reqDeliveryPrice;
            tvDeliveryCharges.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqDeliveryPrice));
            tvTotalPay.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqSubTotalPrice));
            tvRecLoc.setText(preferences.getKeyDropoffAdress());
            tvSenderLoc.setText(preferences.getKeyPickupAdress());
            reqDiscountPrice = ((reqMainTotalPrice *Double.parseDouble(discountValue))/100);
            tvDiscount.setText(discountValue +" %");
            reqSubTotalPrice = (double) (reqMainTotalPrice - reqDiscountPrice);
            tvTotalPay.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqSubTotalPrice));
        }




    }


    private void METHOD_showDeliveryTypes_Bts() {
        DeliveryTypesBottomSheet f = new DeliveryTypesBottomSheet(deliveryTypesList, "",
                new FragmentClickCallback() {
                    @Override
                    public void OnItemClick(int postion, Bundle bundle) {
                        selectedModel = (DeliveryTypesModel)
                                bundle.getSerializable("obj");

                        tvDeliveryType.setText(""+ selectedModel.getVehicleName());
                        Glide.with(view.getContext())
                                .load(selectedModel.getVehicleImage())
                                .placeholder(R.drawable.ic_delivery)
                                .error(R.drawable.ic_delivery)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .into(ivDelivery);

                        cost_per_mile = Integer.parseInt(selectedModel.getChargesPerKM());

                        UpDateCalculatedPrice(false);
                    }
                });
        f.show(getChildFragmentManager(), "DeliveryTypes_BottomSheet");
    }


    @Override
    public boolean onBackPressed() {

//        backpress handle on child CountryAndGoods_F fragment
        String tag_name=""+Functions.getActiveFragment(getChildFragmentManager());

        Log.d(Variables.tag,"I am clicked "+tag_name);


        if (tag_name .equals("Types_of_Goods_F"))
        {
            getChildFragmentManager().popBackStack();
            return true;
        }
        if (tag_name .equals("AddLocationAddressSelection_F"))
        {
            getChildFragmentManager().popBackStack();
            return true;
        }
        if (tag_name .equals("SearchLocation_F"))
        {
            getChildFragmentManager().popBackStack();
            return true;
        }
        if (tag_name.equals("Checkout_F"))
        {

            if (OrderCheckoutF.webView!=null && OrderCheckoutF.webView.canGoBack()) {
                OrderCheckoutF.webView.goBack();
                return true;
            } else {
                getChildFragmentManager().popBackStack();
                isBtnFurther =true;
            }
            return true;
        }
        if (txtNext.getText().toString().equals(view.getContext().getString(R.string.proceed)))
        {
            METHOD_adjustViewsforFurther();
            return true;
        }
        else
        {
            if (IsSure)
            {
                ShowPopUpAreYouSure();
                return true;
            }
        else
            {
                return false;
            }

        }
    }

    boolean IsSure=true;
    private void ShowPopUpAreYouSure() {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_defult_two_btn_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView txt_yes,txt_no,txt_title,txt_message;
        txt_title=dialog.findViewById(R.id.defult_alert_txt_title);
        txt_message=dialog.findViewById(R.id.defult_alert_txt_message);
        txt_no=dialog.findViewById(R.id.defult_alert_btn_cancel_no);
        txt_yes=dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

        txt_title.setText(""+view.getContext().getString(R.string.discard_alert));
        txt_message.setText(""+view.getContext().getString(R.string.are_you_sure_to_exit));
        txt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                IsSure=false;
                getFragmentManager().popBackStackImmediate();
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

    private void ShowPromoCodeDialuge() {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.promo_code_popup_verify_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText edt_code;
        edt_code=dialog.findViewById(R.id.promo_code_edt_code);

        LinearLayout btn_skip,btn_send;
        btn_send=dialog.findViewById(R.id.promo_code_btn_refer);
        btn_skip=dialog.findViewById(R.id.promo_code_btn_skip);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edt_code.getText().toString()))
                {
                    edt_code.setError(view.getContext().getString(R.string.cant_empty));
                    edt_code.setFocusable(true);
                    return;
                }

                JSONObject sendobj = new JSONObject();
                try {
                    sendobj.put("user_id", preferences.getKeyUserId());
                    sendobj.put("coupon_code", edt_code.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Functions.ShowProgressLoader(getContext(),false,false);
                ApiRequest.CallApi(getContext(), ApiUrl.verifyCoupon, sendobj, new Callback() {
                    @Override
                    public void Responce(String resp) {
                        Functions.CancelProgressLoader();

                        if (resp!=null){
                            try {
                                JSONObject respobj = new JSONObject(resp);
                                if (respobj.getString("code").equals("200")){

                                    etPromo.setText(""+edt_code.getText().toString());
                                    JSONObject couponobj = respobj.getJSONObject("msg").getJSONObject("Coupon");

                                    couponId = ""+couponobj.optString("id");
                                    couponCode = ""+couponobj.optString("coupon_code");
                                    discountValue = ""+couponobj.optString("discount","0");
                                    reqDiscountPrice = ((reqMainTotalPrice *Double.parseDouble(discountValue))/100);
                                    tvDiscount.setText(discountValue +" %");

                                    reqSubTotalPrice = (double) (reqMainTotalPrice - reqDiscountPrice);
                                    tvTotalPay.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f", reqSubTotalPrice));

                                    dialog.dismiss();
                                }
                                else
                                {
                                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.invalid_code));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private AlertDialog date_time_alertDialog;
    private View date_time_dialogView;
    private void METHOD_openDateTimePicker() {
        date_time_dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        date_time_alertDialog = new AlertDialog.Builder(getActivity()).create();

        btnDateTime = date_time_dialogView.findViewById(R.id.btn_datetime);
        txtDateTime = date_time_dialogView.findViewById(R.id.txt_datetime);
        txtDateTime.setText(""+view.getContext().getString(R.string.select_date));
        btnOk = date_time_dialogView.findViewById(R.id.btn_ok);

        datePicker = (DatePicker) date_time_dialogView.findViewById(R.id.date_picker);
        timePicker = (TimePicker) date_time_dialogView.findViewById(R.id.time_picker);
        textView = (TextView) date_time_dialogView.findViewById(R.id.textview);
        timePicker.setIs24HourView(false);


        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        datePicker.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        textView.setText("");
        state = 0;

        btnDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (state == 0){
                    METHOD_selectPickupDate();
                }else if(state == 1){
                    METHOD_selectPickupStartTime(date_time_alertDialog);
                }

            }});

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state==0)
                {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                }
                else if (state==1)
                {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                }

                btnDateTime.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
            }
        });
        calandarHelpRight.setFocusable(false);
        calandarHelpRight.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.newColorLightHint), android.graphics.PorterDuff.Mode.MULTIPLY);

        date_time_alertDialog.setView(date_time_dialogView);
        date_time_alertDialog.setCancelable(true);
        date_time_alertDialog.setCanceledOnTouchOutside(true);
        date_time_alertDialog.show();
    }

    private void METHOD_selectPickupStartTime(AlertDialog alertDialog) {
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

        txtDateTime.setText(R.string.select_pickup_time);

        calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());



        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdf_show = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        calandarCurrentTime = Calendar.getInstance();


        if (calandarCurrentTime.after(calandarStartTime) || calandarCurrentTime.compareTo(calandarStartTime)==0)
        {
            textView.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
            textView.setText(view.getContext().getString(R.string.you_select_past_time_send));

            btnOk.setVisibility(View.VISIBLE);
            btnDateTime.setVisibility(View.GONE);
            startTime = "";
            startTimeShow = "";
            tvPickupTime.setText("");
            txtDateTime.setText(R.string.select_pickup_time);
            state = 1;
        }
        else
        {
            startTime = ""+sdf.format(calandarStartTime.getTimeInMillis());
            startTimeShow = ""+sdf_show.format(calandarStartTime.getTimeInMillis());
            tvPickupTime.setText(startTimeShow);
            txtDateTime.setText(R.string.select_date);
            state =0;
            textView.setText("");
            alertDialog.dismiss();
        }


    }

    private void METHOD_selectPickupDate() {
        datePicker.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

        calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        tabTitlePickupDatetime.setVisibility(View.VISIBLE);

        pickupDate = ""+date_format.format(calandarStartTime.getTime());
        pickupDateShow = ""+sdf.format(calandarStartTime.getTime());
        tvPickupDate.setText(""+ pickupDateShow);

        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.VISIBLE);

            state = 1;
        txtDateTime.setText(R.string.select_pickup_time);

        startTime = "";
        startTimeShow = "";
        tvPickupTime.setText("");

    }


    private Boolean METHOD_checkValidations() {
        if (TextUtils.isEmpty(etSenderName.getText().toString())){

            METHOD_setFocusonEdittext(etSenderName);
            etSenderName.requestFocus();
            Functions.showKeyboard(getContext());
            etSenderName.setError(getString(R.string.cant_empty));
            return false;
        }else if (TextUtils.isEmpty(etSenderPhoneno.getText().toString())){

            METHOD_setFocusonEdittext(etSenderPhoneno);
            etSenderPhoneno.requestFocus();
            Functions.showKeyboard(getContext());
            etSenderPhoneno.setError(getString(R.string.cant_empty));
            return  false;
        }else if (!(ccpSender.isValid())){

            METHOD_setFocusonEdittext(etSenderPhoneno);
            etSenderPhoneno.requestFocus();
            Functions.showKeyboard(getContext());
            etSenderPhoneno.setError(getString(R.string.invalid_phone_no));
            return  false;
        }else if (TextUtils.isEmpty(etSenderEmail.getText().toString())){

            METHOD_setFocusonEdittext(etSenderEmail);
            etSenderEmail.requestFocus();
            Functions.showKeyboard(getContext());
            etSenderEmail.setError(getString(R.string.cant_empty));
            return  false;
        }else if (!(Functions.isValidEmail(etSenderEmail.getText().toString()))){

            METHOD_setFocusonEdittext(etSenderEmail);
            etSenderEmail.requestFocus();
            Functions.showKeyboard(getContext());
            etSenderEmail.setError(getString(R.string.invalid_email));
            return  false;
        }else if (TextUtils.isEmpty(etRecName.getText().toString())){

            METHOD_setFocusonEdittext(etRecName);
            etRecName.requestFocus();
            Functions.showKeyboard(getContext());
            etRecName.setError(getString(R.string.cant_empty));
            return  false;
        }else if (TextUtils.isEmpty(etRecPhoneno.getText().toString())){

            METHOD_setFocusonEdittext(etRecPhoneno);
            etRecPhoneno.requestFocus();
            Functions.showKeyboard(getContext());
            etRecPhoneno.setError(getString(R.string.cant_empty));
            return  false;
        }else if (!(ccpReceiver.isValid())){

            METHOD_setFocusonEdittext(etRecPhoneno);
            etRecPhoneno.requestFocus();
            Functions.showKeyboard(getContext());
            etRecPhoneno.setError(getString(R.string.invalid_phone_no));
            return  false;
        }else if (ccpReceiver.getNumber().equalsIgnoreCase(ccpSender.getNumber())){

            METHOD_setFocusonEdittext(etRecPhoneno);
            etRecPhoneno.requestFocus();
            Functions.showKeyboard(getContext());
            etRecPhoneno.setError(getString(R.string.sender_and_reciver_phone_number_must_diff));
            return  false;
        }else if (TextUtils.isEmpty(etRecEmail.getText().toString())){

            METHOD_setFocusonEdittext(etRecEmail);
            etRecEmail.requestFocus();
            Functions.showKeyboard(getContext());
            etRecEmail.setError(getString(R.string.cant_empty));
            return  false;
        }else if (!(Functions.isValidEmail(etRecEmail.getText().toString()))){
            METHOD_setFocusonEdittext(etRecEmail);
            etRecEmail.requestFocus();
            Functions.showKeyboard(getContext());
            etRecEmail.setError(getString(R.string.invalid_email));
            return  false;
        }else if (etRecEmail.getText().toString().equalsIgnoreCase(etSenderEmail.getText().toString())){
            METHOD_setFocusonEdittext(etRecEmail);
            etRecEmail.requestFocus();
            Functions.showKeyboard(getContext());
            etRecEmail.setError(getString(R.string.sender_and_reciver_email_different));
            return  false;
        }else if (TextUtils.isEmpty(goodsTypeId)){
            Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.order_create_status),
                    view.getContext().getString(R.string.must_select_goods_type));
            return  false;
        }else if (TextUtils.isEmpty(etItemName.getText().toString())){
            etItemName.setError(getString(R.string.cant_empty));
            return  false;
        } else {
            return true;
        }
    }

    private void METHOD_adjustViewsforProceed() {
        isBtnFurther = true;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(scrollView.FOCUS_UP, 0);
            }
        });


        etSenderName.setVisibility(View.GONE);
        tvSenderName.setText(""+ etSenderName.getText().toString());
        etSenderPhoneno.setVisibility(View.GONE);
        tvSenderPhoneno.setText(""+ etSenderPhoneno.getText().toString());

        tabSenderEmail.setVisibility(View.GONE);
        tabSenderLoc.setVisibility(View.VISIBLE);
        tabPickupDateTime.setVisibility(View.VISIBLE);


        etRecName.setVisibility(View.GONE);
        tvRecName.setText(""+ etRecName.getText().toString());
        etRecPhoneno.setVisibility(View.GONE);
        tvRecPh.setText(""+ etRecPhoneno.getText().toString());


        rlRecEmail.setVisibility(View.GONE);
        tabRecLoc.setVisibility(View.VISIBLE);

        tvItemsName.setText(etItemName.getText().toString());

        llFurtherItemDetails.setVisibility(View.GONE);
        llProceedDetails.setVisibility(View.VISIBLE);

        txtNext.setText(view.getContext().getString(R.string.proceed));
        UpDateCalculatedPrice(false);
    }

    private void METHOD_adjustViewsforFurther() {
        isBtnFurther =false;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(scrollView.FOCUS_UP, 0);
            }
        });


        etSenderName.setVisibility(View.VISIBLE);
        tvSenderName.setText(""+view.getContext().getString(R.string.full_name));
        etSenderPhoneno.setVisibility(View.VISIBLE);
        tvSenderPhoneno.setText(""+view.getContext().getString(R.string.phone_number));

        calandarHelpRight.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.newColorLightHint), android.graphics.PorterDuff.Mode.MULTIPLY);


        tabSenderEmail.setVisibility(View.VISIBLE);
        tabSenderLoc.setVisibility(View.GONE);
        tabPickupDateTime.setVisibility(View.GONE);

        METHOD_setFocusonEdittext(etSenderName);
        METHOD_setFocusonEdittext(etSenderPhoneno);
        METHOD_setFocusonEdittext(etSenderEmail);

        etRecName.setVisibility(View.VISIBLE);
        tvRecName.setText(""+view.getContext().getString(R.string.full_name));
        etRecPhoneno.setVisibility(View.VISIBLE);
        tvRecPh.setText(""+view.getContext().getString(R.string.phone_number));



        rlRecEmail.setVisibility(View.VISIBLE);
        tabRecLoc.setVisibility(View.GONE);

        llFurtherItemDetails.setVisibility(View.VISIBLE);
        llProceedDetails.setVisibility(View.GONE);
        etPromo.setText("");
        tvDiscount.setText("0 %");
        couponId ="";
        couponCode ="";
        discountValue ="0";


        txtNext.setText(view.getContext().getString(R.string.next));
    }

    private void METHOD_openTypes_of_Goods_F() {
        CountryAndGoodsF f = new CountryAndGoodsF(false, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (bundle!=null){
                            tvGoodsType.setText(bundle.getString("selected_goods_type"));
                            tvGoodsType.setTextColor(getResources().getColor(R.color.newColorBlack));
                            goodsTypeId = bundle.getString("selected_goods_type_id","");
                        }
                    }
                },200);
            }
        });
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, f,"Types_of_Goods_F").addToBackStack("Types_of_Goods_F").commit();
    }


    private void CallApi_placeOrder() {
        JSONObject sendobj = new JSONObject();

        try {

//            set number formate according to international standard
            String sender_phone_no= etSenderPhoneno.getText().toString();
            if (sender_phone_no.charAt(0)=='0')
            {
                sender_phone_no=sender_phone_no.substring(1);
            }
            sender_phone_no=sender_phone_no.replace(preferences.getKeyCountryCode(),"");
            sender_phone_no=sender_phone_no.replace(" ","");
            sender_phone_no=sender_phone_no.replace("+","");
            sender_phone_no= ccpSender.getSelectedCountryCodeWithPlus()+sender_phone_no;
            sender_phone_no=sender_phone_no.replace(" ","");
            sender_phone_no=sender_phone_no.replace("(","");
            sender_phone_no=sender_phone_no.replace(")","");
            sender_phone_no=sender_phone_no.replace("-","");

            String receiver_phone_no= etRecPhoneno.getText().toString();
            if (receiver_phone_no.charAt(0)=='0')
            {
                receiver_phone_no=receiver_phone_no.substring(1);
            }
            receiver_phone_no=receiver_phone_no.replace(preferences.getKeyCountryCode(),"");
            receiver_phone_no=receiver_phone_no.replace(" ","");
            receiver_phone_no=receiver_phone_no.replace("+","");
            receiver_phone_no= ccpReceiver.getSelectedCountryCodeWithPlus()+receiver_phone_no;
            receiver_phone_no=receiver_phone_no.replace(" ","");
            receiver_phone_no=receiver_phone_no.replace("(","");
            receiver_phone_no=receiver_phone_no.replace(")","");
            receiver_phone_no=receiver_phone_no.replace("-","");

            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("good_type_id", ""+ goodsTypeId);
            sendobj.put("delivery_type_id", ""+ selectedModel.getId());

            sendobj.put("item_title", ""+ tvGoodsType.getText().toString());
            sendobj.put("item_description", ""+ etItemDesc.getText().toString());
            sendobj.put("pickup_datetime", ""+ pickupDate +" "+ startTime);

            sendobj.put("sender_name", etSenderName.getText().toString());
            sendobj.put("sender_email", etSenderEmail.getText().toString());
            sendobj.put("sender_phone", sender_phone_no);
            sendobj.put("receiver_name", etRecName.getText().toString());
            sendobj.put("receiver_email", etRecEmail.getText().toString());
            sendobj.put("receiver_phone", receiver_phone_no);


            sendobj.put("sender_location_lat", preferences.getKeyPickupLat());
            sendobj.put("sender_location_long", preferences.getKeyPickupLng());
            sendobj.put("sender_location_string", preferences.getKeyPickupAdress());
            sendobj.put("sender_address_detail", preferences.getKeyPickupDetail());

            sendobj.put("receiver_location_lat", preferences.getKeyDropoffLat());
            sendobj.put("receiver_location_long", preferences.getKeyDropoffLng());
            sendobj.put("receiver_location_string", preferences.getKeyDropoffAdress());
            sendobj.put("receiver_address_detail", preferences.getKeyDropoffDetail());
            sendobj.put("package_size_id", packageModel.getId());

            if (TextUtils.isEmpty(""+ reqSinglePrice))
            {
                sendobj.put("price", "0");
            }
            else
            {
                sendobj.put("price", ""+String.format("%.2f", reqSinglePrice));
            }
            if (TextUtils.isEmpty(discountValue)){
                sendobj.put("discount", "0");
            }else {
                sendobj.put("discount", discountValue);
            }
            if (TextUtils.isEmpty(""+ reqDeliveryPrice))
            {
                sendobj.put("delivery_fee", "0");
            }
            else
            {
                sendobj.put("delivery_fee", ""+String.format("%.2f", reqDeliveryPrice));
            }

            if (TextUtils.isEmpty(couponId)){
                sendobj.put("coupon_id", "0");
            }else {
                sendobj.put("coupon_id", ""+ couponId);
            }

            if (TextUtils.isEmpty(couponCode)){
                sendobj.put("cod", "0");
            }else {
                sendobj.put("cod", ""+ couponCode);
            }

            if (TextUtils.isEmpty(""+ reqSubTotalPrice)){
                sendobj.put("total", "0");
            }else {
                sendobj.put("total", ""+String.format("%.2f", reqSubTotalPrice));
            }

            CallApi_addOrderSession(sendobj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CallApi_addOrderSession(JSONObject object) {
        Functions.ShowProgressLoader(view.getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.addOrderSession, object, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            isBtnFurther = false;
                            JSONObject orderobj = respobj.getJSONObject("msg")
                                    .getJSONObject("OrderSession");

                            String id = ""+orderobj.getString("id");
                            METHOD_openCheckout_F(id);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void METHOD_openCheckout_F(String id) {
        OrderCheckoutF orderCheckout_f = new OrderCheckoutF(id, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle.getBoolean("IsSuccess",false))
                {
                    MainF.whichScreenOpen ="";
                    OrderCheckoutF.webView=null;
                    Functions.CancelProgressLoader();
                    Intent intent=new Intent(getContext(), MainA.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        Log.d(Variables.tag,"PreLoad URL : "+ ApiUrl.paymentCheckoutUrl +id);
        FragmentTransaction ft =  getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("url", ApiUrl.paymentCheckoutUrl +id);
        orderCheckout_f.setArguments(bundle);
        ft.addToBackStack("Checkout_F");
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, orderCheckout_f,"Checkout_F").commit();
    }

    private void METHOD_setFocusonEdittext(EditText et) {
        et.setFocusableInTouchMode(true);
        et.setFocusable(true);
        et.setClickable(true);

        if (et.length() > 0)
            et.setSelection(et.length());
    }





    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}
