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

import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivityProductListBinding;
import com.example.android_mas.products.Product;
import com.example.android_mas.products.ProductAdapter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends Fragment {

    private ActivityProductListBinding binding;
    private ProductAdapter adapter;
    private List<Product> products;
    private ArrayList<Product> filteredProducts;
    private String currentUserUid;
    private int selectedButtonId = R.id.button_assistive;
    private boolean searchByNameProfile = false; // Флаг для отслеживания режима поиска

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadProducts();

        binding.productseacrimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByNameProfile = !searchByNameProfile; // Переключение режима поиска
                updateHintText(); // Обновление текста подсказки
                filterProducts(binding.productseacrimg.getText().toString());
            }
        });

        binding.productseacrimg.addTextChangedListener(new TextWatcher() {
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
                    filterProducts(binding.productseacrimg.getText().toString());
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
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot productsSnapshot = userSnapshot.child("products");
                    if (productsSnapshot.exists()) {
                        String profileImage = userSnapshot.child("profileImage").exists() ? userSnapshot.child("profileImage").getValue().toString() : "";
                        String nameprofile = userSnapshot.child("username").exists() ? userSnapshot.child("username").getValue().toString() : "";

                        for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            String productId = productSnapshot.getKey();
                            String name = productSnapshot.child("name").exists() ? productSnapshot.child("name").getValue().toString() : "No name";
                            String description = productSnapshot.child("description").exists() ? productSnapshot.child("description").getValue().toString() : "No description";
                            String pricePerKg = productSnapshot.child("price_per_kg").exists() ? productSnapshot.child("price_per_kg").getValue().toString() : "0";
                            String productImage = productSnapshot.child("productimg").exists() ? productSnapshot.child("productimg").getValue().toString() : "";

                            Product product = new Product(uid, productId, name, description, pricePerKg, productImage, profileImage, nameprofile);
                            products.add(product);
                            filteredProducts.add(product);
                        }
                    }
                }
                binding.productRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.productRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                adapter = new ProductAdapter(filteredProducts);
                binding.productRv.setAdapter(adapter);
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
            binding.productseacrimg.setHint("Поиск по продавцу");
        } else {
            binding.productseacrimg.setHint("Поиск по названию продукта");
        }
    }
}
