package com.example.navigationbottom.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigationbottom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText edtTaiKhoan, edtMauKhau;
    private TextView tvSignUp;
    private Button btnLogin;
    private TextInputLayout edtLayoutTaikhoan, edtLayoutMatkhau;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");

        initView();
    }

    private void initView() {
        edtTaiKhoan = findViewById(R.id.edt_taiKhoan);
        edtMauKhau = findViewById(R.id.edt_matkhau);
        edtLayoutTaikhoan = findViewById(R.id.edtlayout_taikhoan);
        edtLayoutMatkhau = findViewById(R.id.edtlayout_matkhau);
        tvSignUp = findViewById(R.id.tv_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtTaiKhoan.getText().toString().trim();
                String pass = edtMauKhau.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtTaiKhoan.setError("Invalid Email");
                    edtTaiKhoan.setFocusable(true);
                }
                else if(pass.length()<6){
                    edtMauKhau.setError("Pass length at least 6 characters");
                    edtMauKhau.setFocusable(true);
                }
                else{
                    loginUser(email, pass);
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });



    }

    private void loginUser(String email, String pass) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}