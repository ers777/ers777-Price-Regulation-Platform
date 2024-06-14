package com.example.android_mas.Order;

import static java.lang.Integer.parseInt;

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

import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivityOrderListBinding;
import com.example.android_mas.products.OrderAdapter;
import com.example.android_mas.products.Product;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends Fragment {

    private ActivityOrderListBinding binding;
    private OrderAdapter adapter;
    private List<Product> products;
    private ArrayList<Product> filteredProducts;
    private String currentUserUid;
    private int selectedButtonId = R.id.button_expert;
    private boolean searchByNameProfile = false; // Flag for tracking search mode

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityOrderListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadProducts();

        binding.orderseacrimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByNameProfile = !searchByNameProfile; // Toggle search mode
                updateHintText(); // Update hint text
                filterProducts(binding.orderseacrimg.getText().toString());
            }
        });

        binding.orderseacrimg.addTextChangedListener(new TextWatcher() {
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

        binding.toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    selectedButtonId = checkedId;
                    filterProducts(binding.orderseacrimg.getText().toString());
                }
            }
        });
    }

    private void loadProducts() {
        products = new ArrayList<>();
        filteredProducts = new ArrayList<>(products);

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String price = "";
                String kg = "";
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot productsSnapshot = userSnapshot.child("orders");
                    if (productsSnapshot.exists()) {
                        String profileImage = userSnapshot.child("profileImage").exists() ? userSnapshot.child("profileImage").getValue().toString() : "";
                        String nameprofile = userSnapshot.child("username").exists() ? userSnapshot.child("username").getValue().toString() : "";

                        for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            String productId = productSnapshot.getKey();
                            String name = productSnapshot.child("name").exists() ? productSnapshot.child("name").getValue().toString() : "No name";
                            String description = productSnapshot.child("description").exists() ? productSnapshot.child("description").getValue().toString() : "No description";
                            price = productSnapshot.child("price").exists() ? productSnapshot.child("price").getValue().toString() : "0";
                            kg = productSnapshot.child("kg").exists() ? productSnapshot.child("kg").getValue().toString() : "0";
                            String pricePerKg = Integer.toString(parseInt(price) / parseInt(kg));
                            String productImage = productSnapshot.child("orderimg").exists() ? productSnapshot.child("orderimg").getValue().toString() : "";

                            Product product = new Product(uid, productId, name, description, pricePerKg, productImage, profileImage, nameprofile);
                            products.add(product);
                            filteredProducts.add(product);
                        }
                    }
                }
                binding.orderRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.orderRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                adapter = new OrderAdapter(filteredProducts, kg, price);
                binding.orderRv.setAdapter(adapter);
                filterProducts(binding.orderseacrimg.getText().toString());
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
            boolean matchesQuery;
            if (searchByNameProfile) {
                matchesQuery = product.nameprofile.toLowerCase().contains(query.toLowerCase());
            } else {
                matchesQuery = product.name.toLowerCase().contains(query.toLowerCase()) ||
                        product.description.toLowerCase().contains(query.toLowerCase());
            }
            boolean matchesUserFilter = selectedButtonId == R.id.button_assistive ? product.getUid().equals(currentUserUid) : !product.getUid().equals(currentUserUid);
            if (matchesQuery && matchesUserFilter) {
                filteredProducts.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateHintText() {
        if (searchByNameProfile) {
            binding.orderseacrimg.setHint("Поиск по продавцу");
        } else {
            binding.orderseacrimg.setHint("Поиск по названию продукта");
        }
    }
}
