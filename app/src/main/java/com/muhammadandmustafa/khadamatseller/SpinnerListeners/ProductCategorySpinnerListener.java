package com.muhammadandmustafa.khadamatseller.SpinnerListeners;

import android.view.View;
import android.widget.AdapterView;

import com.muhammadandmustafa.khadamatseller.Activities.AddProductActivity;

public class ProductCategorySpinnerListener implements AdapterView.OnItemSelectedListener {

    public static String Category;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("القسم")){
            //do nothing
            AddProductActivity.CATEGORY_CHOSEN = 1;
        } else {
            AddProductActivity.CATEGORY_CHOSEN = 0;
            Category = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
