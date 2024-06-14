package com.example.android_mas.products;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_mas.R;

import java.util.ArrayList;

public class SelectedProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<Product> products;

    public SelectedProductAdapter(ArrayList<Product> products) {
        this.products = products;
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
            Intent intent = new Intent(holder.itemView.getContext(), SelectedProductPage.class);

            // Pass product details through the intent
            intent.putExtra("productId", product.productId);
            intent.putExtra("sellerId", product.uid);

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
