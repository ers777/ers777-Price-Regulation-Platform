package com.example.android_mas.Order;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivityPerformingOrderPageBinding;

public class PerformingOrderPage extends AppCompatActivity {

    private ActivityPerformingOrderPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerformingOrderPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем данные из интента
        String productId = getIntent().getStringExtra("productId");
        String userId = getIntent().getStringExtra("userId");
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        String productPricePerKg = getIntent().getStringExtra("productPricePerKg");
        String productImage = getIntent().getStringExtra("productImage");
        String productKg = getIntent().getStringExtra("productKg");
        String productPrice = getIntent().getStringExtra("productPrice");

        // Отображаем детали продукта
        binding.PerformingOrderName.setText(productName);
        binding.PerformingOrderDescription.setText(productDescription);
        binding.PerformingOrderPrice.setText(productPrice);
        binding.PerformingOrderKg.setText(productKg);
        binding.PerformingOrderProductPr.setText( productPricePerKg);

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(binding.PerformingOrderImage);
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
