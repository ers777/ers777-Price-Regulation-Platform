package com.example.android_mas.Order;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivitySelectOrderPageBinding;

public class SelectOrderPage extends AppCompatActivity {

    private ActivitySelectOrderPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectOrderPageBinding.inflate(getLayoutInflater());
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
        binding.SelectOrderName.setText(productName);
        binding.SelectOrderDescription.setText(productDescription);
        binding.SelectOrderPrice.setText(productPricePerKg);
        binding.SelectOrderKg.setText(productKg);
        binding.SelectOrderProductPr.setText(productPrice);

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this).load(productImage).into(binding.SelectOrderImage);
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
