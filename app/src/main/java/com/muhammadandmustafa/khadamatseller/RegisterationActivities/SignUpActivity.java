package com.muhammadandmustafa.khadamatseller.RegisterationActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhammadandmustafa.khadamatseller.Activities.HomeActivity;
import com.muhammadandmustafa.khadamatseller.Models.Seller;
import com.muhammadandmustafa.khadamatseller.R;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.AreaSpinnerListener;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.CitySpinnerListener;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.SpecialtySpinnerListener;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    public static int CITY_CHOSEN = 0, AREA_CHOSEN = 0, SPECIALTY_CHOSEN = 0;
    private CircleImageView userImageSignUp;
    private ProgressBar progressBarImg;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPassword, editTextConfirmPassword, editTextStoreName;
    private TextView signInText;
    public static Spinner spinnerCity, spinnerCairoArea, spinnerGizaArea, spinnerSpecialty;
    private Button buttonSignUp;
    private Uri pickedImageUri;
    private String profileImageUrl;
    private UploadTask uploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef, deviceTokenRef;
    static final int PReqCode = 1;
    static int REQUESTCODE = 1;
    static int IMG_UPLOADED = 0;
    static int USER_INFO_SAVED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        progressBarImg = findViewById(R.id.progressBarImg);

        userImageSignUp = findViewById(R.id.userImageSignUp);
        userImageSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    requestPermissionAndOpenGallery();
                } else {
                    openGallery();
                }
            }
        });

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextStoreName = findViewById(R.id.editTextStoreName);

        signInText = findViewById(R.id.signInText);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerCairoArea = findViewById(R.id.spinnerCairoArea);
        spinnerGizaArea = findViewById(R.id.spinnerGizaArea);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);

        citySpinner();
        areaCairoSpinner();
        areaGizaSpinner();
        specialtySpinner();

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void citySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
        spinnerCity.setOnItemSelectedListener(new CitySpinnerListener());
    }

    private void areaCairoSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cairo_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCairoArea.setAdapter(adapter);
        spinnerCairoArea.setOnItemSelectedListener(new AreaSpinnerListener());
    }

    private void areaGizaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.giza_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGizaArea.setAdapter(adapter);
        spinnerGizaArea.setOnItemSelectedListener(new AreaSpinnerListener());
    }

    private void specialtySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);
        spinnerSpecialty.setOnItemSelectedListener(new SpecialtySpinnerListener());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PReqCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "لقد رفضت الطلب", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "اختر صورة"), REQUESTCODE);
    }

    private void requestPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    SignUpActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null && data.getData() != null) {
            //user choose the image
            //replace the image in the UI
            pickedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImageUri);
                userImageSignUp.setImageBitmap(bitmap);
                uploadProfileImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImageToFirebase() {
        String fileName = UUID.randomUUID().toString();
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + fileName + ".jpg");
        if (pickedImageUri != null) {
            progressBarImg.setVisibility(View.VISIBLE);

            uploadTask = profileImageRef.putFile(pickedImageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    IMG_UPLOADED = 0;
                    progressBarImg.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    IMG_UPLOADED = 1;
                    progressBarImg.setVisibility(View.GONE);
                    profileImageUrl = profileImageRef.getDownloadUrl().toString();
                    Toast.makeText(SignUpActivity.this, "تم رفع الصورة", Toast.LENGTH_SHORT).show();
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        profileImageUrl = downloadUri.toString();
                        String userId = mAuth.getCurrentUser().getUid();
                        sellerRef.child(userId).child("profilePhotoURL").setValue(profileImageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userImageSignUp.setVisibility(View.VISIBLE);
                                progressBarImg.setVisibility(View.INVISIBLE);
                                Toast.makeText(SignUpActivity.this, "تم رفع الصورة", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userImageSignUp.setVisibility(View.VISIBLE);
                                progressBarImg.setVisibility(View.INVISIBLE);
                                Toast.makeText(SignUpActivity.this, "فشل رفع الصورة", Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }

    }

    private void saveUserInfoToFirebaseDatabase() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String username = firstName + " " + lastName;
        String phoneNumber = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String storeName = editTextStoreName.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        Seller seller;
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

        if (profileImageUrl != null) {
            buttonSignUp.setVisibility(View.GONE);
            seller = new Seller(username, email, phoneNumber, storeName, CitySpinnerListener.city, AreaSpinnerListener.Area, SpecialtySpinnerListener.specialty, profileImageUrl);
            currentUserDb.setValue(seller).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    USER_INFO_SAVED = 1;
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    USER_INFO_SAVED = 0;
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            buttonSignUp.setVisibility(View.GONE);
            seller = new Seller(username, email, phoneNumber, storeName, CitySpinnerListener.city, AreaSpinnerListener.Area, SpecialtySpinnerListener.specialty, null);
            currentUserDb.setValue(seller).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    USER_INFO_SAVED = 1;
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    buttonSignUp.setVisibility(View.VISIBLE);
                    USER_INFO_SAVED = 0;
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void signUp() {

        String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        final String phoneNumber = editTextPhone.getText().toString();
        String storeName = editTextStoreName.getText().toString();

        if (firstName.isEmpty()) {
            editTextFirstName.setError("ادخل اسمك الأول");
            editTextFirstName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            editTextFirstName.setError("ادخل اسمك الأخير");
            editTextFirstName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("ادخل بريدك الالكتروني");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("ادخل بريد الكتروني صحيح");
            editTextEmail.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            editTextPhone.setError("ادخل رقم هاتفك");
            editTextPhone.requestFocus();
            return;
        }
        if (phoneNumber.length() != 11) {
            editTextPhone.setError("ادخل رقم هاتف صحيح");
            editTextPhone.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("ادخل كلمة السر");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("الحد الأدنى لكلمة السر هو 6 أحرف");
            editTextPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("أعد كتابة كلمة المرور");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("كلمة المرور غير متطابقة");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (storeName.isEmpty()) {
            editTextConfirmPassword.setError("اكتب اسم المحل");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (CITY_CHOSEN == 1) {
            Toast.makeText(this, "اختر المدينة", Toast.LENGTH_LONG).show();
            return;
        }
        if (AREA_CHOSEN == 1) {
            Toast.makeText(this, "اختر المنطقة", Toast.LENGTH_LONG).show();
            return;
        }
        if (SPECIALTY_CHOSEN == 1) {
            Toast.makeText(this, "اختر مجال البيع", Toast.LENGTH_LONG).show();
            return;
        }

        if (pickedImageUri != null) {
            if (IMG_UPLOADED == 1) {
                buttonSignUp.setVisibility(View.GONE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                buttonSignUp.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    saveUserInfoToFirebaseDatabase();
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    String currentUserID = mAuth.getCurrentUser().getUid();
                                    deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    deviceTokenRef.child("device_token").setValue(deviceToken);
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            } else {
                Toast.makeText(SignUpActivity.this, "من فضلك انتظر حتى يتم تحميل الصورة", Toast.LENGTH_LONG).show();
            }
        } else {
            buttonSignUp.setVisibility(View.GONE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            buttonSignUp.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                saveUserInfoToFirebaseDatabase();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                deviceTokenRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(currentUserID);
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                deviceTokenRef.child("device_token").setValue(deviceToken);
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }

}
