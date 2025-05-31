package com.tarificompany.android_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

    // UI Elements
    private EditText edtUserNameLogin;
    private EditText edtPassLogin;
    private Button btnLogin;
    private CheckBox loginCheckBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RequestQueue requestQueue;

    private static final String LOGIN_URL = "http://10.0.2.2/edu/login_api.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupSharedPreferences();
        checkRememberMeStatus();
    }

    private void initViews() {
        edtUserNameLogin = findViewById(R.id.edtUserNameLogin);
        edtPassLogin = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        loginCheckBox = findViewById(R.id.loginCheckBox);

        btnLogin.setOnClickListener(this::handleLoginClick);
    }

    private void setupSharedPreferences() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void checkRememberMeStatus() {
        if (pref.getBoolean(REMEMBER_ME, false)) {
            edtUserNameLogin.setText(pref.getString(USER_EMAIL, ""));
            loginCheckBox.setChecked(true);
        }
    }

    private void handleLoginClick(View view) {
        String username = edtUserNameLogin.getText().toString().trim();
        String password = edtPassLogin.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Please enter both username and password");
            return;
        }

        authenticateUser(username, password);
    }

    private void authenticateUser(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                new JSONObject(params),
                this::handleLoginResponse,
                this::handleLoginError
        );

        requestQueue.add(request);
    }

    private void handleLoginResponse(JSONObject response) {
        try {
            if (response.getBoolean("success")) {
                String userType = response.getString("user_type");
                int userId = response.getInt("user_id");
                String userName = response.getString("name");
                String userEmail = response.getString("email");

                saveUserData(userEmail, userId, userName, userType);
                redirectUser(userType);
            } else {
                showToast(response.getString("message"));
            }
        } catch (JSONException e) {
            showToast("Error processing login response");
            e.printStackTrace();
        }
    }

    private void handleLoginError(VolleyError error) {
        String errorMessage = "Login failed. Please try again.";
        if (error.networkResponse != null) {
            switch (error.networkResponse.statusCode) {
                case 400: errorMessage = "Bad request"; break;
                case 401: errorMessage = "Invalid credentials"; break;
                case 500: errorMessage = "Server error"; break;
            }
        }
        showToast(errorMessage);
    }

    private void saveUserData(String email, int userId, String userName, String userType) {
        editor.putBoolean(REMEMBER_ME, loginCheckBox.isChecked());
        if (loginCheckBox.isChecked()) {
            editor.putString(USER_EMAIL, email);
        } else {
            editor.remove(USER_EMAIL);
        }

        editor.putString(USER_TYPE, userType);
        editor.putInt(USER_ID, userId);
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    private void redirectUser(String userType) {
        showToast("Login successful");

        Intent intent;
        switch (userType.toLowerCase()) {
            case "teacher":
                intent = new Intent(this, TeacherDashboardActivity.class);
                break;
            case "student":
                intent = new Intent(this, StudentDashboardActivity.class);
                break;
            case "registrar":
            default:
                intent = new Intent(this, RegisterActivity.class);
        }

        intent.putExtra("USER_TYPE", userType);
        intent.putExtra("USER_ID", pref.getInt(USER_ID, 0));
        intent.putExtra("USER_NAME", pref.getString(USER_NAME, ""));

        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}