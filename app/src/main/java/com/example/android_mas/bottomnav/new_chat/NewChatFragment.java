package com.example.android_mas.bottomnav.new_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_mas.Order.OrderCreateFragment;
import com.example.android_mas.Order.OrderList;
import com.example.android_mas.Order.PerformingorderOrder;
import com.example.android_mas.Order.SelectOrder;
import com.example.android_mas.R;
import com.example.android_mas.bottomnav.chats.ChatFragment;
import com.example.android_mas.burger_nav.BoughtProduct;
import com.example.android_mas.burger_nav.ProductFragment;
import com.example.android_mas.burger_nav.ProductList;
import com.example.android_mas.burger_nav.SelectProduct;
import com.example.android_mas.databinding.FragmentNewChatBinding;
import com.example.android_mas.users.User;
import com.example.android_mas.users.UsersAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewChatFragment extends Fragment {

    private FragmentNewChatBinding binding;
    private Map<Integer, Fragment> fragmentMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);


        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.SelectProduct, new SelectProduct());
        fragmentMap.put(R.id.CreateProduct, new ProductFragment());
        fragmentMap.put(R.id.BoughtProduct, new BoughtProduct());

        fragmentMap.put(R.id.OrderCreateFragment, new OrderCreateFragment());
        fragmentMap.put(R.id.SelectOrder, new SelectOrder());
        fragmentMap.put(R.id.PerformingorderOrder, new PerformingorderOrder());


        fragmentMap.put(R.id.Chats, new ChatFragment());
        fragmentMap.put(R.id.contact, new NewChatFragment());

        //chats
        //contact
        loadUsers();

        binding.memuImgChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.open();
            }
        });

        binding.navViewBurger.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                Fragment selectedFragment = fragmentMap.get(itemId);
                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                } else {
                    Toast.makeText(getContext(), "123", Toast.LENGTH_SHORT).show();
                }

                binding.drawerLayout.close();
                return true;
            }
        });

        return binding.getRoot();
    }

    private void loadUsers() {
        ArrayList<User> users = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        continue;
                    }

                    String username = userSnapshot.child("username").getValue().toString();
                    String profileImage = userSnapshot.child("profileImage").getValue().toString();
                    String uid = userSnapshot.getKey();

                    users.add(new User(username, profileImage, uid));
                }
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(new UsersAdapter(users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
