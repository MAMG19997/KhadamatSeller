package com.muhammadandmustafa.khadamatseller.RegisterationActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.muhammadandmustafa.khadamatseller.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private TextView sendPassword;
    private TextView cancelBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtEmail = findViewById(R.id.forget_password_edit_text);
        sendPassword = findViewById(R.id.forget_password_send);
        cancelBtn = findViewById(R.id.forget_password_cancel);

        mAuth = FirebaseAuth.getInstance();

        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Resources resources = getApplicationContext().getResources();
                    String messageText = resources.getString(R.string.Please_enter_your_email);
                    Toast.makeText(getApplicationContext(), messageText, Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Resources resources = getApplicationContext().getResources();
                                    String messageText = resources.getString(R.string.check_email_to_reset_password);
                                    Toast.makeText(ResetPasswordActivity.this, messageText, Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Resources resources = getApplicationContext().getResources();
                                    String messageText = resources.getString(R.string.fail_to_send_email_to_reset_password);
                                    Toast.makeText(ResetPasswordActivity.this, messageText, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
