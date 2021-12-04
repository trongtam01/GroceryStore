package com.example.grocerystore.adapters;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.R;
import com.example.grocerystore.models.ModelCartItem;
import com.example.grocerystore.models.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelOrderedItem> orderedItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> orderedItemArrayList) {
        this.context = context;
        this.orderedItemArrayList = orderedItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,viewGroup,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int i) {
        ModelOrderedItem modelOrderedItem = orderedItemArrayList.get(i);

        String getpId = modelOrderedItem.getpId();
        String name = modelOrderedItem.getName();
        String cost = modelOrderedItem.getCost();
        String price = modelOrderedItem.getPrice();
        String quantity = modelOrderedItem.getQuantity();

        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("$" + price);
        holder.itemPriceTv.setText("$" + cost);
        holder.itemQuantityTv.setText("[" + quantity + ']');


    }

    @Override
    public int getItemCount() {
        return orderedItemArrayList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv,itemPriceTv
                ,itemPriceEachTv,itemQuantityTv;

        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
