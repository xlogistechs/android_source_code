package com.qboxus.godelivery.BottomSheetDialogsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.DeliveryTypesModel;
import com.qboxus.godelivery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.godelivery.RecyclerViewAdapters.DeliveryTypesAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeliveryTypesBottomSheet extends BottomSheetDialogFragment {

    private View view;
    private RecyclerView rv;
    private DeliveryTypesAdapter adp;
    private List<DeliveryTypesModel> list = new ArrayList<>();
    public static String id;
    FragmentClickCallback callback;


    public DeliveryTypesBottomSheet(List<DeliveryTypesModel> list, String id,
                                    FragmentClickCallback callback){
        this.list.clear();
        this.list = list;
        this.id = id;
        this.callback = callback;
    }




    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.delivery_type_bts, container, false);


        METHOD_findviewbyid();


        return view;
    }

    protected void METHOD_findviewbyid(){
        rv = view.findViewById(R.id.rv_id);
        rv.setHasFixedSize(false);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        METHOD_setAdapter();
    }

    private void METHOD_setAdapter() {
        adp = new DeliveryTypesAdapter(getContext(), list, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object model_obj, View view) {
                View id = view.findViewById(R.id.view_id);
                id.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", (Serializable) model_obj);
                callback.OnItemClick(postion,bundle);

                dismiss();
            }

            @Override
            public void OnItemLongClick(int postion, Object model_obj, View view) {

            }
        });

        rv.setAdapter(adp);
    }

}
