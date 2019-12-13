package com.muhammadandmustafa.khadamatseller.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muhammadandmustafa.khadamatseller.R;

public class ChangePhoneActivity extends AppCompatActivity {

    private TextView sitting_body_phone_cancel, sittings_body_phone_save;
    private EditText sitting_body_phone_edit_text;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        sitting_body_phone_cancel = findViewById(R.id.sitting_body_phone_cancel);
        sitting_body_phone_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sitting_body_phone_edit_text = findViewById(R.id.sitting_body_phone_edit_text);

        sittings_body_phone_save = findViewById(R.id.sittings_body_phone_save);
        sittings_body_phone_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = sitting_body_phone_edit_text.getText().toString();
                if (phone.isEmpty()) {
                    sitting_body_phone_edit_text.setError("ادخل رقم هاتفك");
                    sitting_body_phone_edit_text.requestFocus();
                    return;
                }
                String userId = mAuth.getCurrentUser().getUid();
                sellerRef.child(userId).child("phoneNumber").setValue(phone).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangePhoneActivity.this, "تم تغيير الرقم بنجاح", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
