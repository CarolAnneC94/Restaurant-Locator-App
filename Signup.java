package com.trees.locationrestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;

public class Signup extends AppCompatActivity {
    EditText username, email, contact, password, confirmpassword;
    Button registerUser;
    SpotsDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.EditText_Username);
        email = findViewById(R.id.EditText_Email);
        contact = findViewById(R.id.EditText_ContactNumber);
        password = findViewById(R.id.EditText_Password);
        confirmpassword = findViewById(R.id.EditText_ConfirmPassword);
        registerUser = findViewById(R.id.registerUser);
        progressDialog = new SpotsDialog(Signup.this, R.style.Custom);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();

            }
        });
    }
    public void checkFields(){
        String usernameGet = username.getText().toString();
        String emailGet = email.getText().toString();
        String contactGet = contact.getText().toString();
        String passwordGet = password.getText().toString();
        String confirmpasswordGet = confirmpassword.getText().toString();

        if(TextUtils.isEmpty(usernameGet)){
            Toast.makeText(getApplicationContext(), "Please enter username",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(emailGet)){
            Toast.makeText(getApplicationContext(), "Please enter email",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(contactGet)){
            Toast.makeText(getApplicationContext(), "Please enter contact number",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passwordGet)){
            Toast.makeText(getApplicationContext(), "Please enter password",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(confirmpasswordGet)){
            Toast.makeText(getApplicationContext(), "Please enter confirm password",Toast.LENGTH_LONG).show();
        }
        else if(!passwordGet.equals(confirmpasswordGet)){
            Toast.makeText(getApplicationContext(), "Password doesn't match",Toast.LENGTH_LONG).show();
        }
        else{
            registerNewUser(emailGet, usernameGet, contactGet,passwordGet);
        }
        }

    private void registerNewUser(String usernameGet, String contactGet, String emailGet, String passwordGet) {
        progressDialog.show();
        DatabaseHelper databaseHelper = new DatabaseHelper(Signup.this);
        Long checkInsert = databaseHelper.insertData(usernameGet, contactGet, emailGet, passwordGet);
        if (checkInsert != -1) {
            Toast.makeText(getApplicationContext(), "User registered successfully!",Toast.LENGTH_LONG).show();
            username.setText("");
            email.setText("");
            contact.setText("");
            password.setText("");
            confirmpassword.setText("");
            progressDialog.cancel();
        }
    }
}
