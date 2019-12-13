package com.muhammadandmustafa.khadamatseller.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import java.io.IOException;
import java.util.UUID;

public class EditOrDeleteProductActivity extends AppCompatActivity {

    private String productName, productPrice, productImageURL, productDescription;
    private TextView nameTextView, productDescriptionTextView, textViewProductPrice;
    private ProgressBar progressBarProductImage;
    private ImageView productImageView, imgCloseChangeNameDialog, imgCloseChangeDescriptionDialog, imgCloseChangePriceDialog;
    static int REQUESTCODE = 1;
    static final int PReqCode = 1;
    static int IMG_UPLOADED = 0;
    private Uri pickedImageUri;
    private UploadTask uploadTask;
    private FirebaseAuth mAuth;
    private String userId, productImageUrl;
    private DatabaseReference sellerRef;
    private DatabaseReference productsDb;
    private Dialog changeNameDialog, changeDescriptionDialog, changePriceDialog;
    private EditText newNameEditText, newDescriptionEditText, newPriceEditText;
    private Button changeNameButton, changeDescriptionButton, changePriceButton, buttonCancelNameDialog,
            buttonChangeNameDialog, buttonChangeDescriptionDialog, buttonCancelDescriptionDialog,
            buttonCancelPriceDialog, buttonChangePriceDialog, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_delete_product);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

        Intent intent = getIntent();
        Product product = intent.getParcelableExtra("product info");
        productName = product.getName();
        productPrice = product.getPrice();
        productImageURL = product.getImgURL();
        productDescription = product.getDescription();

        nameTextView = findViewById(R.id.nameTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);

        progressBarProductImage = findViewById(R.id.progressBarProductImage);

        productImageView = findViewById(R.id.productImageView);

        if (productImageURL != null) {

            progressBarProductImage.setVisibility(View.VISIBLE);
            Glide.with(EditOrDeleteProductActivity.this)
                    .load(productImageURL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarProductImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(productImageView);

        } else {

            // set the swapper Image to default if no image provided
            Resources resources = getApplicationContext().getResources();
            Drawable photoUrl = resources.getDrawable(R.drawable.noimage);
            productImageView.setImageDrawable(photoUrl);
            progressBarProductImage.setVisibility(View.GONE);

        }

        nameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        textViewProductPrice.setText(productPrice);

        changeNameDialog = new Dialog(EditOrDeleteProductActivity.this);
        changeNameDialog.setContentView(R.layout.change_product_name_dialog);
        changeNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        changeNameButton = findViewById(R.id.changeNameButton);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog.show();
            }
        });

        buttonCancelNameDialog = changeNameDialog.findViewById(R.id.buttonCancelNameDialog);
        buttonCancelNameDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog.dismiss();
            }
        });

        imgCloseChangeNameDialog = changeNameDialog.findViewById(R.id.imgCloseChangeNameDialog);
        imgCloseChangeNameDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog.dismiss();
            }
        });

        newNameEditText = changeNameDialog.findViewById(R.id.newNameEditText);

        buttonChangeNameDialog = changeNameDialog.findViewById(R.id.buttonChangeNameDialog);
        buttonChangeNameDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });

        changeDescriptionDialog = new Dialog(EditOrDeleteProductActivity.this);
        changeDescriptionDialog.setContentView(R.layout.change_product_description_dialog);
        changeDescriptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        changeDescriptionButton = findViewById(R.id.changeDescriptionButton);
        changeDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDescriptionDialog.show();
            }
        });

        buttonCancelDescriptionDialog = changeDescriptionDialog.findViewById(R.id.buttonCancelDescriptionDialog);
        buttonCancelDescriptionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDescriptionDialog.dismiss();
            }
        });

        imgCloseChangeDescriptionDialog = changeDescriptionDialog.findViewById(R.id.imgCloseChangeDescriptionDialog);
        imgCloseChangeDescriptionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDescriptionDialog.dismiss();
            }
        });

        newDescriptionEditText = changeDescriptionDialog.findViewById(R.id.newDescriptionEditText);

        buttonChangeDescriptionDialog = changeDescriptionDialog.findViewById(R.id.buttonChangeDescriptionDialog);
        buttonChangeDescriptionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDescription();
            }
        });

        changePriceDialog = new Dialog(EditOrDeleteProductActivity.this);
        changePriceDialog.setContentView(R.layout.change_product_price_dialog);
        changePriceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        changePriceButton = findViewById(R.id.changePriceButton);
        changePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePriceDialog.show();
            }
        });

        buttonCancelPriceDialog = changePriceDialog.findViewById(R.id.buttonCancelPriceDialog);
        buttonCancelPriceDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePriceDialog.dismiss();
            }
        });

        imgCloseChangePriceDialog = changePriceDialog.findViewById(R.id.imgCloseChangePriceDialog);
        imgCloseChangePriceDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePriceDialog.dismiss();
            }
        });

        newPriceEditText = changePriceDialog.findViewById(R.id.newPriceEditText);

        buttonChangePriceDialog = changePriceDialog.findViewById(R.id.buttonChangePriceDialog);
        buttonChangePriceDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrice();
            }
        });

        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {
                    requestPermissionAndOpenGallery();
                } else {
                    openGallery();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("تفاصيل المنتج");

    }

    private void deleteProduct() {

        buttonDelete.setVisibility(View.INVISIBLE);
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId + productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId + productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId + productName);
                    }
                    productsDb.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditOrDeleteProductActivity.this, "تم حذف المنتج بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void changePrice() {

        final String newPrice = newPriceEditText.getText().toString();
        if (newPrice.isEmpty()) {
            newPriceEditText.setError("ادخل السعر الجديد");
            newPriceEditText.requestFocus();
            return;
        }
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId + productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId + productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId + productName);
                    }
                    productsDb.child("price").setValue(newPrice).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditOrDeleteProductActivity.this, "تم تغيير السعر بنجاح", Toast.LENGTH_SHORT).show();
                            productDescriptionTextView.setText(newPrice);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void changeDescription() {

        final String newDescription = newDescriptionEditText.getText().toString();
        if (newDescription.isEmpty()) {
            newDescriptionEditText.setError("ادخل الوصف الجديد");
            newDescriptionEditText.requestFocus();
            return;
        }
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId + productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId + productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId + productName);
                    }
                    productsDb.child("description").setValue(newDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditOrDeleteProductActivity.this, "تم تغيير الوصف بنجاح", Toast.LENGTH_SHORT).show();
                            productDescriptionTextView.setText(newDescription);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void changeName() {

        final String newName = newNameEditText.getText().toString();
        if (newName.isEmpty()) {
            newNameEditText.setError("ادخل الاسم الجديد");
            newNameEditText.requestFocus();
            return;
        }
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId + productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId + productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId + productName);
                    }
                    productsDb.child("name").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditOrDeleteProductActivity.this, "تم تغيير الاسم بنجاح", Toast.LENGTH_SHORT).show();
                            nameTextView.setText(newName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        if (ContextCompat.checkSelfPermission(EditOrDeleteProductActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    EditOrDeleteProductActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(EditOrDeleteProductActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            } else {
                ActivityCompat.requestPermissions(EditOrDeleteProductActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null && data.getData() != null) {
            //user choose the image
            //replace the image in the UI
            pickedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImageUri);
                productImageView.setImageBitmap(bitmap);
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
            progressBarProductImage.setVisibility(View.VISIBLE);

            uploadTask = productImageRef.putFile(pickedImageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    IMG_UPLOADED = 0;
                    progressBarProductImage.setVisibility(View.GONE);
                    Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    IMG_UPLOADED = 1;
                    progressBarProductImage.setVisibility(View.GONE);
                    productImageUrl = productImageRef.getDownloadUrl().toString();
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
                        changeImageURL();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    private void changeImageURL() {
        DatabaseReference productImageDb = FirebaseDatabase.getInstance().getReference().child("products").child(productName).child("productImageUrl");
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical").child(userId + productName);
                    } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint").child(userId + productName);
                    } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                        productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing").child(userId + productName);
                    }
                    productsDb.child("imgURL").setValue(productImageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBarProductImage.setVisibility(View.GONE);
                            Toast.makeText(EditOrDeleteProductActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarProductImage.setVisibility(View.GONE);
                            Toast.makeText(EditOrDeleteProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
