package com.muhammadandmustafa.khadamatseller.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhammadandmustafa.khadamatseller.Activities.AddProductActivity;
import com.muhammadandmustafa.khadamatseller.Adapters.ProductsAdapter;
import com.muhammadandmustafa.khadamatseller.Common;
import com.muhammadandmustafa.khadamatseller.Activities.EditOrDeleteProductActivity;
import com.muhammadandmustafa.khadamatseller.Models.Product;
import com.muhammadandmustafa.khadamatseller.Models.Seller;
import com.muhammadandmustafa.khadamatseller.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyProductsFragment extends Fragment {

    private View rootView;
    private ListView myProductsList;
    private FloatingActionButton fabAddProduct;
    private TextView emptyTVMyProducts;
    private LinearLayout noConnectionViewMyProducts;
    private DatabaseReference sellerRef;
    private FirebaseAuth mAuth;
    private boolean firstVisit;
    private String userId;
    private DatabaseReference productsDb;
    private ProductsAdapter productsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_products, container, false);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(userId);

        firstVisit = true;

        myProductsList = rootView.findViewById(R.id.myProductsList);

        fabAddProduct = rootView.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), AddProductActivity.class);
                startActivity(intent);
            }
        });

        emptyTVMyProducts = rootView.findViewById(R.id.emptyTVMyProducts);

        noConnectionViewMyProducts = rootView.findViewById(R.id.noConnectionViewMyProducts);

        fetchData();

        return rootView;
    }

    private void fetchData() {

        // If there is a network connection, fetch data
        if (Common.isNetworkAvailable(getContext()) || Common.isWifiAvailable(getContext())) {

            myProductsList.setVisibility(View.VISIBLE);
            noConnectionViewMyProducts.setVisibility(View.INVISIBLE);

            sellerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Seller seller = dataSnapshot.getValue(Seller.class);
                        if (seller.getSpecialty().equals("أدوات كهربائية ومعدات يدوية")) {
                            productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("electrical");
                        } else if (seller.getSpecialty().equals("الطلاء ولوازم الدهان")) {
                            productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("paint");
                        } else if (seller.getSpecialty().equals("أدوات صحية ولوازم السباكة")) {
                            productsDb = FirebaseDatabase.getInstance().getReference().child("products").child("plumbing");
                        }
                        productsDb.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.exists()) {
                                    Product product = dataSnapshot.getValue(Product.class);
                                    if (product.getSellerID().equals(userId)) {
                                        productsAdapter.add(product);
                                        productsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        final List<Product> productBodyList = new ArrayList<>();
                        Collections.reverse(productBodyList);
                        productsAdapter = new ProductsAdapter(getContext(), R.layout.product_list_item, productBodyList);
                        myProductsList.setVisibility(View.VISIBLE);
                        myProductsList.refreshDrawableState();
                        myProductsList.setAdapter(productsAdapter);
                        productsAdapter.notifyDataSetChanged();

                        myProductsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Product product = productBodyList.get(parent.getCount() - position - 1);
                                Intent intent = new Intent(getContext(), EditOrDeleteProductActivity.class);
                                intent.putExtra("product info", product);
                                startActivity(intent);

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            myProductsList.setVisibility(View.INVISIBLE);
            noConnectionViewMyProducts.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        //other stuff
        if (firstVisit) {
            //do stuff for first visit only

            firstVisit = false;
        } else {
            fetchData();
        }
    }
}
