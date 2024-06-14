package com.example.android_mas.products;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_mas.Order.EditOrder;
import com.example.android_mas.Order.TakeOrder;
import com.example.android_mas.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<Product> products;
    private String currentUserUid;
    private String kg;
    private String price;

    public OrderAdapter(ArrayList<Product> products,String kg , String price) {
        this.products = products;
        this.kg = kg;
        this.price = price;
        this.currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            Intent intent;
            if (product.uid.equals(currentUserUid)) {
                // Open EditOrder if the product belongs to the current user
                intent = new Intent(holder.itemView.getContext(), EditOrder.class);
            } else {
                // Open TakeOrder if the product belongs to another user
                intent = new Intent(holder.itemView.getContext(), TakeOrder.class);
            }

            // Pass product details through the intent
            intent.putExtra("userId", product.uid);
            intent.putExtra("productId", product.productId);
            intent.putExtra("productName", product.name);
            intent.putExtra("productDescription", product.description);
            intent.putExtra("productPricePerKg", product.pricePerKg);
            intent.putExtra("productImage", product.productImage);
            intent.putExtra("sellerId", product.uid); // Add seller ID
            intent.putExtra("productKg", kg); // Add seller ID
            intent.putExtra("productPrice", price); // Add seller ID

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
