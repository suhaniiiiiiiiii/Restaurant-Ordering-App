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
import com.google.firebase.auth.FirebaseUser;
import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.models.User;
import com.restaurant.orderingapp.utils.FirebaseHelper;
import com.restaurant.orderingapp.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPhone, tilPassword;
    private TextInputEditText etName, etEmail, etPhone, etPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.tilRegName);
        tilEmail = findViewById(R.id.tilRegEmail);
        tilPhone = findViewById(R.id.tilRegPhone);
        tilPassword = findViewById(R.id.tilRegPassword);

        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPhone = findViewById(R.id.etRegPhone);
        etPassword = findViewById(R.id.etRegPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.regProgressBar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (!ValidationUtils.isValidName(name)) {
            tilName.setError(getString(R.string.err_empty_name));
            isValid = false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            isValid = false;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            tilPhone.setError(getString(R.string.err_invalid_phone));
            isValid = false;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError(getString(R.string.err_short_password));
            isValid = false;
        }

        if (!isValid) return;

        setLoading(true);

        FirebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fbUser = FirebaseHelper.getCurrentUser();
                        if (fbUser != null) {
                            User newUser = new User(fbUser.getUid(), name, email, phone, "");
                            FirebaseHelper.getUsersRef().child(fbUser.getUid()).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        setLoading(false);
                                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    });
                        }
                    } else {
                        setLoading(false);
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed.";
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
        etName.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPhone.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }
}
