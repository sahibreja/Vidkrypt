package com.example.vidkrypt.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vidkrypt.MainActivity;
import com.example.vidkrypt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    private EditText email,pass;
    private TextView newUser;
    private Button loginBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog,progressForgot;
    private AlertDialog.Builder encDialogBuilder;
    private AlertDialog encDialog;
    private TextView forgotBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        sharedpreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = pass.getText().toString();
                String uEmail=sharedpreferences.getString("uEmail","");
                String uPass=sharedpreferences.getString("uPass","");
                if(userEmail.isEmpty())
                {
                    email.setError("Please Enter Your Email");
                }else if(userPassword.isEmpty())
                {
                    pass.setError("Please Enter Your Password");
                }else
                {

                    progressDialog.show();
                    auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                //encDialog.show();
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {

                                        //encDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
//                                    }
//                                },3000);

                            }else
                            {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt=email.getText().toString().trim();
                if(emailTxt.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Enter Your register email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressForgot.show();
                auth.sendPasswordResetEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressForgot.dismiss();
                    }
                });

            }
        });

    }
    private void initialize()
    {
        email =findViewById(R.id.emailTxt);
        pass =findViewById(R.id.passTxt);
        newUser =findViewById(R.id.newUser);
        loginBtn =findViewById(R.id.loginBtn);
        forgotBtn=findViewById(R.id.forgot);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressForgot=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");
        progressForgot.setTitle("Forgot password");
        progressForgot.setMessage("Sending link to your email to reset your password");
        encDialogBuilder=new AlertDialog.Builder(LoginActivity.this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.welcome,null);
        encDialogBuilder.setView(contactPopupView);
        encDialogBuilder.setCancelable(false);
        encDialog=encDialogBuilder.create();
    }

}