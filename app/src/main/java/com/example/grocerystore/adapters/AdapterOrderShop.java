package com.example.grocerystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.FilterOrderShop;
import com.example.grocerystore.R;
import com.example.grocerystore.activities.OrderDetailsSellerActivity;
import com.example.grocerystore.models.ModelOrderShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList, filterList;
    private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller,viewGroup,false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holderOrderShop, int i) {
        ModelOrderShop modelOrderShop = orderShopArrayList.get(i);

        final String orderId = modelOrderShop.getOrderId();
        final String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        String orderTo = modelOrderShop.getOrderTo();

        loadUserInfo(modelOrderShop, holderOrderShop);

        holderOrderShop.amountTv.setText("Tổng: $" + orderCost);
        holderOrderShop.statusTv.setText(orderStatus);
        holderOrderShop.orderIdTv.setText("ID:" + orderId);

        if(orderStatus.equals("In Progress")){
            holderOrderShop.statusTv.setTextColor(context.getResources().getColor(R.color.purple_700));
        }
        else if(orderStatus.equals("Completed")){
            holderOrderShop.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else if(orderStatus.equals("Cancelled")){
            holderOrderShop.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holderOrderShop.orderDateTv.setText(formatedDate);

        holderOrderShop.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(context, OrderDetailsSellerActivity.class);
                intent.putExtra("orderId",orderId);
                intent.putExtra("orderBy",orderBy);

                context.startActivity(intent);

            }
        });

    }

    private void loadUserInfo(ModelOrderShop modelOrderShop,final HolderOrderShop holderOrderShop) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = "" + dataSnapshot.child("email").getValue();
                        holderOrderShop.emailTv.setText("Email: "+email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterOrderShop(this,filterList);
        }
        return filter;
    }

    class HolderOrderShop extends RecyclerView.ViewHolder{

        private TextView orderIdTv, orderDateTv, emailTv, amountTv,statusTv;

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);

        }
    }
}
