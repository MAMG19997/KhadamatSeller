package com.muhammadandmustafa.khadamatseller.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.muhammadandmustafa.khadamatseller.Fragments.MoreFragment;
import com.muhammadandmustafa.khadamatseller.Fragments.MyProductsFragment;
import com.muhammadandmustafa.khadamatseller.Fragments.OrdersFragment;
import com.muhammadandmustafa.khadamatseller.Fragments.ProfileFragment;
import com.muhammadandmustafa.khadamatseller.R;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.nav_bar_orders:
                        selectedFragment = new OrdersFragment();
                        break;
                    case R.id.nav_bar_my_products:
                        selectedFragment = new MyProductsFragment();
                        break;
//                    case R.id.nav_bar_add_product:
//                        selectedFragment = new AddProductFragment();
//                        break;
                    case R.id.nav_bar_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                    case R.id.nav_bar_more:
                        selectedFragment = new MoreFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            }
        });

    }
}
