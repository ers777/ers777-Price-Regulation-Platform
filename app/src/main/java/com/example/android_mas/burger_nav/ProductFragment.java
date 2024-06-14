package com.example.android_mas.burger_nav;

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
import com.example.android_mas.databinding.ActivityProductFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProductFragment extends Fragment {

    private EditText productName, productDescription, productPrice;
    private Button saveProductButton;
    private DatabaseReference db;
    private ActivityProductFragmentBinding binding;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_product_fragment, container, false);

        productName = view.findViewById(R.id.product_name);
        productDescription = view.findViewById(R.id.product_description);
        productPrice = view.findViewById(R.id.product_price);
        saveProductButton = view.findViewById(R.id.save_product_button);
        ImageView alltexttemove = view.findViewById(R.id.alltexttemove);
        ImageView removeimg = view.findViewById(R.id.removeimg);
        binding = ActivityProductFragmentBinding.bind(view);

        db = FirebaseDatabase.getInstance().getReference();

        binding.productImg.setOnClickListener(v -> selectImage());

        saveProductButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            } else {
                saveProduct("");
            }
        });

        // Скрыть кнопки при создании фрагмента
        alltexttemove.setVisibility(View.GONE);
        removeimg.setVisibility(View.GONE);

        // Добавить TextWatcher для отображения alltexttemove
        productName.addTextChangedListener(textWatcher);
        productDescription.addTextChangedListener(textWatcher);
        productPrice.addTextChangedListener(textWatcher);

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
            binding.productImg.setImageURI(imageUri);

            // Показать кнопку removeimg при выборе изображения
            ImageView removeimg = getView().findViewById(R.id.removeimg);
            removeimg.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("product_images").child(userId);

        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveProduct(imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveProduct(String imageUrl) {
        String name = productName.getText().toString().trim();
        String description = productDescription.getText().toString().trim();
        String price = productPrice.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userProductsRef = db.child("Users").child(userId).child("products");

        String productId = userProductsRef.push().getKey();
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", description);
        product.put("price_per_kg", Double.parseDouble(price));
        product.put("productimg", imageUrl);

        userProductsRef.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show();
                    productName.setText("");
                    productDescription.setText("");
                    productPrice.setText("");
                    binding.productImg.setImageResource(R.drawable.selected); // Замените изображение после успешной отправки

                    // Вернуть изображение обратно через 3 секунды
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.productImg.setImageResource(R.drawable.selectimg);
                    }, 3000); // 3000 миллисекунд = 3 секунды
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка при добавлении продукта", Toast.LENGTH_SHORT).show());
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
        String name = productName.getText().toString().trim();
        String description = productDescription.getText().toString().trim();
        String price = productPrice.getText().toString().trim();

        if (!name.isEmpty() && !description.isEmpty() && !price.isEmpty()) {
            alltexttemove.setVisibility(View.VISIBLE);
        } else {
            alltexttemove.setVisibility(View.GONE);
        }
    }

    private void clearTextFields() {
        productName.setText("");
        productDescription.setText("");
        productPrice.setText("");
    }

    private void clearImage() {
        binding.productImg.setImageResource(R.drawable.selectimg);
        imageUri = null;
        ImageView removeimg = getView().findViewById(R.id.removeimg);
        removeimg.setVisibility(View.GONE);
    }
}
