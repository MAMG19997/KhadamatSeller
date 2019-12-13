package com.muhammadandmustafa.khadamatseller.RegisterationActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muhammadandmustafa.khadamatseller.Activities.HomeActivity;
import com.muhammadandmustafa.khadamatseller.Models.Seller;
import com.muhammadandmustafa.khadamatseller.R;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.AreaSpinnerListener;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.CitySpinnerListenerComplete;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.SpecialtySpinnerListener;

public class CompleteSignUpActivity extends AppCompatActivity {

    public static int CITY_CHOSEN_COMPLETE = 0, AREA_CHOSEN_COMPLETE = 0, SPECIALTY_CHOSEN_COMPLETE = 0;
    private EditText editTextPhone_google, editTextStoreName_google;
    public static Spinner spinnerCityComplete, spinnerCairoAreaComplete, spinnerGizaAreaComplete, spinnerSpecialtyComplete;
    private Button finish_google_signUp;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef, deviceTokenRef;
    private String username, email, photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_sign_up);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        Intent intent = getIntent();
        username = intent.getExtras().getString("Username");
        photoURL = intent.getExtras().getString("PhotoURL");
        email = intent.getExtras().getString("Email");

        editTextPhone_google = findViewById(R.id.editTextPhone_google);
        editTextStoreName_google = findViewById(R.id.editTextStoreName_google);

        spinnerCityComplete = findViewById(R.id.spinnerCityComplete);
        spinnerCairoAreaComplete = findViewById(R.id.spinnerCairoAreaComplete);
        spinnerGizaAreaComplete = findViewById(R.id.spinnerGizaAreaComplete);
        spinnerSpecialtyComplete = findViewById(R.id.spinnerSpecialtyComplete);
        citySpinner();
        areaCairoSpinner();
        areaGizaSpinner();
        specialtySpinner();

        finish_google_signUp = findViewById(R.id.finish_google_signUp);
        finish_google_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfoToFirebaseDatabase();
            }
        });
    }

    private void citySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCityComplete.setAdapter(adapter);
        spinnerCityComplete.setOnItemSelectedListener(new CitySpinnerListenerComplete());
    }

    private void areaCairoSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cairo_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCairoAreaComplete.setAdapter(adapter);
        spinnerCairoAreaComplete.setOnItemSelectedListener(new AreaSpinnerListener());
    }

    private void areaGizaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.giza_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGizaAreaComplete.setAdapter(adapter);
        spinnerGizaAreaComplete.setOnItemSelectedListener(new AreaSpinnerListener());
    }

    private void specialtySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialtyComplete.setAdapter(adapter);
        spinnerSpecialtyComplete.setOnItemSelectedListener(new SpecialtySpinnerListener());
    }

    private void saveUserInfoToFirebaseDatabase() {
        String phoneNumber = editTextPhone_google.getText().toString().trim();
        String storeName = editTextStoreName_google.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        Seller seller;
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

        if (phoneNumber.isEmpty()) {
            editTextPhone_google.setError("ادخل رقم هاتفك");
            editTextPhone_google.requestFocus();
            return;
        }
        if (phoneNumber.length() != 11) {
            editTextPhone_google.setError("ادخل رقم هاتف صحيح");
            editTextPhone_google.requestFocus();
            return;
        }
        if (storeName.isEmpty()) {
            editTextStoreName_google.setError("اكتب اسم المحل");
            editTextStoreName_google.requestFocus();
            return;
        }

        if (photoURL != null) {
            finish_google_signUp.setVisibility(View.GONE);
            seller = new Seller(username, email, phoneNumber, storeName, CitySpinnerListenerComplete.city, AreaSpinnerListener.Area, SpecialtySpinnerListener.specialty, photoURL);
            currentUserDb.setValue(seller).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish_google_signUp.setVisibility(View.VISIBLE);
                    //Set the user Device token if he signed in using google
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String currentUserID = mAuth.getCurrentUser().getUid();
                    deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    deviceTokenRef.child("device_token").setValue(deviceToken);

                    Intent intent = new Intent(CompleteSignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish_google_signUp.setVisibility(View.VISIBLE);
                    Toast.makeText(CompleteSignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            finish_google_signUp.setVisibility(View.GONE);
            seller = new Seller(username, email, phoneNumber, storeName, CitySpinnerListenerComplete.city, AreaSpinnerListener.Area, SpecialtySpinnerListener.specialty, null);
            currentUserDb.setValue(seller).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish_google_signUp.setVisibility(View.VISIBLE);
                    //Set the user Device token if he signed in using google
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String currentUserID = mAuth.getCurrentUser().getUid();
                    deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    deviceTokenRef.child("device_token").setValue(deviceToken);

                    Intent intent = new Intent(CompleteSignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish_google_signUp.setVisibility(View.VISIBLE);
                    Toast.makeText(CompleteSignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
