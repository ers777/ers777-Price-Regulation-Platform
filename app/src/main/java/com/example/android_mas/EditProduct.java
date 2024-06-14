package com.example.android_mas;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.android_mas.databinding.BuyProductBinding;
import com.example.android_mas.databinding.EditProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProduct extends AppCompatActivity {
    private EditProductBinding binding;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_product);
        binding = EditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Retrieve product details from intent
        String productId = getIntent().getStringExtra("productId");
        String userId = getIntent().getStringExtra("userId");
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPricePerKg = getIntent().getStringExtra("productPricePerKg");
        String productImage = getIntent().getStringExtra("productImage");


        // Find views
        TextView nameTextView = findViewById(R.id.EditproductName);
        TextView descriptionTextView = findViewById(R.id.EditproductDescription);
        TextView priceTextView = findViewById(R.id.EditproductPrice);
        ImageButton deleteButton = findViewById(R.id.editdelete);

        // Display product details
        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(productPricePerKg );

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(binding.EditproductImage);
        }

        // Set up delete button
        deleteButton.setOnClickListener(v -> {
            if (userId != null && productId != null) {
                deleteProduct(userId, productId);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void deleteProduct(String userId, String productId) {
        // Path to the product in the Firebase Database
        DatabaseReference productRef = mDatabase.child("Users").child(userId).child("products").child(productId);

        // Remove the product
        productRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProduct.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditProduct.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
