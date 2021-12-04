package com.example.grocerystore.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.R;
import com.example.grocerystore.models.ModelReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview> {

    private Context context;
    private ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,viewGroup,false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holderReview, int i) {
        ModelReview modelReview = reviewArrayList.get(i);

        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String timestamp = modelReview.getTimestamp();
        String review = modelReview.getReview();

        // user write review
        loadUserDetail(modelReview, holderReview);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holderReview.ratingBar.setRating(Float.parseFloat(ratings));
        holderReview.reviewTv.setText(review);
        holderReview.dateTv.setText(dateFormat);


    }

    private void loadUserDetail(ModelReview modelReview,final HolderReview holderReview) {
        String uid = modelReview.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = "" + dataSnapshot.child("name").getValue();
                        String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                        holderReview.nameTv.setText(name);

                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holderReview.profileIv);
                        }
                        catch (Exception e)
                        {
                            holderReview.profileIv.setImageResource(R.drawable.ic_store_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{

        private ImageView profileIv;
        private TextView nameTv, dateTv, reviewTv;
        private RatingBar ratingBar;

        public HolderReview(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);


        }
    }
}
