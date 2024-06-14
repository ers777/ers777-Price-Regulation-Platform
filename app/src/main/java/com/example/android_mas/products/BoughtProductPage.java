package com.example.android_mas.products;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android_mas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoughtProductPage extends AppCompatActivity {

    private static final String TAG = "BoughtProductPage";

    private ImageView boughtProductImage;
    private TextView boughtProductName;
    private TextView boughtProductDescription;
    private TextView boughtProductPrice;
    private TextView boughtQuantityInput;
    private TextView boughtTotalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_product_page);

        // Initialize views
        boughtProductImage = findViewById(R.id.Bought_productImage);
        boughtProductName = findViewById(R.id.Bought_productName);
        boughtProductDescription = findViewById(R.id.Bought_productDescription);
        boughtProductPrice = findViewById(R.id.Bought_productPrice);
        boughtQuantityInput = findViewById(R.id.Bought_quantityInput);
        boughtTotalPrice = findViewById(R.id.Bought_quantityInput2);

        String productId = getIntent().getStringExtra("productId");
        String sellerId = getIntent().getStringExtra("sellerId");

        loadProductData(productId);
    }

    private void loadProductData(String productId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("boughtproduct")
                .child(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String productName = snapshot.child("name").getValue(String.class);
                            String productDescription = snapshot.child("description").getValue(String.class);
                            String productPrice = snapshot.child("price_per_kg").getValue(Long.class).toString();
                            String productImage = snapshot.child("productimg").getValue(String.class);
                            String quantity = snapshot.child("quantity").getValue(Long.class).toString();
                            String totalPrice = snapshot.child("total_price").getValue(Long.class).toString();

                            // Display product details
                            boughtProductName.setText(productName);
                            boughtProductDescription.setText(productDescription);
                            boughtProductPrice.setText( productPrice);
                            boughtQuantityInput.setText(quantity);
                            boughtTotalPrice.setText(totalPrice);

                            if (productImage != null && !productImage.isEmpty()) {
                                Glide.with(BoughtProductPage.this).load(productImage).into(boughtProductImage);
                            }
                        } else {
                            Log.d(TAG, "Snapshot does not exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                    }
                });
    }
}
