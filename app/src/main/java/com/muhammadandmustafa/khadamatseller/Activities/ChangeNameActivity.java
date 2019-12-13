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

public class ChangeNameActivity extends AppCompatActivity {

    private TextView sitting_body_cancel, sittings_body_save;
    private EditText sitting_body_edit_text;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        sitting_body_cancel = findViewById(R.id.sitting_body_cancel);
        sitting_body_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sitting_body_edit_text = findViewById(R.id.sitting_body_edit_text);

        sittings_body_save = findViewById(R.id.sittings_body_save);
        sittings_body_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = sitting_body_edit_text.getText().toString();
                if (name.isEmpty()) {
                    sitting_body_edit_text.setError("ادخل اسمك");
                    sitting_body_edit_text.requestFocus();
                    return;
                }
                String userId = mAuth.getCurrentUser().getUid();
                sellerRef.child(userId).child("username").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangeNameActivity.this, "تم تغيير الاسم بنجاح", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangeNameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
