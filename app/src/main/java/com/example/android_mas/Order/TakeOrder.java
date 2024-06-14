package com.example.android_mas.Order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.android_mas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TakeOrder extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_order);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Retrieve order details from intent
        String productId = getIntent().getStringExtra("productId");
        String userId = getIntent().getStringExtra("userId");

        // Find views
        TextView nameTextView = findViewById(R.id.EditproductName);
        TextView descriptionTextView = findViewById(R.id.EditproductDescription);
        TextView priceTextView = findViewById(R.id.EditproductPrice);
        TextView kgTextView = findViewById(R.id.Editproductkg);
        TextView pricePrTextView = findViewById(R.id.EditproductPr);
        ImageView imageView = findViewById(R.id.EditproductImage);
        Button completeOrderButton = findViewById(R.id.complete_order_btn);

        // Load product details from Firebase
        if (userId != null && productId != null) {
            mDatabase.child("Users").child(userId).child("orders").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String productName = snapshot.child("name").exists() ? snapshot.child("name").getValue().toString() : "No name";
                        String productDescription = snapshot.child("description").exists() ? snapshot.child("description").getValue().toString() : "No description";
                        String productPricePerKg = snapshot.child("price").exists() ? snapshot.child("price").getValue().toString() : "0";
                        String productKg = snapshot.child("kg").exists() ? snapshot.child("kg").getValue().toString() : "0";
                        String productImage = snapshot.child("productimg").exists() ? snapshot.child("productimg").getValue().toString() : "";

                        // Display product details
                        nameTextView.setText(productName);
                        descriptionTextView.setText(productDescription);
                        priceTextView.setText(productPricePerKg);
                        kgTextView.setText(productKg);
                        pricePrTextView.setText(Integer.toString(Integer.parseInt(productPricePerKg)*Integer.parseInt(productKg)));

                        if (productImage != null && !productImage.isEmpty()) {
                            Glide.with(TakeOrder.this).load(productImage).into(imageView);
                        }

                        // Set up complete order button
                        completeOrderButton.setOnClickListener(v -> {
                            addOrderToFirebase(userId, productId, productName, productDescription, productPricePerKg, productKg, productImage);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TakeOrder.this, "Failed to load product", Toast.LENGTH_SHORT).show();
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addOrderToFirebase(String userId, String productId, String productName, String productDescription, String productPricePerKg, String productKg, String productImage) {
        String buyerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference selectOrderRef = mDatabase.child("Users").child(buyerId).child("selectorder");
        DatabaseReference performingOrderRef = mDatabase.child("Users").child(userId).child("performingorder");

        String newOrderId = selectOrderRef.push().getKey();

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("name", productName);
        orderData.put("description", productDescription);
        orderData.put("price_per_kg", productPricePerKg);
        orderData.put("kg", productKg);
        orderData.put("productimg", productImage);
        orderData.put("order_id", newOrderId);
        orderData.put("buyer_id", buyerId);
        orderData.put("seller_id", userId);

        selectOrderRef.child(newOrderId).setValue(orderData)
                .addOnSuccessListener(aVoid -> {
                    performingOrderRef.child(newOrderId).setValue(orderData)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(TakeOrder.this, "Order completed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(TakeOrder.this, "Failed to add order to seller's performing orders", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(TakeOrder.this, "Failed to add order to buyer's selected orders", Toast.LENGTH_SHORT).show());
    }
}
