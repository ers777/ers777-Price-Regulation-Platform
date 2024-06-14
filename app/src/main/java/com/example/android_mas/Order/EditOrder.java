package com.example.android_mas.Order;

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
import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivityEditOrderBinding;
import com.example.android_mas.databinding.BuyProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditOrder extends AppCompatActivity {
    private ActivityEditOrderBinding binding;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_order);
        binding = ActivityEditOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Retrieve order details from intent
        String productId = getIntent().getStringExtra("productId");
        String userId = getIntent().getStringExtra("userId");
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPricePerKg = getIntent().getStringExtra("productPricePerKg");
        String productImage = getIntent().getStringExtra("productImage");
        String productKg = getIntent().getStringExtra("productKg");
        String productPrice = getIntent().getStringExtra("productPrice");

        // Find views
        TextView nameTextView = findViewById(R.id.EditproductName);
        TextView descriptionTextView = findViewById(R.id.EditproductDescription);
        TextView priceTextView = findViewById(R.id.EditproductPrice);
        TextView kgTextView = findViewById(R.id.Editproductkg);
        TextView pricePrTextView = findViewById(R.id.EditproductPr);
        ImageButton deleteButton = findViewById(R.id.editdelete);

        // Display order details
        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(productPricePerKg );
        kgTextView.setText(productKg);
        pricePrTextView.setText(Integer.toString(Integer.parseInt(productPrice)*Integer.parseInt(productKg)));

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(binding.EditproductImageOrder);
        }

        // Set up delete button
        deleteButton.setOnClickListener(v -> {
            if (userId != null && productId != null) {
                deleteOrder(userId, productId);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void deleteOrder(String userId, String productId) {
        // Path to the order in the Firebase Database
        DatabaseReference orderRef = mDatabase.child("Users").child(userId).child("orders").child(productId);

        // Remove the order
        orderRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditOrder.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditOrder.this, "Failed to delete order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
