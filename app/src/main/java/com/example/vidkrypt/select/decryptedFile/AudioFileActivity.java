package com.example.vidkrypt.select.decryptedFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vidkrypt.Adapter.AudioViewAdapter;
import com.example.vidkrypt.Adapter.Constant;
import com.example.vidkrypt.Adapter.ImageViewAdapter;
import com.example.vidkrypt.Adapter.Method;
import com.example.vidkrypt.R;

import java.io.File;

public class AudioFileActivity extends AppCompatActivity {
    ImageView backBtn;
    RecyclerView recyclerView;
    private AudioViewAdapter recyclerViewAdapter;
    private boolean permission;
    private File storage;
    private String[] storagePaths;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_file);
        init();
        buttonClick();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //if you face lack in scrolling then add following lines
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerViewAdapter = new AudioViewAdapter(this,"decrypted");
        recyclerView.setAdapter(recyclerViewAdapter);

        storagePaths = new String[]{"/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Decrypted"};

        for (String path : storagePaths) {
            Constant.allAudioList.clear();
            storage = new File(path);
            Method.load_audio_files(storage);
        }

        recyclerViewAdapter.notifyDataSetChanged();
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
        backBtn=findViewById(R.id.backBtn);
        recyclerView=findViewById(R.id.recyclerView);
    }
}