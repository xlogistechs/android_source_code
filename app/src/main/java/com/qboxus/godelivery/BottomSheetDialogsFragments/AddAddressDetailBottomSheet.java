package com.qboxus.godelivery.BottomSheetDialogsFragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddAddressDetailBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText etAddressDetails;
    private LinearLayout btnAdd, btnCancel;
    private Boolean isPickupDetails;
    FragmentClickCallback callback;
    Preferences preferences;
    private View view;

    public AddAddressDetailBottomSheet(Boolean isPickupDetails, FragmentClickCallback callback) {
        this.isPickupDetails = isPickupDetails;
        this.callback = callback;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_address_details, container, false);


        METHOD_init_views();


        return view;
    }



    private void METHOD_init_views() {
        preferences=new Preferences(view.getContext());
        etAddressDetails = view.findViewById(R.id.et_address_details);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                callback.OnItemClick(0, new Bundle());
                dismiss();
                break;


            case R.id.btn_add:
                if (TextUtils.isEmpty(etAddressDetails.getText().toString()))
                {
                    etAddressDetails.setError(""+view.getContext().getString(R.string.cant_empty));
                    etAddressDetails.setFocusable(true);
                    return;
                }
                METHOD_add_Address_Details();
                break;

        }
    }



    private void METHOD_add_Address_Details() {
        String address_details = ""+ etAddressDetails.getText().toString();

        if (isPickupDetails)
            preferences.setKeyPickupDetail(address_details);
        else
            preferences.setKeyDropoffDetail(address_details);

        Bundle bundle = new Bundle();
        bundle.putString("details", etAddressDetails.getText().toString());

        callback.OnItemClick(0, bundle);
        dismiss();
    }
}
