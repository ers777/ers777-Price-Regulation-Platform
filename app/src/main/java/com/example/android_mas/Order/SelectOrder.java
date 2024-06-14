package com.example.android_mas.Order;

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

import com.example.android_mas.databinding.ActivitySelectOrderBinding;
import com.example.android_mas.products.Product;
import com.example.android_mas.products.SelectOrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectOrder extends Fragment {

    private ActivitySelectOrderBinding binding;
    private SelectOrderAdapter adapter;
    private List<Product> products;
    private ArrayList<Product> filteredProducts;
    private String currentUserUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySelectOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        products = new ArrayList<>();
        filteredProducts = new ArrayList<>(products);

        binding.selectOrderRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.selectOrderRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new SelectOrderAdapter(filteredProducts, "", ""); // Initialize with empty kg and price for now
        binding.selectOrderRv.setAdapter(adapter);

        loadSelectedOrders();

        binding.selectOrderSeacrimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOrders(binding.selectOrderSeacrimg.getText().toString());
            }
        });

        binding.selectOrderSeacrimg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void loadSelectedOrders() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(currentUserUid)
                .child("selectorder")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            String orderId = orderSnapshot.getKey();
                            String name = orderSnapshot.child("name").exists() ? orderSnapshot.child("name").getValue().toString() : "No name";
                            String description = orderSnapshot.child("description").exists() ? orderSnapshot.child("description").getValue().toString() : "No description";
                            String pricePerKg = orderSnapshot.child("price_per_kg").exists() ? orderSnapshot.child("price_per_kg").getValue().toString() : "0";
                            String kg = orderSnapshot.child("kg").exists() ? orderSnapshot.child("kg").getValue().toString() : "0";
                            String productImage = orderSnapshot.child("productimg").exists() ? orderSnapshot.child("productimg").getValue().toString() : "";
                            String sellerId = orderSnapshot.child("seller_id").exists() ? orderSnapshot.child("seller_id").getValue().toString() : "";
                            String buyerId = currentUserUid;

                            loadSellerData(sellerId, orderId, name, description, Integer.toString(Integer.parseInt(pricePerKg) * Integer.parseInt(kg)), productImage, buyerId, kg, pricePerKg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling
                    }
                });
    }

    private void loadSellerData(String sellerId, String orderId, String name, String description, String pricePerKg, String productImage, String buyerId, String kg, String price) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sellerSnapshot) {
                        String profileImage = sellerSnapshot.child("profileImage").exists() ? sellerSnapshot.child("profileImage").getValue().toString() : "";
                        String nameProfile = sellerSnapshot.child("username").exists() ? sellerSnapshot.child("username").getValue().toString() : "";

                        Product product = new Product(sellerId, orderId, name, description, pricePerKg, productImage, profileImage, nameProfile);
                        products.add(product);
                        filteredProducts.add(product);

                        adapter = new SelectOrderAdapter(filteredProducts, kg, price); // Initialize with empty kg and price for now
                        binding.selectOrderRv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling
                    }
                });
    }

    private void filterOrders(String query) {
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
