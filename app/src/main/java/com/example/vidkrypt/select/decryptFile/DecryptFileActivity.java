package com.example.vidkrypt.select.decryptFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vidkrypt.R;

public class DecryptFileActivity extends AppCompatActivity {
    ImageView backBtn;
    CardView selVidBtn,selImgBtn,selAudBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_file);
        init();
        ButtonClicked();
    }
    private void ButtonClicked()
    {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selVidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DecryptFileActivity.this,VideoDecryptActivity.class);
                startActivity(intent);
            }
        });
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        selVidBtn=findViewById(R.id.selVideo);
        selImgBtn=findViewById(R.id.selIMage);
        selAudBtn=findViewById(R.id.selAudio);
    }
}