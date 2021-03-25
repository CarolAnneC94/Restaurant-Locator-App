package com.trees.locationrestaurants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity {
    Button login;
    EditText userName, userPassword;
    TextView Signup, forgotPassword;
    SpotsDialog progressDialog;
    SharedPreferences sharedpreferences;
    static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        progressDialog = new SpotsDialog(Login.this, R.style.Custom);
        login = findViewById(R.id.login);
        userName = findViewById(R.id.EditText_Username);
        Signup = findViewById(R.id.btnSignup);
        userPassword = findViewById(R.id.EditText_Password);
        forgotPassword = findViewById(R.id.btnForget);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });
        if (!CheckPermissions()) {
            SpecificPermission();
        }
    }

    public void checkFields(){
        String userNameString = userName.getText().toString();
        String passwordString = userPassword.getText().toString();
        if(TextUtils.isEmpty(userNameString)){
            Toast.makeText(getApplicationContext(), "Empty username or password",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passwordString)){
            Toast.makeText(getApplicationContext(), "Empty username or password",Toast.LENGTH_LONG).show();
        }

        else{
            progressDialog.show();
            getLoginInfo();
        }
    }

    private void getLoginInfo() {
        Cursor cursors = new DatabaseHelper(Login.this).getData(userName.getText().toString(), userPassword.getText().toString());
        cursors.moveToNext();

        try {
            if (cursors.getString(1) != null) {
                Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this, MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Username or password incorrect!", Toast.LENGTH_LONG).show();

            }
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Username or password incorrect!", Toast.LENGTH_LONG).show();
        }
        progressDialog.cancel();
    }

    private boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void SpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }
}
