package com.example.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerystore.R;
import com.example.grocerystore.adapters.AdapterOrderUser;
import com.example.grocerystore.adapters.AdapterProductUser;
import com.example.grocerystore.adapters.AdapterShop;
import com.example.grocerystore.models.ModelOrderUser;
import com.example.grocerystore.models.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,phoneTv,tabShopsTv,tabOrdersTv;
    private RelativeLayout shopsRl, ordersRl;
    private ImageButton logoutBtn, editProfileBtn, settingsBtn;
    private ImageView profileIv;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RecyclerView shopsRv, orderRv;

    private ArrayList<ModelShop> shopsList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> orderList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv= findViewById(R.id.nameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileIv = findViewById(R.id.profileIv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        shopsRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.ordersRl);
        shopsRv = findViewById(R.id.shopsRv);
        orderRv = findViewById(R.id.orderRv);
        settingsBtn = findViewById(R.id.settingsBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();

        showShopsUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMeOffline();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
            }
        });
        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShopsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this,SettingsActivity.class));
            }
        });
    }

    private void showShopsUI() {
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorBack));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);
    }


    private void makeMeOffline() {
        progressDialog.setMessage("Đăng nhập người dùng");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }
        else {
            LoadMyInfo();
        }
    }

    private void LoadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String city = "" + ds.child("city").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            loadShops(city);
                            loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrders() {
        orderList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = "" + ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            orderList.add(modelOrderUser);
                                        }
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this,orderList);
                                        orderRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShops(final String myCity) {
        shopsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        shopsList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopCity = "" + ds.child("city").getValue();

//                            if(shopCity.equals(myCity)){
//                                shopsList.add(modelShop);
//                            }

                            shopsList.add(modelShop);
                        }
                        adapterShop = new AdapterShop(MainUserActivity.this, shopsList);
                        shopsRv.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}