package com.example.android_mas.burger_nav;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_mas.databinding.BoughtProductBinding;
import com.example.android_mas.products.Product;
import com.example.android_mas.products.BoughtProductAdapter;
import com.example.android_mas.products.SelectedProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BoughtProduct extends Fragment {

    private BoughtProductBinding binding;
    private BoughtProductAdapter adapter;
    private List<Product> products;
    private ArrayList<Product> filteredProducts;
    private String currentUserUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BoughtProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadBoughtProducts();

        binding.boughtProductseacrimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProducts(binding.boughtProductseacrimg.getText().toString());
            }
        });

        binding.boughtProductseacrimg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void loadBoughtProducts() {
        products = new ArrayList<>();
        filteredProducts = new ArrayList<>(products);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(currentUserUid)
                .child("boughtproduct")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            String productId = productSnapshot.getKey();
                            String name = productSnapshot.child("name").exists() ? productSnapshot.child("name").getValue().toString() : "No name";
                            String description = productSnapshot.child("description").exists() ? productSnapshot.child("description").getValue().toString() : "No description";
                            String pricePerKg = productSnapshot.child("price_per_kg").exists() ? productSnapshot.child("price_per_kg").getValue().toString() : "0";
                            String productImage = productSnapshot.child("productimg").exists() ? productSnapshot.child("productimg").getValue().toString() : "";
                            String sellerId = productSnapshot.child("seller_id").exists() ? productSnapshot.child("seller_id").getValue().toString() : "";
                            String buyerId = currentUserUid;

                            loadSellerData(sellerId, productId, name, description, pricePerKg, productImage, buyerId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling
                    }
                });
    }

    private void loadSellerData(String sellerId, String productId, String name, String description, String pricePerKg, String productImage, String buyerId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(buyerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sellerSnapshot) {
                        String profileImage = sellerSnapshot.child("profileImage").exists() ? sellerSnapshot.child("profileImage").getValue().toString() : "";
                        String nameProfile = sellerSnapshot.child("username").exists() ? sellerSnapshot.child("username").getValue().toString() : "";

                        Product product = new Product(sellerId, productId, name, description, pricePerKg, productImage, profileImage, nameProfile);
                        products.add(product);
                        filteredProducts.add(product);

                        binding.boughtProductRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.boughtProductRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                        adapter = new BoughtProductAdapter(filteredProducts);
                        binding.boughtProductRv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling
                    }
                });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        for (Product product : products) {
            if (product.name.toLowerCase().contains(query.toLowerCase()) ||
                    product.description.toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
