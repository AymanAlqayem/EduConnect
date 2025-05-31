package com.tarificompany.android_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // SharedPreferences keys
    private static final String USER_TYPE = "USER_TYPE";
    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String REMEMBER_ME = "REMEMBER_ME";

    private EditText edtEmailLogin;
    private EditText edtPassLogin;
    private Button btnLogin;
    private Button btnRegister;
    private CheckBox loginCheckBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RequestQueue requestQueue;

    // Replace with your server URL
    private static final String LOGIN_URL = "http://192.168.0.111/rest/info_json.php?cat=" + ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        setUpViews();
        setUpSharedPref();
        checkPrefs();
    }

    private void setUpViews() {
        edtEmailLogin = findViewById(R.id.edtUserNameLogin);
        edtPassLogin = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        loginCheckBox = findViewById(R.id.loginCheckBox);

        btnLogin.setOnClickListener(this::btnLoginOnClick);
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setUpSharedPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
    }

    private void checkPrefs() {
        boolean rememberMe = pref.getBoolean(REMEMBER_ME, false);
        if (rememberMe) {
            String storedEmail = pref.getString(USER_EMAIL, "");
            edtEmailLogin.setText(storedEmail);
            loginCheckBox.setChecked(true);
        }
    }

    public void btnLoginOnClick(View view) {
        String email = edtEmailLogin.getText().toString().trim();
        String password = edtPassLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        // Create JSON object for POST request
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                String userType = response.getString("user_type");
                                int userId = response.getInt("user_id");
                                String userName = response.getString("name");

                                handleSuccessfulLogin(email, userId, userName, userType);
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }

    private void handleSuccessfulLogin(String email, int userId, String userName, String userType) {
        // Save to SharedPreferences if "Remember me" is checked
        if (loginCheckBox.isChecked()) {
            editor.putString(USER_EMAIL, email);
            editor.putBoolean(REMEMBER_ME, true);
        } else {
            editor.remove(USER_EMAIL);
            editor.putBoolean(REMEMBER_ME, false);
        }

        // Save current user info
        editor.putString(USER_TYPE, userType);
        editor.putInt(USER_ID, userId);
        editor.putString(USER_NAME, userName);
        editor.apply();

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

        // Redirect to appropriate activity
        Intent intent;
        if (userType.equals("teacher")) {
            intent = new Intent(this, TeacherHomeActivity.class);
        } else {
            intent = new Intent(this, StudentHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}