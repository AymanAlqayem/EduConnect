package com.tarificompany.android_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUserName;
    private EditText edtPass;
    private Button btnLogin;
    private CheckBox loginCheckBox;

    private RequestQueue queue;
    private String url = "http://10.0.2.2/AndroidProject/handleLogin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpViews();
        loadSavedCredentials();
    }

    private void setUpViews() {
        edtUserName = findViewById(R.id.edtUserName);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        loginCheckBox = findViewById(R.id.loginCheckBox);

        queue = Volley.newRequestQueue(LoginActivity.this);
    }

    private void loadSavedCredentials() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", "");
        String savedPassword = prefs.getString("password", "");
        boolean isRemembered = prefs.getBoolean("rememberMe", false);

        if (isRemembered) {
            edtUserName.setText(savedUsername);
            edtPass.setText(savedPassword);
            loginCheckBox.setChecked(true);
        }
    }

    public void handleLogin(View view) {
        String username = edtUserName.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");

                        if (success) {
                            String role = json.getString("role");

                            if (loginCheckBox.isChecked()) {
                                SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                prefs.edit()
                                        .putString("username", username)
                                        .putString("password", password)
                                        .putBoolean("rememberMe", true)
                                        .apply();
                            } else {
                                SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                prefs.edit()
                                        .clear()
                                        .apply();
                            }

                            switch (role.toLowerCase()) {
                                case "teacher":
                                    String teacherId = json.getString("teacher_id");

                                    SharedPreferences prefs = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
                                    prefs.edit()
                                            .putString("teacher_id", teacherId)
                                            .apply();

                                    Log.d("LoginActivity", "Saved teacher ID: " + teacherId);

                                    startActivity(new Intent(this, TeacherActivity.class));
                                    break;

                                case "register":
                                    startActivity(new Intent(this, RegisterActivity.class));
                                    break;

                                case "student":
                                    startActivity(new Intent(this, StudentActivity.class));
                                    break;

                                default:
                                    Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        queue.add(request);
    }
}
