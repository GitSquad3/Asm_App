package com.example.asm_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin, btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        etUsername = findViewById(R.id.etUsernameLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Handle Login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseHelper db = new DatabaseHelper(MainActivity.this);

                    if (db.loginUser(username, password)) {
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        if (db.isAdmin(username)) {
                            // Nếu là admin, chuyển sang AdminDashboardActivity
                            Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivity(intent);
                        } else {
                            // Người dùng thường, chuyển sang ViewActivity
                            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed. Please check your username or password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Handle Register button click
        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
