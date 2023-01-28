package com.example.vidkrypt.select.send;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vidkrypt.R;
import com.example.vidkrypt.select.model.Users;
import com.example.vidkrypt.select.send.Adapter.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowUserActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private  FirebaseUser firebaseUser;
    private ArrayList<Users> users;
    private UsersAdapter usersAdapter;
    private RecyclerView chatRecyclerView;
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        init();
        buttonClick();
        chatRecyclerView.setAdapter(usersAdapter);
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String cUser=snapshot1.child("userId").getValue().toString();
                    Users user = snapshot1.getValue(Users.class);
                    if(!cUser.equals(firebaseUser.getUid()))
                    {
                        users.add(user);
                    }


                }
                usersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void buttonClick()
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
        chatRecyclerView=findViewById(R.id.recyclerView);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        users =new ArrayList<>();
        usersAdapter = new UsersAdapter(this,users);
        backBtn =findViewById(R.id.backBtn);
    }
}