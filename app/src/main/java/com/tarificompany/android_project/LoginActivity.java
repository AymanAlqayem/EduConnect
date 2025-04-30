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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    public static final String NAME = "NAME";
    public static final String PASS = "PASS";
    public static final String FLAG = "FLAG";

    private EditText edtUserNameLogin;
    private EditText edtPassLogin;
    private Button btnLogin;
    private CheckBox loginCheckBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpViews();
        setUpSharedPref();
        checkPrefs(); // Check if saved login data exists
    }

    /**
     * setUpViews method that will initialise the hooks, and create the animation.
     */
    public void setUpViews() {
        edtUserNameLogin = findViewById(R.id.edtUserNameLogin);
        edtPassLogin = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        loginCheckBox = findViewById(R.id.loginCheckBox);
    }

    /**
     * setUpSharedPref method that will initialise the shared preferences objects.
     */
    public void setUpSharedPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
    }

    /**
     * checkPrefs method that will Check if saved login info exists and pre-fill it.
     */
    public void checkPrefs() {
        boolean flag = pref.getBoolean(FLAG, false);
        if (flag) {
            String storedName = pref.getString(NAME, "");
            String storedPass = pref.getString(PASS, "");

            edtUserNameLogin.setText(storedName);
            edtPassLogin.setText(storedPass);
            loginCheckBox.setChecked(true);
        }
    }

    /**
     * btnLoginOnClick method that will make actions for the login button.
     */

    public void btnLoginOnClick(View view) {
//        String name = edtUserNameLogin.getText().toString();
//        String pass = edtPassLogin.getText().toString();
//
//        String storedName = pref.getString(NAME, "");
//        String storedPass = pref.getString(PASS, "");
//
//        if (name.equals(storedName) && pass.equals(storedPass)) {
//            if (loginCheckBox.isChecked()) {
//                editor.putString(NAME, name);
//                editor.putString(PASS, pass);
//                editor.putBoolean(FLAG, true);
//                editor.commit();
//            }
//            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
//        }
//    }

    }

}