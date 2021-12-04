package com.example.grocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.grocerystore.R;
import com.example.grocerystore.adapters.AdapterReview;
import com.example.grocerystore.models.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopReviewsActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv;
    private TextView shopNameTv, ratingsTv;
    private RatingBar ratingBar;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    private String shopUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingsTv = findViewById(R.id.ratingsTv);
        reviewsRv = findViewById(R.id.reviewsRv);



        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails();
        loadReviews();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews() {
        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reviewArrayList.clear();

                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }
                        adapterReview = new AdapterReview(ShopReviewsActivity.this,reviewArrayList);
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;

                        ratingsTv.setText(String.format("%.2f",avgRating) + "[" + numberOfReviews + "]");
                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = "" + dataSnapshot.child("shopName").getValue();
                        String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                        shopNameTv.setText(shopName);

                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                        }
                        catch (Exception e)
                        {
                            profileIv.setImageResource(R.drawable.ic_store_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}