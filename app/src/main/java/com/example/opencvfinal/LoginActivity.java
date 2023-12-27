package com.example.opencvfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opencvfinal.auth.AuthRequest;
import com.example.opencvfinal.auth.JWTToken;
import com.example.opencvfinal.dao.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static AuthService authService;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isUserAuthenticated()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);
            initializeLoginActivity();
        }
    }

    private void initializeLoginActivity() {
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Check authentication (replace with your authentication logic)
            authenticateUser(username, password);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://128.10.4.35:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authService = retrofit.create(AuthService.class);
    }

    private boolean isUserAuthenticated() {
        // Check if the user is authenticated by verifying the presence of a valid token
        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);
        return authToken != null;
    }

    private void authenticateUser(String username, String password) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);

        Call<JWTToken> call = getAuthService().authenticate(authRequest);
        call.enqueue(new Callback<JWTToken>() {
            @Override
            public void onResponse(Call<JWTToken> call, Response<JWTToken> response) {
                if (response.isSuccessful()) {
                    JWTToken jwtToken = response.body();
                    if (jwtToken != null) {
                        String authToken = jwtToken.getId_token();
                        if (authToken != null) {
                            // Extract Authorization header from the response
                            String authorizationHeader = response.headers().get("Authorization");

                            // Do something with the Authorization header
                            Log.d("Authorization Header:", authorizationHeader);

                            Toast.makeText(LoginActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                            Log.d("le token : ", authToken);
                            saveAuthTokenLocally(authToken);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish(); // Finish the authentication activity
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed: Null token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: Null JWTToken", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JWTToken> call, Throwable t) {
                // Handle network or unexpected errors
                Toast.makeText(LoginActivity.this, "Authentication error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAuthTokenLocally(String authToken) {
        // Save the authentication token locally using SharedPreferences or another storage method
        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("authToken", authToken);
        editor.apply();
    }
    public static AuthService getAuthService() {
        return authService;
    }
}
