package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonRegister;
    private Button mButtonSendToLogin;

    // Variables para registrar

    private String name = "";
    private  String email = "";
    private  String password = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mButtonRegister = (Button) findViewById(R.id.btnRegister);
        mButtonSendToLogin = (Button) findViewById(R.id.btnSendToLogin);

        mButtonRegister.setOnClickListener((view) -> {
            name = mEditTextName.getText().toString();
            email = mEditTextEmail.getText().toString();
            password = mEditTextPassword.getText().toString();
            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                if (password.length() >= 6){
                    registerUser();
                }
                else {
                    Toast.makeText(MainActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(MainActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
            }
        } );

        mButtonSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

    }
    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()){
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("password", password);

                String id = mAuth.getCurrentUser().getUid();

                mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
        }
    }
}