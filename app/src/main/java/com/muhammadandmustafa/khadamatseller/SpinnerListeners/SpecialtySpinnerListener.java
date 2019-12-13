package com.muhammadandmustafa.khadamatseller.SpinnerListeners;

import android.view.View;
import android.widget.AdapterView;

import com.muhammadandmustafa.khadamatseller.RegisterationActivities.SignUpActivity;

public class SpecialtySpinnerListener implements AdapterView.OnItemSelectedListener {

    public static String specialty;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).equals("مجال البيع")) {
            //do nothing
            SignUpActivity.SPECIALTY_CHOSEN = 1;
        } else {
            SignUpActivity.SPECIALTY_CHOSEN = 0;
            specialty = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
