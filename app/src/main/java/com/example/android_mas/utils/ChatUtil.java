package com.example.android_mas.utils;

import androidx.annotation.NonNull;

import com.example.android_mas.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class ChatUtil {

    public static void createChat(User user, Consumer<String> callback) {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String chatId = generateChatId(uid, user.uid);

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            HashMap<String, String> chatInfo = new HashMap<>();
                            chatInfo.put("user1", uid);
                            chatInfo.put("user2", user.uid);
                            FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                                    .setValue(chatInfo);

                            addChatIdToUser(uid, chatId);
                            addChatIdToUser(user.uid, chatId);
                        }
                        callback.accept(chatId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибок
                    }
                });
    }

    private static String generateChatId(String userId1, String userId2) {
        String sumUser1User2 = userId1 + userId2;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private static void addChatIdToUser(String uid, String chatId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String chats = task.getResult().getValue().toString();
                        if (!chats.contains(chatId)) {
                            String chatsUpd = addIdToStr(chats, chatId);

                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                    .child("chats").setValue(chatsUpd);
                        }
                    }
                });
    }

    private static String addIdToStr(String str, String chatId) {
        if (!str.contains(chatId)) {
            str += (str.isEmpty()) ? chatId : ("," + chatId);
        }
        return str;
    }
}
