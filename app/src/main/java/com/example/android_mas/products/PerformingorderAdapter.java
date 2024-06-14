package com.example.android_mas.products;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android_mas.products.Product;
import com.example.android_mas.products.ProductViewHolder;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_mas.Order.PerformingOrderPage;
import com.example.android_mas.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PerformingorderAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<Product> products;
    private String kg;
    private String price;

    public PerformingorderAdapter(ArrayList<Product> products, String kg, String price) {
        this.products = products;
        this.kg = kg;
        this.price = price;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_rv, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productname.setText(product.name);
        holder.productprice.setText(product.pricePerKg);
        holder.nameprofile.setText(product.nameprofile);

        if (!product.productImage.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(product.productImage).into(holder.imgproduct);
        }
        if (!product.producеtProfile.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(product.producеtProfile).into(holder.product_tv);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PerformingOrderPage.class);
            intent.putExtra("userId", product.uid);
            intent.putExtra("productId", product.productId);
            intent.putExtra("productName", product.name);
            intent.putExtra("productDescription", product.description);
            intent.putExtra("productPricePerKg", product.pricePerKg);
            intent.putExtra("productImage", product.productImage);
            intent.putExtra("productKg", kg);
            intent.putExtra("productPrice", price);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

