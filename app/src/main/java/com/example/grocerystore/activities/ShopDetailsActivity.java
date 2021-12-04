package com.example.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.grocerystore.Constants;
import com.example.grocerystore.R;
import com.example.grocerystore.adapters.AdapterCartItem;
import com.example.grocerystore.adapters.AdapterProductUser;
import com.example.grocerystore.adapters.AdapterReview;
import com.example.grocerystore.models.ModelCartItem;
import com.example.grocerystore.models.ModelProduct;
import com.example.grocerystore.models.ModelReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv, phoneTv
            , emailTv, openCloseTv
            , deliveryFeeTv, addressTv, filteredProductsTv,cartCountTv;
    private ImageButton callBtn, mapBtn, cartBtn, backBtn, filterProductBtn, reviewsBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private RatingBar ratingBar;

    private String shopUid;
    private String myLatitude, myLongitude, myPhone;
    private String shopName,shopEmail,shopPhone,shopAddress, shopLatitude, shopLongitude;

    public String deliveryFee;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productsList;

    private AdapterProductUser adapterProductUser;


    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        cartCountTv = findViewById(R.id.cartCountTv);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        ratingBar = findViewById(R.id.ratingBar);

        backBtn = findViewById(R.id.backBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        productsRv = findViewById(R.id.productsRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();

        easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","unique"}))
                .doneTableColumn();

        deleteCartData();
        cartCount();
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartDialog();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);

                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else {
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopDetailsActivity.this,ShopReviewsActivity.class);
                intent.putExtra("shopUid",shopUid);
                startActivity(intent);
            }
        });

    }

    private float ratingSum = 0;
    private void loadReviews() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }
                        long numberOfReviews = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void deleteCartData() {

        easyDB.deleteAllDataFromTable();
    }

    public void cartCount() {
        int count = easyDB.getAllData().getCount();

        if(count <= 0){
            cartCountTv.setVisibility(View.GONE);
        }
        else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText("" + count);
        }
    }

    public double allTotalPrice = 0.00;
    public TextView sTotalTv ,dFeeTv,allTotalPriceTv;

    private void showCartDialog() {

        cartItemList = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);

        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        shopNameTv.setText(shopName);

        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","unique"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    "" + pId,
                    "" +name,
                    "" + price,
                    "" + cost,
                    "" + quantity
            );

            cartItemList.add(modelCartItem);

        }

        adapterCartItem = new AdapterCartItem(this, cartItemList);

        cartItemsRv.setAdapter(adapterCartItem);

        dFeeTv.setText("$" + deliveryFee);
        sTotalTv.setText("$" + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("$" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("$",""))));

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.00;
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(myLatitude.equals("") || myLatitude.equals("null") || myLongitude.equals("") || myLongitude.equals("null")){
//                    Toast.makeText(ShopDetailsActivity.this, "Please enter your address in your profile before placing order...", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if(myPhone.equals("") || myPhone.equals("null")) {
                    Toast.makeText(ShopDetailsActivity.this, "Please enter your phone in your profile before placing order...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cartItemList.size() == 0){
                    Toast.makeText(ShopDetailsActivity.this, "No item in Cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();

            }
        });
    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Enter");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        String cost = allTotalPriceTv.getText().toString().trim().replace("$","");



        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","In Progress");
        hashMap.put("orderCost","" + cost);
        hashMap.put("orderBy","" + firebaseAuth.getUid());
        hashMap.put("orderTo","" + shopUid);
        hashMap.put("latitude","" + myLatitude);
        hashMap.put("longitude","" + myLongitude);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for(int i = 0; i < cartItemList.size(); i++){
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashMap1  = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, "Order Placed Success...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                        intent.putExtra("orderTo", shopUid);
                        intent.putExtra("orderId", timestamp);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void openMap() {
        String address = "https://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&dadd=" + shopLatitude + "," + shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(this, "" + shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            myPhone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String city = "" + ds.child("city").getValue();
                            myLatitude = "" + ds.child("latitude").getValue();
                            myLongitude = "" + ds.child("longitude").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = "" + dataSnapshot.child("name").getValue();
                shopName = "" + dataSnapshot.child("shopName").getValue();
                shopEmail = "" + dataSnapshot.child("email").getValue();
                shopPhone = "" + dataSnapshot.child("phone").getValue();
                shopLatitude = "" + dataSnapshot.child("latitude").getValue();
                shopAddress = "" + dataSnapshot.child("address").getValue();
                shopLongitude = "" + dataSnapshot.child("longitude").getValue();
                deliveryFee = "" + dataSnapshot.child("deliveryFee").getValue();
                String profileImage = "" + dataSnapshot.child("profileImage").getValue();
                String shopOpen = "" + dataSnapshot.child("shopOpen").getValue();

                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Phí giao hàng: $" + deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);

                if(shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }
                else {
                    openCloseTv.setText("Closed");
                }
                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShopProducts() {


        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

//    private void prepareNotificationMessage(String orderId){
//
//
//        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
//        String NOTIFICATION_TITLE = "New order" + orderId;
//        String NOTIFICATION_MESSAGE = "Bạn có đơn hàng mới đang chờ...";
//        String NOTIFICATION_TYPE = "NewOrder";
//
//        JSONObject notificationJo = new JSONObject();
//        JSONObject notificationBodyJo = new JSONObject();
//        try{
//            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
//            notificationBodyJo.put("buyerUid",firebaseAuth.getUid());
//            notificationBodyJo.put("sellerUid",shopUid);
//            notificationBodyJo.put("orderId",orderId);
//            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
//            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);
//
//            notificationJo.put("to",NOTIFICATION_TOPIC);
//            notificationJo.put("data",notificationBodyJo);
//        }
//        catch (Exception e){
//            Toast.makeText(this, ""  + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//
//
//    }




}