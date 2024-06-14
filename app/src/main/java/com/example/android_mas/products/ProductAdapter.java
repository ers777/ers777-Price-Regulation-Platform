package com.example.android_mas.products;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_mas.EditProduct;
import com.example.android_mas.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<Product> products;
    private String currentUserUid;

    public ProductAdapter(ArrayList<Product> products) {
        this.products = products;
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
                // Open EditProduct if the product belongs to the current user
                intent = new Intent(holder.itemView.getContext(), EditProduct.class);
            } else if (holder.itemView.getContext() instanceof SelectedProductPage) {
                // Open SelectedProductPage if the item is clicked in the selected product context
                intent = new Intent(holder.itemView.getContext(), SelectedProductPage.class);
            } else {
                // Open BuyProduct if the product belongs to another user
                intent = new Intent(holder.itemView.getContext(), BuyProduct.class);
            }

            // Pass product details through the intent
            intent.putExtra("userId", product.uid);
            intent.putExtra("productId", product.productId);
            intent.putExtra("productName", product.name);
            intent.putExtra("productDescription", product.description);
            intent.putExtra("productPricePerKg", product.pricePerKg);
            intent.putExtra("productImage", product.productImage);
            intent.putExtra("sellerId", product.uid); // Добавить айди продавца

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
