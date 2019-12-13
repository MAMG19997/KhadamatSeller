package com.muhammadandmustafa.khadamatseller.SpinnerListeners;

import android.view.View;
import android.widget.AdapterView;

import com.muhammadandmustafa.khadamatseller.RegisterationActivities.SignUpActivity;

public class CitySpinnerListener implements AdapterView.OnItemSelectedListener {

    public static String city;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).equals("المدينة")) {
            city = parent.getItemAtPosition(position).toString();
            //do nothing
            SignUpActivity.CITY_CHOSEN = 1;
            SignUpActivity.spinnerCairoArea.setVisibility(View.GONE);
            SignUpActivity.spinnerGizaArea.setVisibility(View.GONE);
        } else {
            SignUpActivity.CITY_CHOSEN = 0;
            city = parent.getItemAtPosition(position).toString();
            if (city.equals("القاهرة")) {
                SignUpActivity.spinnerCairoArea.setVisibility(View.VISIBLE);
                SignUpActivity.spinnerGizaArea.setVisibility(View.GONE);
            } else if (city.equals("الجيزة")) {
                SignUpActivity.spinnerCairoArea.setVisibility(View.GONE);
                SignUpActivity.spinnerGizaArea.setVisibility(View.VISIBLE);
            } else {
                SignUpActivity.spinnerCairoArea.setVisibility(View.GONE);
                SignUpActivity.spinnerGizaArea.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
