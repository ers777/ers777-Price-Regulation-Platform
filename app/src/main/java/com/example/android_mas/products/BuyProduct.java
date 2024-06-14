package com.example.android_mas.products;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.android_mas.ChatActivity;
import com.example.android_mas.databinding.BuyProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BuyProduct extends AppCompatActivity {

    private BuyProductBinding binding;
    private double productPricePerKg;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BuyProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        // Получить данные о продукте из Intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPrice = getIntent().getStringExtra("productPricePerKg");
        String productImage = getIntent().getStringExtra("productImage");
        String sellerId = getIntent().getStringExtra("sellerId"); // Получить айди продавца из Intent

        // Отобразить данные о продукте
        binding.EditproductName.setText(productName);
        binding.EditproductDescription.setText(productDescription);
        binding.EditproductPrice.setText("Price: " + productPrice);

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(binding.EditproductImage);
        }

        // Парсинг цены продукта
        productPricePerKg = Double.parseDouble(productPrice);

        // Установить TextWatcher для количества
        binding.quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не требуется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Обновить общую стоимость
                updateTotalPrice(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не требуется
            }
        });

        // Disable the pay button initially
        binding.payButton.setEnabled(false);

        // Set up click listener for the pay button
        binding.payButton.setOnClickListener(view -> addProductToFirebase(productName, productDescription, productPricePerKg, productImage, sellerId));

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateTotalPrice(String quantityText) {
        double quantity = 0;
        if (!quantityText.isEmpty()) {
            quantity = Double.parseDouble(quantityText);
        }
        double totalPrice = quantity * productPricePerKg;
        binding.totalPrice.setText(String.format("%.1f", totalPrice));

        // Enable or disable the pay button based on the quantity input
        binding.payButton.setEnabled(quantity > 0);
    }

    private void addProductToFirebase(String productName, String productDescription, double productPrice, String productImage, String sellerId) {
        String quantityText = binding.quantityInput.getText().toString();
        double quantity = Double.parseDouble(quantityText);
        double totalPrice = quantity * productPrice;

        // Get the user ID and reference to the selectproduct node
        String buyerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference buyerRef = mDatabase.child("Users").child(buyerId).child("selectproduct");

        String productId = buyerRef.push().getKey();
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("description", productDescription);
        product.put("price_per_kg", productPrice);
        product.put("productimg", productImage);
        product.put("quantity", quantity);
        product.put("total_price", totalPrice);
        product.put("seller_id", sellerId);
        product.put("product_id", productId); // Добавить идентификатор товара

        // Add product to buyer's selected products
        buyerRef.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BuyProduct.this, "Product added to cart", Toast.LENGTH_SHORT).show();
                    binding.quantityInput.setText("");
                    binding.totalPrice.setText("0.0 тг");
                    binding.payButton.setEnabled(false);

                    // Add product to seller's bought products
                    addProductToSeller(productName, productDescription, productPrice, productImage, quantity, totalPrice, sellerId, buyerId, productId);

                    // Создать чат и открыть его
                })
                .addOnFailureListener(e -> Toast.makeText(BuyProduct.this, "Failed to add product to cart", Toast.LENGTH_SHORT).show());
    }

    private void addProductToSeller(String productName, String productDescription, double productPrice, String productImage, double quantity, double totalPrice, String sellerId, String buyerId, String productId) {
        DatabaseReference sellerRef = mDatabase.child("Users").child(sellerId).child("boughtproduct");

        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("description", productDescription);
        product.put("price_per_kg", productPrice);
        product.put("productimg", productImage);
        product.put("quantity", quantity);
        product.put("total_price", totalPrice);
        product.put("buyer_id", buyerId);
        product.put("product_id", productId); // Добавить идентификатор товара

        sellerRef.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> Toast.makeText(BuyProduct.this, "Product added to seller's bought products", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(BuyProduct.this, "Failed to add product to seller's bought products", Toast.LENGTH_SHORT).show());
    }

    private void createChatAndOpen(String buyerId, String sellerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = database.getReference("Chats");

        String chatId = chatRef.push().getKey();
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("user1", buyerId);
        chatData.put("user2", sellerId);
        chatData.put("chat_id", chatId);

        chatRef.child(chatId).setValue(chatData).addOnSuccessListener(aVoid -> {
            openChatActivity(chatId);
        }).addOnFailureListener(e -> {
            Toast.makeText(BuyProduct.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
        });
    }

    private void openChatActivity(String chatId) {
        Intent intent = new Intent(BuyProduct.this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
    }
}
