package com.example.vidkrypt.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.vidkrypt.select.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    private EditText name,email,pass,confirmPass;
    private TextView alreadyAccount;
    private Button signup;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder encDialogBuilder;
    private AlertDialog encDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedpreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int login=sharedpreferences.getInt("login",0);//0
        if(login==1)
        {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
        init();
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finishAffinity();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  userName = name.getText().toString();
                String  userEmail = email.getText().toString();
                String  userPass = pass.getText().toString();
                String  userConfirmPass = confirmPass.getText().toString();

                if(userName.isEmpty())
                {
                    name.setError("Please Enter Your Name");

                }else if(userEmail.isEmpty())
                {
                    email.setError("Please Enter Your Email");
                }else if(userPass.isEmpty())
                {
                    pass.setError("Please Enter Your Password");

                }else
                {
                    if(userPass.equals(userConfirmPass))
                    {
                        progressDialog.show();

                        auth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    encDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String uid=task.getResult().getUser().getUid().toString();
                                            Users users=new Users(uid,userName,userEmail,userPass,"");
                                            database.getReference().child("Users").child(uid).setValue(users);
                                            encDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            finishAffinity();
                                        }
                                    },3000);

                                }else
                                {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    //pass.setError("Password format"+"Aaa12#");
                                }
                            }
                        });
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("uName",userName);
//                        editor.putString("uEmail",userEmail);
//                        editor.putString("uPass",userPass);
//                        editor.putInt("login",1);
//                        editor.commit();
//                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
//                        finish();

                    }else{

                        confirmPass.setError("Password Doesn't Match");
                    }
                }
            }
        });


    }
    private void init()
    {
        name =findViewById(R.id.nameTxt);
        email =findViewById(R.id.emailTxt);
        pass =findViewById(R.id.passTxt);
        confirmPass =findViewById(R.id.confirmPass);
        alreadyAccount =findViewById(R.id.alreadyAct);
        signup =findViewById(R.id.registerBtn);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("we are creating your account");
        encDialogBuilder=new AlertDialog.Builder(SignUpActivity.this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.show_success,null);
        encDialogBuilder.setView(contactPopupView);
        encDialogBuilder.setCancelable(false);
        encDialog=encDialogBuilder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            //reload();
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }
}