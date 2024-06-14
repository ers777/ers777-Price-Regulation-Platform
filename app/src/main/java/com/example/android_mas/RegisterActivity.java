package com.example.android_mas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_mas.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Dummy data for spinners
        String[] farmers = {"Farmer", "Customer"};

        // Setting up the Farmer Spinner
        Spinner farmerSpinner = findViewById(R.id.farmer_spinner);
        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, farmers);
        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmerSpinner.setAdapter(farmerAdapter);

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.MailText.getText().toString().isEmpty()|| binding.PasswordText.getText().toString().isEmpty()
                        || binding.usernameEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Get selected items from the spinners
                    String selectedFarmer = farmerSpinner.getSelectedItem().toString();

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.MailText.getText().toString(),binding.PasswordText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String,String> userInfo=new HashMap<>();
                                        userInfo.put("email",binding.MailText.getText().toString());
                                        userInfo.put("username",binding.usernameEt.getText().toString());
                                        userInfo.put("profileImage","");
                                        userInfo.put("chats","");
                                        userInfo.put("products","");
                                        userInfo.put("boughtproduct","");
                                        userInfo.put("selectproduct","");
                                        userInfo.put("orders","");
                                        userInfo.put("category", selectedFarmer);

                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userInfo);
                                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                    }
                                }
                            });
                }
            }
        });

        // Add this part to handle the imageView2 click event
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}
