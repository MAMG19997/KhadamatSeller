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

public class ChangeStoreNameActivity extends AppCompatActivity {

    private TextView sitting_body_store_cancel, sittings_body_store_save;
    private EditText sitting_body_store_edit_text;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_store_name);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        sitting_body_store_cancel = findViewById(R.id.sitting_body_store_cancel);
        sitting_body_store_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sitting_body_store_edit_text = findViewById(R.id.sitting_body_store_edit_text);

        sittings_body_store_save = findViewById(R.id.sittings_body_store_save);
        sittings_body_store_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeName = sitting_body_store_edit_text.getText().toString();
                if (storeName.isEmpty()) {
                    sitting_body_store_edit_text.setError("ادخل اسم المحل");
                    sitting_body_store_edit_text.requestFocus();
                    return;
                }
                String userId = mAuth.getCurrentUser().getUid();
                sellerRef.child(userId).child("username").setValue(storeName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangeStoreNameActivity.this, "تم تغيير الاسم بنجاح", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangeStoreNameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
