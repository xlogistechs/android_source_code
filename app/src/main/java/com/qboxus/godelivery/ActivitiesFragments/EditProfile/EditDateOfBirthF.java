package com.qboxus.godelivery.ActivitiesFragments.EditProfile;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;
import com.ycuwq.datepicker.date.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class EditDateOfBirthF extends RootFragment implements View.OnClickListener {

    View view;
    DatePicker datePicker;
    String currentDate ="";
    Bundle bundle;
    Date c;

    FragmentClickCallback callBack;
    public EditDateOfBirthF(FragmentClickCallback callBack) {
        this.callBack=callBack;
    }

    public EditDateOfBirthF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_edit_d_o_b, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.select_dob).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this);

        datePicker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                view.findViewById(R.id.select_dob).setEnabled(true);
                view.findViewById(R.id.select_dob).setClickable(true);
            }
        });


    }

    private void InitControl() {
        bundle=new Bundle();


        c = Calendar.getInstance().getTime();


        String str_date= Functions.ChangeDateFormat("dd MMM yyyy","yyyy-MM-dd",""+getArguments().getString("DOB"));
        String date_arr[]=str_date.split("-");

        datePicker=view.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        datePicker.getYearPicker().setEndYear(2020);
        datePicker.setDate(Integer.valueOf(date_arr[0]),Integer.valueOf(date_arr[1]),Integer.valueOf(date_arr[2]),true);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.select_dob:
                Functions.HideSoftKeyboard(getActivity());
            {
                currentDate = datePicker.getYear() + "-" + datePicker.getMonth()  + "-" + datePicker.getDay() ;

                SimpleDateFormat df = new SimpleDateFormat("yyy", Locale.getDefault());
                String formattedDate = df.format(c);
                Date dob = null;
                Date currentdate = null;
                try
                {
                    dob = df.parse(formattedDate);
                    currentdate = df.parse(currentDate);
                } catch (ParseException e){

                }

                int value = getDiffYears(currentdate,dob);

                if(value > 15)
                {
                    bundle.putString("dob", currentDate);
                    this.callBack.OnItemClick(0,bundle);
                    getActivity().onBackPressed();
                }
                else
                {
                    Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.age_restriction),""+view.getContext().getString(R.string.your_age_must_be_fifteen));
                }


            }
            break;
            default:
                break;
        }
    }

//    restrict user age according to year
    public int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }
    public Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }


}