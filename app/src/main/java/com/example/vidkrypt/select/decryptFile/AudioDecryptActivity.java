package com.example.vidkrypt.select.decryptFile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vidkrypt.Adapter.AudioViewAdapter;
import com.example.vidkrypt.Adapter.Constant;
import com.example.vidkrypt.Adapter.Method;
import com.example.vidkrypt.Algorithm.AES;
import com.example.vidkrypt.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AudioDecryptActivity extends AppCompatActivity {

    ImageView backBtn;
    RecyclerView recyclerView;
    Button selectVideoBtn;
    Uri selectedFilePath;
    String filePath;
    String m_Text;
    private static final int AUDIO_REQUEST = 101;
    private AudioViewAdapter recyclerViewAdapter;
    private boolean permission;
    private File storage;
    private String[] storagePaths;
    private AlertDialog.Builder showDecAnim;
    private AlertDialog animDecDialog;
    private final static int IV_LENGTH = 16; // Default length with Default 128
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    @SuppressLint("NotifyDataSetChanged")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_decrypt);
        init();
        buttonClick();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerViewAdapter = new AudioViewAdapter(this,"encrypted");
        recyclerView.setAdapter(recyclerViewAdapter);
        storagePaths = new String[]{"/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Encrypted"};

        for (String path : storagePaths) {
            Constant.allAudioList.clear();
            storage = new File(path);
            Method.load_audio_files(storage);
        }

        recyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AUDIO_REQUEST)
        {
            if(data==null)
            {
                Toast.makeText(this, "Please Select Encrypted form Of Audio File", Toast.LENGTH_SHORT).show();
            }else
            {
                Uri targetUri = data.getData();
                String []file=fileNameAndType(targetUri);
                decryption(targetUri,file[0],file[1]);

            }
        }



    }
    private void decryption(Uri fileUri,String filetype,String fileName)
    {
        showDecAnim=new AlertDialog.Builder(AudioDecryptActivity.this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.dec_anim,null);
        showDecAnim.setView(contactPopupView);
        showDecAnim.setCancelable(false);
        animDecDialog=showDecAnim.create();
        androidx.appcompat.app.AlertDialog.Builder builderPass = new androidx.appcompat.app.AlertDialog.Builder(AudioDecryptActivity.this);
        builderPass.setTitle("Password..");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity

        @SuppressLint("ResourceType") RelativeLayout mainLayout = (RelativeLayout) findViewById(R.layout.activity_select_for_encrypt);
        View viewInflated = LayoutInflater.from(AudioDecryptActivity.this).inflate(R.layout.text_input_password,mainLayout, false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builderPass.setView(viewInflated);

// Set up the buttons
        builderPass.setMessage("For Decryption you have to give Password").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                //animation
                animDecDialog.show();
                String path =getRealPathFromUri(AudioDecryptActivity.this,fileUri);
                File inFile =new File(path);
                String encName=fileName+filetype;
                String decName=fileName+filetype;
                File outFile = new File(path);
                File outFile_dec = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Decrypted"+"/"+decName);
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
                    //AES.encrypt(skeySpec,new FileInputStream(inFile), new FileOutputStream(outFile));
                    AES.decrypt(key2, new FileInputStream(outFile), new FileOutputStream(outFile_dec));
                    long encTime=AES.getEncTime();
                    encTime=encTime/1000000;
                    long time;
                    if(encTime<2000)
                    {
                        time=2000;
                    }else
                    {
                        time=encTime;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animDecDialog.dismiss();
                            Toast.makeText(AudioDecryptActivity.this, "Decryption Success", Toast.LENGTH_SHORT).show();
                        }
                    },time);

                    //
                } catch (Exception e) {
                    Toast.makeText(AudioDecryptActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    animDecDialog.dismiss();
                }
            }
        });
        builderPass.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builderPass.show();
    }
    private String[] fileNameAndType(Uri uri)
    {
        String type=getRealPathFromUri(AudioDecryptActivity.this,uri);
        File inFile =new File(type);
        String filetype="";
        String fileName="";
        for(int i=inFile.getName().length()-1;i>=0;i--)
        {
            char c='.';
            if(inFile.getName().charAt(i)==c)
            {
                filetype=inFile.getName().substring(i);
                fileName=inFile.getName().substring(0,i);
                break;
            }else
            {
                continue;
            }
        }
        return new String[]{filetype,fileName};
    }
    private void selectPassDialog(String fileType)
    {
        Toast.makeText(this, fileType, Toast.LENGTH_SHORT).show();
    }
    private String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void buttonClick()
    {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent,AUDIO_REQUEST);
            }
        });
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        recyclerView=findViewById(R.id.recyclerView);
        selectVideoBtn=findViewById(R.id.selectVideoBtn);
    }
}