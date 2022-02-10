package com.qboxus.godelivery.BottomSheetDialogsFragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.PackagesSizeSelectionModel;
import com.qboxus.godelivery.R;
import com.qboxus.godelivery.RecyclerViewAdapters.PackagesSizeSelectionAdapter;

import java.util.ArrayList;


public class AddPackageSizeSelectionBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private View view;
    private RecyclerView recyclerView;
    private TextView tvNoData,tvSelectedPrice;
    LinearLayout btnNext;
    FragmentClickCallback callback;
    Preferences preferences;
    double totalCost;
    private PackagesSizeSelectionAdapter adapter;
    private ArrayList<PackagesSizeSelectionModel> list = new ArrayList<>();

    public AddPackageSizeSelectionBottomSheet() {
    }

    public AddPackageSizeSelectionBottomSheet(ArrayList<PackagesSizeSelectionModel> list, double totalCost, FragmentClickCallback callback) {
        this.callback = callback;
        this.totalCost=totalCost;
        this.list = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_add_package_size_selection__bottom_sheet, container, false);
       InitControl();
       ActionControl();
       return view;
    }

    private void ActionControl() {
        btnNext.setOnClickListener(this);
    }

    private void InitControl() {
        preferences=new Preferences(view.getContext());
        tvSelectedPrice =view.findViewById(R.id.tv_selected_price);
        recyclerView = view.findViewById(R.id.recyclerview);
        tvNoData =view.findViewById(R.id.txt_no_data);
        btnNext =view.findViewById(R.id.btn_next);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PackagesSizeSelectionAdapter(getContext(), list, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object model_obj, View view) {
               switch (view.getId())
               {
                   case R.id.main_tab:
                   {
                       for (int i=0;i<list.size();i++)
                       {
                           PackagesSizeSelectionModel model=list.get(i);
                           model.setSelected(false);
                           list.set(i,model);
                           adapter.notifyDataSetChanged();
                       }
                       double showPrice=0;
                       if (list.get(postion).getPrice().equalsIgnoreCase(""))
                       {
                           showPrice=((totalCost));
                       }
                       else
                       {
                           showPrice=((totalCost)+Double.valueOf(list.get(postion).getPrice()));
                       }
                       tvSelectedPrice.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f",showPrice));
                       view.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.ractengle_stroke_round_white));
                       PackagesSizeSelectionModel model=list.get(postion);
                       model.setSelected(true);
                       list.set(postion,model);
                   }
                   break;
               }
            }

            @Override
            public void OnItemLongClick(int postion, Object model_obj, View view) {

            }
        });

        recyclerView.setAdapter(adapter);

        SetUpScreenData();
    }

    private void SetUpScreenData() {
        if (list.size()>0)
        {
            for (int i=0;i<list.size();i++)
            {
                if (list.get(i).isSelected())
                {
                    double showPrice=0;
                    if (list.get(i).getPrice().equalsIgnoreCase(""))
                    {
                        showPrice=((totalCost));
                    }
                    else
                    {
                        showPrice=((totalCost)+Double.valueOf(list.get(i).getPrice()));
                    }
                    tvSelectedPrice.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.2f",showPrice));
                }
            }
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
            {
                PackagesSizeSelectionModel model = null;
                for (int i=0;i<list.size();i++)
                {
                    if (list.get(i).isSelected())
                    {
                        model=list.get(i);
                    }
                }
                if (model!=null)
                {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("DataModel", model);
                    callback.OnItemClick(0,bundle);
                   dismiss();
                }
            }
                break;

        }
    }
}