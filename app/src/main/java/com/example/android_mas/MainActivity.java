package com.example.android_mas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.android_mas.Order.OrderList;
import com.example.android_mas.bottomnav.chats.ChatFragment;
import com.example.android_mas.bottomnav.new_chat.NewChatFragment;
import com.example.android_mas.bottomnav.profile.ProfileFragment;
import com.example.android_mas.burger_nav.ProductList;
import com.example.android_mas.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton imageButton;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return; // Добавьте return, чтобы предотвратить выполнение оставшегося кода в случае перенаправления на LoginActivity
        }

        // Инициализация карты фрагментов
        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.Chats, new ChatFragment());
        fragmentMap.put(R.id.Product2, new ProductList());
        fragmentMap.put(R.id.Order, new OrderList());
        fragmentMap.put(R.id.profile, new ProfileFragment());


        binding.bottomNav.setSelectedItemId(R.id.Product2);
        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new ProductList()).commit();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();
            }
            return true;
        });
    }

    public void openRegisterActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
