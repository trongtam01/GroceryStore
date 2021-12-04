package com.example.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.grocerystore.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {


    private ImageView imvProduct, thongke, editHome, settingsHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        imvProduct =  findViewById(R.id.imvProduct);
        thongke =  findViewById(R.id.thongke);
        editHome =  findViewById(R.id.editHome);
        settingsHome =  findViewById(R.id.settingsHome);

        imvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddProductActivity.class));
            }
        });
        thongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MainSellerActivity.class));
            }
        });
        editHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileEditSellerActivity.class));
            }
        });
        settingsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
            }
        });



    }
}