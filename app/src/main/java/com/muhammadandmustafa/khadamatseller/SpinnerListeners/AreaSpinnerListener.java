package com.muhammadandmustafa.khadamatseller.SpinnerListeners;

import android.view.View;
import android.widget.AdapterView;

import com.muhammadandmustafa.khadamatseller.RegisterationActivities.SignUpActivity;

public class AreaSpinnerListener implements AdapterView.OnItemSelectedListener {

    public static String Area;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("المنطقة")){
            //do nothing
            SignUpActivity.AREA_CHOSEN = 1;
        } else {
            SignUpActivity.AREA_CHOSEN = 0;
            Area = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
