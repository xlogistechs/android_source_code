package com.qboxus.godelivery.ActivitiesFragments.SignUp;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.ModelClasses.RequestRegisterUserModel;
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


public class RegisterDOBF extends RootFragment implements View.OnClickListener {

    View view;
    DatePicker datePicker;
    RequestRegisterUserModel model;
    String currentDate ="";
    Date c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_register_d_o_b, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.register_no_continue).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this);

        datePicker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                view.findViewById(R.id.register_no_continue).setEnabled(true);
                view.findViewById(R.id.register_no_continue).setClickable(true);
            }
        });
    }

    private void InitControl() {

        c = Calendar.getInstance().getTime();


        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        datePicker=view.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        datePicker.getYearPicker().setEndYear(2020);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.register_no_continue:
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
                    model.setDateOfBirthday(currentDate);
                    HintRegisterUserAPI();
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

    private void HintRegisterUserAPI() {
        ShowRegisterAuth();
    }

    private void ShowRegisterAuth() {
        RegisterUserNameF fragment = new RegisterUserNameF();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("UserData",model);
        fragment.setArguments(args);
        transaction.addToBackStack("RegisterUserName_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.fl_id, fragment,"RegisterUserName_F").commit();
    }
}