package com.muhammadandmustafa.khadamatseller.Activities;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhammadandmustafa.khadamatseller.Models.Product;
import com.muhammadandmustafa.khadamatseller.Models.Seller;
import com.muhammadandmustafa.khadamatseller.R;
import com.muhammadandmustafa.khadamatseller.RegisterationActivities.SignUpActivity;
import com.muhammadandmustafa.khadamatseller.SpinnerListeners.ProductCategorySpinnerListener;

import java.io.IOException;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    public static int CATEGORY_CHOSEN = 0;
    private ImageView productImage;
    private ProgressBar progressBarImgAddProduct;
    private EditText editTextProductName, editTextProductPrice, editTextProductDescription;
    public static Spinner spinnerProductCategory;
    private Button buttonAddProduct;
    private DatabaseReference sellerRef;
    private FirebaseAuth mAuth;
    private String userId, productImageUrl;
    static final int PReqCode = 1;
    static int REQUESTCODE = 1;
    private Uri pickedImageUri;
    static int IMG_UPLOADED = 0;
    private UploadTask uploadTask;
    private DatabaseReference productsDb;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        categorySpinner();

        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")){
                        spinnerProductCategory.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        progressBarImgAddProduct = findViewById(R.id.progressBarImgAddProduct);

        productImage = findViewById(R.id.productImage);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    requestPermissionAndOpenGallery();
                } else {
                    openGallery();
                }
            }
        });

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);

        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("أضف منتج");

    }

    private void categorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(adapter);
        spinnerProductCategory.setOnItemSelectedListener(new ProductCategorySpinnerListener());
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
        if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    AddProductActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(AddProductActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            } else {
                ActivityCompat.requestPermissions(AddProductActivity.this,
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
                productImage.setImageBitmap(bitmap);
                uploadProductImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProductImageToFirebase() {

        String fileName = UUID.randomUUID().toString();
        final StorageReference productImageRef =
                FirebaseStorage.getInstance().getReference("productspics/" + fileName + ".jpg");
        if (pickedImageUri != null) {
            progressBarImgAddProduct.setVisibility(View.VISIBLE);

            uploadTask = productImageRef.putFile(pickedImageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    IMG_UPLOADED = 0;
                    progressBarImgAddProduct.setVisibility(View.GONE);
                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    IMG_UPLOADED = 1;
                    progressBarImgAddProduct.setVisibility(View.GONE);
                    productImageUrl = productImageRef.getDownloadUrl().toString();
                    Toast.makeText(AddProductActivity.this, "تم رفع الصورة", Toast.LENGTH_SHORT).show();
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return productImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        productImageUrl = downloadUri.toString();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    private void addProduct(){

        final String productName = editTextProductName.getText().toString().trim();
        final String productPrice = editTextProductPrice.getText().toString().trim();
        final String productDescription = editTextProductDescription.getText().toString().trim();
        if (productName.isEmpty()) {
            editTextProductName.setError("ادخل اسم المنتج");
            editTextProductName.requestFocus();
            return;
        }
        if (productPrice.isEmpty()) {
            editTextProductPrice.setError("ادخل سعر المنتج بالارقام");
            editTextProductPrice.requestFocus();
            return;
        }
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")){
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId+productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")){
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId+productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")){
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId+productName);
                    }

                    if (productImageUrl != null){

                        if (IMG_UPLOADED == 1){
                            buttonAddProduct.setVisibility(View.GONE);
                            product = new Product(productName, productPrice, productDescription, productImageUrl, userId);
                            productsDb.setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddProductActivity.this, "تم اضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    buttonAddProduct.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            Toast.makeText(AddProductActivity.this, "من فضلك انتظر حتى يتم تحميل الصورة", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        buttonAddProduct.setVisibility(View.GONE);
                        product = new Product(productName, productPrice, productDescription, null, userId);
                        productsDb.push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddProductActivity.this, "تم اضافة المنتج بنجاح", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                buttonAddProduct.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
