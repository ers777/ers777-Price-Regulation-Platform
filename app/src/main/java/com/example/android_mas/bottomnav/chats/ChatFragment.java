package com.example.android_mas.bottomnav.chats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_mas.Order.OrderCreateFragment;
import com.example.android_mas.Order.OrderList;
import com.example.android_mas.Order.PerformingorderOrder;
import com.example.android_mas.Order.SelectOrder;
import com.example.android_mas.R;
import com.example.android_mas.bottomnav.new_chat.NewChatFragment;
import com.example.android_mas.burger_nav.BoughtProduct;
import com.example.android_mas.burger_nav.ProductFragment;
import com.example.android_mas.burger_nav.ProductList;
import com.example.android_mas.burger_nav.SelectProduct;
import com.example.android_mas.chats.Chat;
import com.example.android_mas.chats.ChatsAdapter;
import com.example.android_mas.databinding.FragmentChatsBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private FragmentChatsBinding binding;
    private Map<Integer, Fragment> fragmentMap;
    private ArrayList<Chat> chats;
    private ArrayList<Chat> filteredChats;
    private ChatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater,container,false);
        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.SelectProduct, new SelectProduct());
        fragmentMap.put(R.id.CreateProduct, new ProductFragment());
        fragmentMap.put(R.id.BoughtProduct, new BoughtProduct());
        fragmentMap.put(R.id.OrderCreateFragment, new OrderCreateFragment());
        fragmentMap.put(R.id.SelectOrder, new SelectOrder());
        fragmentMap.put(R.id.PerformingorderOrder, new PerformingorderOrder());
        fragmentMap.put(R.id.Chats, new ChatFragment());
        fragmentMap.put(R.id.contact, new NewChatFragment());

        loadChats();

        binding.memuImgNewchat.setOnClickListener(new View.OnClickListener() {
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

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        return binding.getRoot();
    }

    private void loadChats() {
        chats = new ArrayList<>();
        filteredChats = new ArrayList<>();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("chats").getValue()).toString();
                String[] chatsIds = chatsStr.split(",");
                if (chatsStr.isEmpty()) return;

                for (String chatId : chatsIds) {
                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                    String userId1 = Objects.requireNonNull(chatSnapshot.child("user1").getValue()).toString();
                    String userId2 = Objects.requireNonNull(chatSnapshot.child("user2").getValue()).toString();

                    String chatUserId = (uid.equals(userId1)) ? userId2 : userId1;
                    String chatName = Objects.requireNonNull(snapshot.child("Users").child(chatUserId).child("username").getValue()).toString();

                    Chat chat = new Chat(chatId, chatName, userId1, userId2);
                    chats.add(chat);
                    filteredChats.add(chat);
                }

                binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new ChatsAdapter(filteredChats);
                binding.chatsRv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get user chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterChats(String query) {
        filteredChats.clear();
        for (Chat chat : chats) {
            if (chat.getChat_name().toLowerCase().contains(query.toLowerCase())) {
                filteredChats.add(chat);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
