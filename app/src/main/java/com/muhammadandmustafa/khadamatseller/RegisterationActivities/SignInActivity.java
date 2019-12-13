package com.muhammadandmustafa.khadamatseller.RegisterationActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muhammadandmustafa.khadamatseller.Common;
import com.muhammadandmustafa.khadamatseller.Activities.HomeActivity;
import com.muhammadandmustafa.khadamatseller.R;

public class SignInActivity extends AppCompatActivity {

    private TextView signUpText, forget_password;
    private LinearLayout mainView, splashScreen, noConnectionViewSignIn;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef, deviceTokenRef;
    private Button buttonSignIn, retryBtnSignIn, signIn_google_btn;
    private EditText editTextEmail, editTextPassword;
    private ProgressDialog p;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private boolean haseAUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mainView = findViewById(R.id.mainView);
        splashScreen = findViewById(R.id.splashScreen);
        noConnectionViewSignIn = findViewById(R.id.noConnectionViewSignIn);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        retryBtnSignIn = findViewById(R.id.retryBtnSignIn);
        retryBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isNetworkAvailable(SignInActivity.this) || Common.isWifiAvailable(SignInActivity.this)) {
                    splashScreen.setVisibility(View.VISIBLE);
                    mainView.setVisibility(View.GONE);
                    noConnectionViewSignIn.setVisibility(View.GONE);
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        splashScreen.setVisibility(View.GONE);
                        mainView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainView.setVisibility(View.GONE);
                    noConnectionViewSignIn.setVisibility(View.VISIBLE);
                    Resources resources = getApplicationContext().getResources();
                    String messageText = resources.getString(R.string.no_internet_connection);
                    Toast.makeText(SignInActivity.this, messageText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        signUpText = findViewById(R.id.signUpText);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        forget_password = findViewById(R.id.forget_password);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetPassword = new Intent(SignInActivity.this, ResetPasswordActivity.class);
                startActivity(forgetPassword);
            }
        });

        signIn_google_btn = findViewById(R.id.signIn_google_btn);
        signIn_google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = new ProgressDialog(SignInActivity.this);
                p.setMessage("انتظر من فضلك...");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
                signInGoogle();
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
//        else {
//            faceBookCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();

                            final String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

                            sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userId)) {

                                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        String currentUserID = mAuth.getCurrentUser().getUid();
                                        deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        deviceTokenRef.child("device_token").setValue(deviceToken);
                                        startActivity(intent);

                                        haseAUser = true;
                                    } else {
                                        Intent intent = new Intent(SignInActivity.this, CompleteSignUpActivity.class);
                                        intent.putExtra("Username", account.getDisplayName());
                                        if (account.getPhotoUrl().toString() != null) {
                                            intent.putExtra("PhotoURL", account.getPhotoUrl().toString());
                                        }
                                        intent.putExtra("Email", user.getEmail());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });

                        } else {
                            // If sign in fails, display a message to the user.
//                            Snackbar.make(findViewById(R.id.main_layout), "فشل تسجيل الدخول", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(SignInActivity.this, "فشل تسجيل الدخول", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }


    private void signIn() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Resources resources = getApplicationContext().getResources();
            String messageText = resources.getString(R.string.Please_enter_your_email);
            editTextEmail.setError(messageText);
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Resources resources = getApplicationContext().getResources();
            String messageText = resources.getString(R.string.Please_enter_a_valid_email);
            editTextEmail.setError(messageText);
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            Resources resources = getApplicationContext().getResources();
            String messageText = resources.getString(R.string.Please_enter_your_password);
            editTextPassword.setError(messageText);
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Resources resources = getApplicationContext().getResources();
            String messageText = resources.getString(R.string.Minimum_length_of_password_is_6);
            editTextPassword.setError(messageText);
            editTextPassword.requestFocus();
            return;
        }

        buttonSignIn.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                buttonSignIn.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//                    if (user != null) {
                    Task<Void> userTask = user.reload();
                    userTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            String currentUserID = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            sellerRef.child(currentUserID).child("device_token")
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });
                        }


                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //if user is already logged in then ShiftSwapFragment will open instead of SignInActivity
        if (Common.isNetworkAvailable(SignInActivity.this) || Common.isWifiAvailable(SignInActivity.this)) {
            splashScreen.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
            noConnectionViewSignIn.setVisibility(View.GONE);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {

                final String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

                sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userId)) {

                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            String currentUserID = mAuth.getCurrentUser().getUid();
                            deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            deviceTokenRef.child("device_token").setValue(deviceToken);
                            startActivity(intent);

                            haseAUser = true;
                        } else {
                            Intent intent = new Intent(SignInActivity.this, CompleteSignUpActivity.class);
                            if (account.getDisplayName() != null) {
                                intent.putExtra("Username", account.getDisplayName());
                            }
                            if (account.getPhotoUrl().toString() != null) {
                                intent.putExtra("PhotoURL", account.getPhotoUrl().toString());
                            }
                            if (user.getEmail() != null) {
                                intent.putExtra("Email", user.getEmail());
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


//                Task<Void> userTask = user.reload();
//                userTask.addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                    if (user != null) {
//                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();
////                    }
//                    }
//                });
            } else {
                splashScreen.setVisibility(View.GONE);
                mainView.setVisibility(View.VISIBLE);
            }
        } else {
            mainView.setVisibility(View.GONE);
            noConnectionViewSignIn.setVisibility(View.VISIBLE);
        }

    }
}
