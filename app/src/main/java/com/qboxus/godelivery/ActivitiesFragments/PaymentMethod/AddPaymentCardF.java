package com.qboxus.godelivery.ActivitiesFragments.PaymentMethod;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class AddPaymentCardF extends RootFragment implements View.OnClickListener {

    View view;
    EditText cardNameEdit, cardNumberEdit, expireDateEdit, cvvEdit;
    private String month;
    private String year;
    ImageView imgCardType;

    int count = 0;
    FragmentClickCallback fragmentClickCallbackBundle;
    Calendar calandarStartTime, calandarCurrentTime;
    private SparseArray<Pattern> mCCPatterns = null;
    private final int mDefaultDrawableResId = R.drawable.ic_other_card;
    private int mCurrentDrawableResId = 0;
    Preferences preferences;
    Bundle bundle2 = new Bundle();
    String a;
    int keyDel;
    public AddPaymentCardF(FragmentClickCallback fragmentClickCallbackBundle) {
        this.fragmentClickCallbackBundle = fragmentClickCallbackBundle;
    }

    public AddPaymentCardF(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_card, container, false);

        preferences=new Preferences(view.getContext());

        if (mCCPatterns == null) {
            mCCPatterns = new SparseArray<>();
            // Without spaces for credit card masking
            mCCPatterns.put(R.drawable.ic_visa_card, Pattern.compile("^4[0-9]{2,12}(?:[0-9]{3})?$"));
            mCCPatterns.put(R.drawable.ic_master_card, Pattern.compile("^5[1-5][0-9]{1,14}$"));
            mCCPatterns.put(R.drawable.ic_american_express, Pattern.compile("^3[47][0-9]{1,13}$"));
        }

        cardNameEdit =view.findViewById(R.id.card_name_edit);
        cardNumberEdit =view.findViewById(R.id.card_number_edit);
        expireDateEdit =view.findViewById(R.id.expire_date_edit);
        cvvEdit =view.findViewById(R.id.cvv_edit);
        imgCardType =view.findViewById(R.id.selected_card);

        cardNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

                int mDrawableResId = 0;
                for (int i = 0; i < mCCPatterns.size(); i++) {
                    int key = mCCPatterns.keyAt(i);
                    // get the object by the key.
                    Pattern p = mCCPatterns.get(key);

                    Matcher m = p.matcher(((""+text).replace(" ","")));
                    if (m.find()) {
                        mDrawableResId = key;
                        break;
                    }
                }
                if (mDrawableResId > 0 && mDrawableResId != mCurrentDrawableResId) {
                    mCurrentDrawableResId = mDrawableResId;
                } else if (mDrawableResId == 0) {
                    mCurrentDrawableResId = mDefaultDrawableResId;
                }

                if (mCurrentDrawableResId==0)
                {
                    imgCardType.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_other_card));
                }
                else
                {
                    imgCardType.setImageDrawable(ContextCompat.getDrawable(view.getContext(),mCurrentDrawableResId));
                }

                boolean flag = true;
                String eachBlock[] = cardNumberEdit.getText().toString().split(" ");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    cardNumberEdit.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((cardNumberEdit.getText().length() + 1) % 5) == 0) {

                            if (cardNumberEdit.getText().toString().split(" ").length <= 3) {
                                cardNumberEdit.setText(cardNumberEdit.getText() + " ");
                                cardNumberEdit.setSelection(cardNumberEdit.getText().length());
                            }
                        }
                        a = cardNumberEdit.getText().toString();
                    } else {
                        a = cardNumberEdit.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    cardNumberEdit.setText(a);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        expireDateEdit.setOnClickListener(this);

        view.findViewById(R.id.btn_save_card).setOnClickListener(this);
        view.findViewById(R.id.back_icon).setOnClickListener(this);
        return view;
    }


    private void METHOD_openDateTimePicker() {
        final View dialogView = View.inflate(getActivity(), R.layout.add_card_date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();


        LinearLayout btn_date_time = dialogView.findViewById(R.id.btn_datetime);
        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());


        btn_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calandarStartTime = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());

                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                expireDateEdit.setText(""+sdf.format(calandarStartTime.getTime()));
                alertDialog.dismiss();
            }});

        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_icon:
                getActivity().onBackPressed();
                break;

            case R.id.expire_date_edit:
                METHOD_openDateTimePicker();
                break;


            case R.id.btn_save_card:
                Validate_card();
                break;

        }
    }


    public void Validate_card() {


        if(cardNameEdit.getText().toString().isEmpty()){
            cardNameEdit.setError(getString(R.string.cant_empty));
            return;
        }
        if(cardNumberEdit.getText().toString().isEmpty()){
            cardNumberEdit.setError(getString(R.string.cant_empty));
            return;
        }
        if(cardNumberEdit.getText().toString().length()!=19){
            cardNumberEdit.setError(getString(R.string.invalid_card_number));
            return;
        }
        if(cvvEdit.getText().toString().isEmpty()){
            cvvEdit.setError(getString(R.string.cant_empty));
            return;
        }
        if(expireDateEdit.getText().toString().isEmpty()){
            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.select_the_card_validity));
            return;
        }
        calandarCurrentTime = Calendar.getInstance();
        if (calandarCurrentTime.after(calandarStartTime) || calandarCurrentTime.compareTo(calandarStartTime)==0)
        {
            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.select_the_card_validity));
            return;
        }
        String expire_date_field = expireDateEdit.getText().toString();
        if(!expire_date_field.isEmpty())
        {
            month = expire_date_field.substring(0, 2);
        }
        if(!expire_date_field.isEmpty())
        {
            year = expire_date_field.substring(5, 7);
        }

        call_api_for_add_card();
    }

    private void call_api_for_add_card() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("name", cardNameEdit.getText().toString());
            sendobj.put("card", cardNumberEdit.getText().toString());
            sendobj.put("cvc", cvvEdit.getText().toString());
            sendobj.put("exp_month", month);
            sendobj.put("exp_year", year);
            sendobj.put("default","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.addPaymentMethod, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")){
                        bundle2.putBoolean("IsUpdate", true);
                        getActivity().onBackPressed();
                    }
                    else
                    {
                        Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.payment_status),""+respobj.getString("msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    public void onDestroy() {
        pass_data_back();
        super.onDestroy();
    }

    public void pass_data_back(){
        if(fragmentClickCallbackBundle !=null) {
            fragmentClickCallbackBundle.OnItemClick(0,bundle2);
        }
    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}
