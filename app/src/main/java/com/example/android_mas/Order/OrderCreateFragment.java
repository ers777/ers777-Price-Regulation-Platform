package com.example.android_mas.Order;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_mas.R;
import com.example.android_mas.databinding.ActivityOrderCreateBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class OrderCreateFragment extends Fragment {

    private EditText orderName, orderDescription, orderPrice, orderKg;
    private Button saveOrderButton;
    private DatabaseReference db;
    private ActivityOrderCreateBinding binding;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_create, container, false);

        orderName = view.findViewById(R.id.order_name);
        orderDescription = view.findViewById(R.id.order_description);
        orderPrice = view.findViewById(R.id.order_price);
        orderKg = view.findViewById(R.id.order_kg);
        saveOrderButton = view.findViewById(R.id.save_order_button);
        ImageView alltexttemove = view.findViewById(R.id.alltexttemove);
        ImageView removeimg = view.findViewById(R.id.removeimg);
        binding = ActivityOrderCreateBinding.bind(view);

        db = FirebaseDatabase.getInstance().getReference();

        binding.orderImg.setOnClickListener(v -> selectImage());

        saveOrderButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            } else {
                saveOrder("");
            }
        });

        // Скрыть кнопки при создании фрагмента
        alltexttemove.setVisibility(View.GONE);
        removeimg.setVisibility(View.GONE);

        // Добавить TextWatcher для отображения alltexttemove
        orderName.addTextChangedListener(textWatcher);
        orderDescription.addTextChangedListener(textWatcher);
        orderPrice.addTextChangedListener(textWatcher);
        orderKg.addTextChangedListener(textWatcher);

        // Обработчики кликов для кнопок очистки
        alltexttemove.setOnClickListener(v -> clearTextFields());
        removeimg.setOnClickListener(v -> clearImage());

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.orderImg.setImageURI(imageUri);

            // Показать кнопку removeimg при выборе изображения
            ImageView removeimg = getView().findViewById(R.id.removeimg);
            removeimg.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("order_images").child(userId);

        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveOrder(imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveOrder(String imageUrl) {
        String name = orderName.getText().toString().trim();
        String description = orderDescription.getText().toString().trim();
        String price = orderPrice.getText().toString().trim();
        String kg = orderKg.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || kg.isEmpty()) {
            Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userOrdersRef = db.child("Users").child(userId).child("orders");

        String orderId = userOrdersRef.push().getKey();
        Map<String, Object> order = new HashMap<>();
        order.put("name", name);
        order.put("description", description);
        order.put("price", Double.parseDouble(price));
        order.put("kg", Double.parseDouble(kg));
        order.put("orderimg", imageUrl);

        userOrdersRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Заказ добавлен", Toast.LENGTH_SHORT).show();
                    orderName.setText("");
                    orderDescription.setText("");
                    orderPrice.setText("");
                    orderKg.setText("");
                    binding.orderImg.setImageResource(R.drawable.selected); // Замените изображение после успешной отправки

                    // Вернуть изображение обратно через 3 секунды
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.orderImg.setImageResource(R.drawable.selectimg);
                    }, 3000); // 3000 миллисекунд = 3 секунды
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка при добавлении заказа", Toast.LENGTH_SHORT).show());
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void checkFieldsForEmptyValues() {
        ImageView alltexttemove = getView().findViewById(R.id.alltexttemove);
        String name = orderName.getText().toString().trim();
        String description = orderDescription.getText().toString().trim();
        String price = orderPrice.getText().toString().trim();
        String kg = orderKg.getText().toString().trim();

        if (!name.isEmpty() && !description.isEmpty() && !price.isEmpty() && !kg.isEmpty()) {
            alltexttemove.setVisibility(View.VISIBLE);
        } else {
            alltexttemove.setVisibility(View.GONE);
        }
    }

    private void clearTextFields() {
        orderName.setText("");
        orderDescription.setText("");
        orderPrice.setText("");
        orderKg.setText("");
    }

    private void clearImage() {
        binding.orderImg.setImageResource(R.drawable.selectimg);
        imageUri = null;
        ImageView removeimg = getView().findViewById(R.id.removeimg);
        removeimg.setVisibility(View.GONE);
    }
}
