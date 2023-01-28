package com.example.vidkrypt.select;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vidkrypt.Algorithm.AES;
import com.example.vidkrypt.MainActivity;
import com.example.vidkrypt.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SelectForEncryptActivity extends AppCompatActivity {
    ImageView backBtn;
    CardView selVidBtn,selImgBtn,selAudBtn;
    Uri selectedFilePath;
    String filePath;
    File dir;
    String dirr;
    String m_Text;
    private static final int VIDEO_REQUEST = 101;
    private static final int AUDIO_REQUEST = 201;
    private static final int IMAGE_REQUEST = 301;
    private final int STORAGE_PERMISSION_CODE = 1;
    private final static int IV_LENGTH = 16; // Default length with Default 128
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_for_encrypt);
        init();
        if (ContextCompat.checkSelfPermission(SelectForEncryptActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(SelectForEncryptActivity.this, "You have already granted this permission!",
                    //Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }

        selectButton();

    }
    private void selectButton()
    {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(SelectForEncryptActivity.this, MainActivity.class));
                finish();
            }
        });
        selVidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,VIDEO_REQUEST);
            }
        });
        selImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMAGE_REQUEST);
//                Toast.makeText(SelectForEncryptActivity.this, selectedFilePath.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        selAudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,AUDIO_REQUEST);
//
//                Toast.makeText(SelectForEncryptActivity.this, selectedFilePath.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of Encryption And Decryption")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SelectForEncryptActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == VIDEO_REQUEST)
        {
            if(data==null)
            {
                Toast.makeText(this, "Please Select a video to encrypt", Toast.LENGTH_SHORT).show();
            }else
            {
                Uri targetUri = data.getData();
                selectedFilePath=targetUri;
                selectPassDialog(".mp4");
            }


        }else if(requestCode==IMAGE_REQUEST)
       {
           if(data==null)
           {
               Toast.makeText(this, "Please Select a Image to encrypt", Toast.LENGTH_SHORT).show();
           }else
           {
               Uri targetUri = data.getData();
               selectedFilePath=targetUri;
               selectPassDialog(".jpg");
           }
       }else if(requestCode==AUDIO_REQUEST)
       {
           if(data==null)
           {
               Toast.makeText(this, "Please Select a Audio to encrypt", Toast.LENGTH_SHORT).show();
           }else
           {
               Uri targetUri = data.getData();
               selectedFilePath=targetUri;
               selectPassDialog(".mp3");
           }
       }else
       {
           if(data==null)
           {
               Toast.makeText(this, "Please Select a File to encrypt", Toast.LENGTH_SHORT).show();
           }else
           {
               Uri targetUri = data.getData();
               selectedFilePath=targetUri;
               selectPassDialog("null");
           }
       }
    }
    public void selectPassDialog(String filetype)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectForEncryptActivity.this);
        builder.setTitle("Password..");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity

        @SuppressLint("ResourceType") RelativeLayout mainLayout = (RelativeLayout) findViewById(R.layout.activity_select_for_encrypt);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_password,mainLayout, false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setMessage("For Encryption you have to give Password").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                filePath=getRealPathFromUri(SelectForEncryptActivity.this,selectedFilePath);
                File inFile =new File(filePath);
                String encName=inFile.getName().substring(0,inFile.getName().toString().length()-1-3)+"encrypted"+filetype;
                String decName=inFile.getName().substring(0,inFile.getName().toString().length()-1-3)+"decrypted"+filetype;
                File outFile = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Encrypted"+"/"+encName);
                //File outFile_dec = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt"+"/"+decName);
                try {
                byte[] keyyData= m_Text.getBytes("UTF-16LE");
                MessageDigest md = MessageDigest.getInstance("md5");
                byte[] digestOfPassword = md.digest(keyyData);
                SecretKeySpec skeySpec = new SecretKeySpec(digestOfPassword, "AES");
                    //SecretKey key = KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey();
                    //byte[] keyData = key.getEncoded();
                    SecretKey key2 = new SecretKeySpec(digestOfPassword, 0, digestOfPassword.length, ALGO_SECRET_KEY_GENERATOR); //if you want to store key bytes to db so its just how to //recreate back key from bytes array
                    byte[] iv = new byte[IV_LENGTH];
                    SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR).nextBytes(iv); // If
                    //storing
                    //separately
                    AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
                    AES.encrypt(skeySpec,new FileInputStream(inFile), new FileOutputStream(outFile));
                    Toast.makeText(SelectForEncryptActivity.this, "Encryption Success", Toast.LENGTH_SHORT).show();
                    //AES.decrypt(key2, new FileInputStream(outFile), new FileOutputStream(outFile_dec));
                } catch (Exception e) {
                    Toast.makeText(SelectForEncryptActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        selVidBtn=findViewById(R.id.selVideo);
        selImgBtn=findViewById(R.id.selIMage);
        selAudBtn=findViewById(R.id.selAudio);
        createFolder("VidKrypt","Encrypted");
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectForEncryptActivity.this);
                String sMessage = "Message: failed to create" + "\nPath " + Environment.getExternalStorageDirectory() +
                        "\nmkdirs" + file.mkdirs();
                builder.setMessage(sMessage);
                builder.show();

            }
        }
    }
}