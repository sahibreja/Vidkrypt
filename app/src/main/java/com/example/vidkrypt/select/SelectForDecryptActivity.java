package com.example.vidkrypt.select;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vidkrypt.Adapter.Method;
import com.example.vidkrypt.Adapter.StorageUtil;
import com.example.vidkrypt.MainActivity;
import com.example.vidkrypt.R;
import com.example.vidkrypt.select.decryptFile.DecryptFileActivity;

import java.io.File;

public class SelectForDecryptActivity extends AppCompatActivity {
    ImageView backBtn;
    CardView decrypted_file,decBtn;
    private File storage;
    private String[] storagePaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_for_decrypt);
        init();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SelectForDecryptActivity.this, MainActivity.class));
                finish();
            }
        });
        decrypted_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                storagePaths = new String[]{"/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt"};
//                for (String path : storagePaths) {
//                    storage = new File(path);
//                    Method.load_Directory_Files(storage);
//                }

                Intent intent = new Intent(SelectForDecryptActivity.this, DecryptedFileActivity.class);
                startActivity(intent);
            }
        });
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(SelectForDecryptActivity.this, DecryptFileActivity.class);
               startActivity(intent);
            }
        });
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        decrypted_file=findViewById(R.id.decrypted_file);
        decBtn=findViewById(R.id.decryptBtn);
        createFolder("VidKrypt","Decrypted");

    }
    private void createFolder(String dirName,String subdir) {
        //File file =new File(getExternalFilesDir(null)+"/"+"VidKrypt");
        File file;
        if (subdir != null) {
            file = new File(getExternalFilesDir(null) + "/" + dirName + "/" + subdir);
        } else {
            file = new File(getExternalFilesDir(null) + "/" + dirName);
        }
        if (!file.exists()) {
            file.mkdir();
            if (file.isDirectory()) {
                Toast.makeText(this, "Folder Created Success", Toast.LENGTH_SHORT).show();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectForDecryptActivity.this);
                String sMessage = "Message: failed to create" + "\nPath " + Environment.getExternalStorageDirectory() +
                        "\nmkdirs" + file.mkdirs();
                builder.setMessage(sMessage);
                builder.show();

            }
        }
    }
}