package com.example.grocerystore.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.FilterProductUser;
import com.example.grocerystore.R;
import com.example.grocerystore.activities.ShopDetailsActivity;
import com.example.grocerystore.models.ModelProduct;
import com.example.grocerystore.models.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser  extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int i) {
        final ModelProduct modelProduct = productList.get(i);
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();


        holder.titleTv.setText(productTitle);
        holder.discountedNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("$" + originalPrice);
        holder.discountedPriceTv.setText("$" + discountPrice);

        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);

        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);

        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDiaLog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDiaLog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);

        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        final TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data
        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            // Product have discount
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            // Product don't have discount
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }
        cost = Double.parseDouble(price.replaceAll("",""));
        finalCost = Double.parseDouble(price.replaceAll("",""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }

        titleTv.setText("" + title);
        pQuantityTv.setText("" + productQuantity);
        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("$"+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("$"+modelProduct.getDiscountPrice());
        finalPriceTv.setText("$" + finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity ++;

                finalPriceTv.setText("$" + finalCost);
                quantityTv.setText("" + quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity > 1){
                    finalCost = finalCost - cost;
                    quantity--;

                    finalPriceTv.setText("$"+finalCost);
                    quantityTv.setText("$" + quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().replace("$","");
                String quantity = quantityTv.getText().toString().trim();

                addCart(productId, title, priceEach,totalPrice ,quantity);

                dialog.dismiss();
            }
        });
    }

    private int itemId = 1;
    private void addCart(String productId, String title, String priceEach,String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Price",new String[]{"text","unique"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","unique"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id", itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();

        ((ShopDetailsActivity)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv ;
        private TextView discountedNoteTv, titleTv,descriptionTv, addToCartTv,
                discountedPriceTv,originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);

        }
    }
}
