package com.muhammadandmustafa.khadamatseller.SpinnerListeners;

import android.view.View;
import android.widget.AdapterView;

import com.muhammadandmustafa.khadamatseller.RegisterationActivities.CompleteSignUpActivity;

public class CitySpinnerListenerComplete implements AdapterView.OnItemSelectedListener {

    public static String city;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).equals("المدينة")) {
            city = parent.getItemAtPosition(position).toString();
            //do nothing
            CompleteSignUpActivity.CITY_CHOSEN_COMPLETE = 1;
            CompleteSignUpActivity.spinnerCairoAreaComplete.setVisibility(View.GONE);
            CompleteSignUpActivity.spinnerGizaAreaComplete.setVisibility(View.GONE);
        } else {
            CompleteSignUpActivity.CITY_CHOSEN_COMPLETE = 0;
            city = parent.getItemAtPosition(position).toString();
            if (city.equals("القاهرة")) {
                CompleteSignUpActivity.spinnerCairoAreaComplete.setVisibility(View.VISIBLE);
                CompleteSignUpActivity.spinnerGizaAreaComplete.setVisibility(View.GONE);
            } else if (city.equals("الجيزة")) {
                CompleteSignUpActivity.spinnerCairoAreaComplete.setVisibility(View.GONE);
                CompleteSignUpActivity.spinnerGizaAreaComplete.setVisibility(View.VISIBLE);
            } else {
                CompleteSignUpActivity.spinnerCairoAreaComplete.setVisibility(View.GONE);
                CompleteSignUpActivity.spinnerGizaAreaComplete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
