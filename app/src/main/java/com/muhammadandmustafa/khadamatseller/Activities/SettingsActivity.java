package com.muhammadandmustafa.khadamatseller.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.muhammadandmustafa.khadamatseller.R;

public class SettingsActivity extends AppCompatActivity {

    private TextView sittings_change_account_user_name, sittings_change_account_user_phone, sittings_change_store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sittings_change_account_user_name = findViewById(R.id.sittings_change_account_user_name);
        sittings_change_account_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeNameActivity.class);
                startActivity(intent);
            }
        });

        sittings_change_account_user_phone = findViewById(R.id.sittings_change_account_user_phone);
        sittings_change_account_user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePhoneActivity.class);
                startActivity(intent);
            }
        });

        sittings_change_store_name = findViewById(R.id.sittings_change_store_name);
        sittings_change_store_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeStoreNameActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("الاعدادات");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //To support reverse transition when user clicks the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
