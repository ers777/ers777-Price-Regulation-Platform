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

public class SelectedProductPage extends AppCompatActivity {

    private static final String TAG = "SelectedProductPage";

    private ImageView selectProductImage;
    private TextView selectProductName;
    private TextView selectProductDescription;
    private TextView selectProductPrice;
    private TextView selectQuantityInput;
    private TextView selectTotalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_product_page);

        // Initialize views
        selectProductImage = findViewById(R.id.Select_productImage);
        selectProductName = findViewById(R.id.Select_productName);
        selectProductDescription = findViewById(R.id.Select_productDescription);
        selectProductPrice = findViewById(R.id.Select_productPrice);
        selectQuantityInput = findViewById(R.id.Select_quantityInput);
        selectTotalPrice = findViewById(R.id.Select_quantityInput2);

        String productId = getIntent().getStringExtra("productId");
        String sellerId = getIntent().getStringExtra("sellerId");


        loadProductData(productId);
    }

    private void loadProductData(String productId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("selectproduct")
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
                            selectProductName.setText(productName);
                            selectProductDescription.setText(productDescription);
                            selectProductPrice.setText(productPrice);
                            selectQuantityInput.setText(quantity);
                            selectTotalPrice.setText(totalPrice);

                            if (productImage != null && !productImage.isEmpty()) {
                                Glide.with(SelectedProductPage.this).load(productImage).into(selectProductImage);
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
