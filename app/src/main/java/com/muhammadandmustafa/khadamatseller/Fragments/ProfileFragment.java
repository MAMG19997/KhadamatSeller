package com.muhammadandmustafa.khadamatseller.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhammadandmustafa.khadamatseller.Activities.SettingsActivity;
import com.muhammadandmustafa.khadamatseller.Models.Seller;
import com.muhammadandmustafa.khadamatseller.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View rootView;
    private CircleImageView imageViewProfile;
    private TextView textViewName, textViewEmail, textViewPhoneNumber, textViewStoreName;
    private FirebaseAuth mAuth;
    private DatabaseReference sellerRef;
    private ProgressBar progressBarProfileImage;
    private ImageView imgSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        imgSettings = rootView.findViewById(R.id.imgSettings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        progressBarProfileImage = rootView.findViewById(R.id.progressBarProfileImage);

        imageViewProfile = rootView.findViewById(R.id.imageViewProfile);

        textViewName = rootView.findViewById(R.id.textViewName);
        textViewEmail = rootView.findViewById(R.id.textViewEmail);
        textViewPhoneNumber = rootView.findViewById(R.id.textViewPhoneNumber);
        textViewStoreName = rootView.findViewById(R.id.textViewStoreName);

        String userId = mAuth.getCurrentUser().getUid();

        sellerRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    textViewName.setText(seller.getUsername());
                    textViewEmail.setText(seller.getEmail());
                    textViewPhoneNumber.setText(seller.getPhoneNumber());
                    textViewStoreName.setText(seller.getStoreName());
                    progressBarProfileImage.setVisibility(View.VISIBLE);
                    if (seller.getProfilePhotoURL() != null) {
                        Glide.with(rootView.getContext())
                                .load(seller.getProfilePhotoURL())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBarProfileImage.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(imageViewProfile);
                        progressBarProfileImage.setVisibility(View.GONE);
                    } else {
                        progressBarProfileImage.setVisibility(View.GONE);
                        imageViewProfile.setImageResource(R.drawable.noimage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }
}
