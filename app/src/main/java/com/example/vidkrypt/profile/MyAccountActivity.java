package com.example.vidkrypt.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vidkrypt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;
    private ImageView backBtn;
    private TextView email,name;
    String username,userEmail="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        init();
        ButtonClick();
    }
    private void ButtonClick()
    {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        email=findViewById(R.id.userEmail);
        name=findViewById(R.id.userName);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        name.setText("");
        email.setText("");
        database.getReference().child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               username=snapshot.child("userName").getValue().toString();
               userEmail=snapshot.child("userEmail").getValue().toString();
                name.setText(username);
                email.setText(userEmail);            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}