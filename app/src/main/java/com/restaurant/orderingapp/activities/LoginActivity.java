package com.restaurant.orderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.utils.FirebaseHelper;
import com.restaurant.orderingapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilLoginEmail);
        tilPassword = findViewById(R.id.tilLoginPassword);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.loginProgressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            isValid = false;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError(getString(R.string.err_short_password));
            isValid = false;
        }

        if (!isValid) return;

        setLoading(true);

        FirebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Authentication failed.";
                        Toast.makeText(LoginActivity.this, "Login Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }
}
