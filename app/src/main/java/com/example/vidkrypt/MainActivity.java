package com.example.vidkrypt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.vidkrypt.Algorithm.AES;
import com.example.vidkrypt.Algorithm.EncryptionAndDecryption;
import com.example.vidkrypt.login.LoginActivity;
import com.example.vidkrypt.login.SignUpActivity;
import com.example.vidkrypt.profile.HelpActivity;
import com.example.vidkrypt.profile.MyAccountActivity;
import com.example.vidkrypt.select.DecryptedFileActivity;
import com.example.vidkrypt.select.decryptFile.AudioDecryptActivity;
import com.example.vidkrypt.select.decryptFile.ImageDecryptActivity;
import com.example.vidkrypt.select.decryptFile.VideoDecryptActivity;
import com.example.vidkrypt.select.decryptedFile.AudioFileActivity;
import com.example.vidkrypt.select.decryptedFile.ImageFileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private TextView openBtn;
    //LinearLayout selectEncrypt,selectDecrypt;
    private Button selectEncrypt,selectDecrypt,decrypted_file;
    private AlertDialog.Builder encDialogBuilder,decDialogBuilder,showEncAnim,showDecAnim;
    private AlertDialog encDialog,decDialog,animEncDialog,animDecDialog;
    Uri selectedFilePath;
    String filePath;
    String m_Text;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    String userName;
    private Switch auto_key;
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
        setContentView(R.layout.activity_main);
        init();
        fromAnotherApp();
        createFolder("VidKrypt","Decrypted");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                switch(item.getItemId())
                {

                    case R.id.myAccount:
                        startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
                        break;
                    case R.id.keygen:
                        if(drawerLayout.isDrawerOpen(GravityCompat.START))
                        {
                            if(auto_key.isChecked())
                            {
                                auto_key.setChecked(false);
                            }else
                            {
                                auto_key.setChecked(true);
                            }
                            drawerLayout.openDrawer(GravityCompat.START);
                        }

                        break;
                    case R.id.help:
                             startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        break;
                    case R.id.delAccount:
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Delete Account").setMessage("Are you sure You want to delete your account").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                                bottomSheetDialog.setContentView(R.layout.delete_confirm_popup);

                                EditText email=bottomSheetDialog.findViewById(R.id.emailTxt);
                                EditText pass=bottomSheetDialog.findViewById(R.id.emailTxt);

                                Button ok_btn=bottomSheetDialog.findViewById(R.id.ok_btn);
                                Button cancel_btn=bottomSheetDialog.findViewById(R.id.cancel_btn);
                                bottomSheetDialog.show();
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String emailTxt=email.getText().toString();
                                        String passTxt=pass.getText().toString();

                                        if(emailTxt.isEmpty() || passTxt.isEmpty())
                                        {
                                            if(emailTxt.isEmpty())
                                            {
                                                email.setError("Please Enter your email");
                                            }else
                                            {
                                                pass.setError("Please Enter your password");
                                            }

                                        }else
                                        {
                                            deleteuser(emailTxt,passTxt);
                                            bottomSheetDialog.dismiss();
                                        }
                                    }
                                });
                                cancel_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomSheetDialog.dismiss();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create();
                        builder.show();
                        break;

                    case R.id.logout:
                        auth.getCurrentUser();
                        auth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });
        selectEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEncPopup();
            }
        });
        selectDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDecPopup("");
            }
        });
        decrypted_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, DecryptedFileActivity.class));
               showDecFilePopup();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = (TextView) headerView.findViewById(R.id.userName);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userName=snapshot.child("userName").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                navUsername.setText(userName);

            }
        });





    }
    public String GetPassword(int length){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();

        for(int i = 0; i < length; i++){
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }
    private void fromAnotherApp() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("video/*".equals(type)) {
                handleSendVideo(intent); // Handle text being sent
            } else if ("image/*".equals(type)) {
                handleSendImage(intent); // Handle single image being sent
            }else if ("audio/*".equals(type)) {
                handleSendAudio(intent); // Handle multiple images being sent
            }
        }
    }

    private String[] fileNameAndType(Uri uri)
    {
        String type=getRealPathFromUri(MainActivity.this,uri);
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
    void handleSendVideo(Intent intent) {
        Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null) {
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.create();

            builder.setTitle("Alert !!");
            builder.setMessage("What do you want ").setCancelable(true).setPositiveButton("Encryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String []file=fileNameAndType(videoUri);
                    encryption(videoUri,file[0],file[1]);
                }
            }).setNegativeButton("Decryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(MainActivity.this, "Dec", Toast.LENGTH_SHORT).show();
                    String []file=fileNameAndType(videoUri);
                    decryption(videoUri,file[0],file[1]);
                }
            });
            builder.show();
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared

            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.create();

            builder.setTitle("Alert !!");
            builder.setMessage("What do you want ").setCancelable(true).setPositiveButton("Encryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String []file=fileNameAndType(imageUri);
                    encryption(imageUri,file[0],file[1]);
                }
            }).setNegativeButton("Decryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Dec", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    void handleSendAudio(Intent intent) {
        Uri audioUris = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (audioUris != null) {
            // Update UI to reflect multiple images being shared
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.create();

            builder.setTitle("Alert !!");
            builder.setMessage("What do you want ").setCancelable(true).setPositiveButton("Encryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String []file=fileNameAndType(audioUris);
                    encryption(audioUris,file[0],file[1]);
                }
            }).setNegativeButton("Decryption", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Dec", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }
    private void decryption(Uri fileUri,String filetype,String fileName)
    {
        androidx.appcompat.app.AlertDialog.Builder builderPass = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builderPass.setTitle("Password..");

        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.text_input_password,null, false);
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
                String path =getRealPathFromUri(MainActivity.this,fileUri);

                EncryptionAndDecryption encryptionAndDecryption=new EncryptionAndDecryption(MainActivity.this);
                encryptionAndDecryption.decryption(m_Text,path);
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
    private void encryption(Uri fileUri,String filetype,String fileName)
    {
        if(auto_key.isChecked())
        {
             String pass=GetPassword(8);
             String path =getRealPathFromUri(MainActivity.this,fileUri);
             File inFile =new File(path);
             EncryptionAndDecryption encryptionAndDecryption=new EncryptionAndDecryption(MainActivity.this);
             encryptionAndDecryption.encryption(pass,path);
        }else
        {
            androidx.appcompat.app.AlertDialog.Builder builderPass = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            builderPass.setTitle("Password..");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity

            View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.text_input_password,null, false);
// Set up the input
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builderPass.setView(viewInflated);

// Set up the buttons
            builderPass.setMessage("For Encryption you have to give Password").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    m_Text = input.getText().toString();
                    //animation
                    String path =getRealPathFromUri(MainActivity.this,fileUri);
                    File inFile =new File(path);
                    EncryptionAndDecryption encryptionAndDecryption=new EncryptionAndDecryption(MainActivity.this);
                    encryptionAndDecryption.encryption(m_Text,path);
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

    }
   private void showDecFilePopup()
   {

       if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           //Toast.makeText(SelectForEncryptActivity.this, "You have already granted this permission!",
           //Toast.LENGTH_SHORT).show();
       } else {
           requestStoragePermission();
       }
       decDialogBuilder=new AlertDialog.Builder(this);
       final View contactPopupView=getLayoutInflater().inflate(R.layout.norm_file_popup,null);
       ImageView cancelBtn=contactPopupView.findViewById(R.id.cancel_btn);
       RelativeLayout video_btn=contactPopupView.findViewById(R.id.dec_video_btn);
       RelativeLayout image_btn=contactPopupView.findViewById(R.id.dec_image_btn);
       RelativeLayout audio_btn=contactPopupView.findViewById(R.id.dec_audio_btn);
       video_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, DecryptedFileActivity.class));
           }
       });
       image_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, ImageFileActivity.class));
           }
       });
       audio_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, AudioFileActivity.class));
           }
       });
       decDialogBuilder.setView(contactPopupView);
       decDialog=decDialogBuilder.create();
       decDialog.show();
       cancelBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               decDialog.dismiss();
           }
       });
   }
    private void showDecPopup(String type)
    {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(SelectForEncryptActivity.this, "You have already granted this permission!",
            //Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
        createFolder("VidKrypt","Decrypted");
        decDialogBuilder=new AlertDialog.Builder(this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.dec_popup,null);
        ImageView cancelBtn=contactPopupView.findViewById(R.id.cancel_btn);
        RelativeLayout dec_video_btn=contactPopupView.findViewById(R.id.dec_video_btn);
        RelativeLayout dec_image_btn=contactPopupView.findViewById(R.id.dec_image_btn);
        RelativeLayout dec_audio_btn=contactPopupView.findViewById(R.id.dec_audio_btn);


        dec_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoDecryptActivity.class));
            }
        });
        dec_image_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, ImageDecryptActivity.class));
           }
       });
        dec_audio_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              startActivity(new Intent(MainActivity.this, AudioDecryptActivity.class));

           }
       });
        decDialogBuilder.setView(contactPopupView);
        decDialog=decDialogBuilder.create();
        decDialog.show();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decDialog.dismiss();
            }
        });


    }
    private void showEncPopup()
    {


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(SelectForEncryptActivity.this, "You have already granted this permission!",
            //Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
        createFolder("VidKrypt","Encrypted");
        encDialogBuilder=new AlertDialog.Builder(this);
        final View contactPopupView=getLayoutInflater().inflate(R.layout.enc_popup,null);
        ImageView cancelBtn=contactPopupView.findViewById(R.id.cancel_btn);
        RelativeLayout enc_video_btn=contactPopupView.findViewById(R.id.enc_video_btn);
        RelativeLayout enc_image_btn=contactPopupView.findViewById(R.id.enc_image_btn);
        RelativeLayout enc_audio_btn=contactPopupView.findViewById(R.id.enc_audio_btn);

        enc_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,VIDEO_REQUEST);
            }
        });
        enc_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,IMAGE_REQUEST);
            }
        });
        enc_audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,AUDIO_REQUEST);
            }
        });


        encDialogBuilder.setView(contactPopupView);
        encDialog=encDialogBuilder.create();
        encDialog.show();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encDialog.dismiss();
            }
        });
    }
   private void anim()
   {
       ImageView img1=findViewById(R.id.enc_image);
       ImageView img2=findViewById(R.id.dec_img);
       ImageView img3=findViewById(R.id.file_img);
       CircleImageView image1=findViewById(R.id.logo_1);
       CircleImageView image2=findViewById(R.id.logo_2);
       CircleImageView image3=findViewById(R.id.logo_3);
       TextView encText =findViewById(R.id.enc_text);
       TextView decText =findViewById(R.id.dec_text);
       TextView fileText =findViewById(R.id.file_text);
       Animation animSlide1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
       Animation animSlide2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_left);
       Animation animSlide3= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
       img1.startAnimation(animSlide1);
       img2.setVisibility(View.GONE);
       img3.setVisibility(View.GONE);
       image1.setVisibility(View.GONE);
       image2.setVisibility(View.GONE);
       image3.setVisibility(View.GONE);
       //encText.setVisibility(View.GONE);
       decText.setVisibility(View.GONE);
       fileText.setVisibility(View.GONE);

       YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(findViewById(R.id.enc_text));
       animSlide1.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {
           }

           @Override
           public void onAnimationEnd(Animation animation) {
               img2.setVisibility(View.VISIBLE);
               img2.startAnimation(animSlide2);
               decText.setVisibility(View.VISIBLE);
               YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(findViewById(R.id.dec_text));
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });
       animSlide2.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {
               image1.setVisibility(View.VISIBLE);

               YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(image1);
           }

           @Override
           public void onAnimationEnd(Animation animation) {
               img3.setVisibility(View.VISIBLE);
               img3.startAnimation(animSlide3);
               fileText.setVisibility(View.VISIBLE);
               YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(findViewById(R.id.file_text));
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });
       animSlide3.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {

               image2.setVisibility(View.VISIBLE);
               YoYo.with(Techniques.FadeIn).duration(4000).repeat(0).playOn(image2);

           }

           @Override
           public void onAnimationEnd(Animation animation) {
               image3.setVisibility(View.VISIBLE);
               YoYo.with(Techniques.FadeIn).duration(4000).repeat(0).playOn(image3);
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });



      // YoYo.with(Techniques.SlideInRight).duration(10000).repeat(0).playOn(findViewById(R.id.enc_image));

   }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of Encryption And Decryption")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
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
                String filetypee []=fileNameAndType(selectedFilePath);
                String type=filetypee[0];
                String name=filetypee[1];
                selectPassDialog(type,name);
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
                String filetypee []=fileNameAndType(selectedFilePath);
                String type=filetypee[0];
                String name=filetypee[1];
                selectPassDialog(type,name);
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

                String filetypee []=fileNameAndType(selectedFilePath);
                String type=filetypee[0];
                String name=filetypee[1];
                selectPassDialog(type,name);
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
                String filetypee []=fileNameAndType(selectedFilePath);
                String type=filetypee[0];
                String name=filetypee[1];
                selectPassDialog(type,name);
            }
        }
    }
    public void selectPassDialog(String filetype, String filename) {
        encDialog.dismiss();
        if(auto_key.isChecked())
        {
            String pass=GetPassword(8);
            filePath=getRealPathFromUri(MainActivity.this,selectedFilePath);
            EncryptionAndDecryption encryptionAndDecryption= new EncryptionAndDecryption(MainActivity.this);
            encryptionAndDecryption.encryption(pass,filePath);
        }else
        {
            androidx.appcompat.app.AlertDialog.Builder builderPass = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            builderPass.setTitle("Password..");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity

            View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_password,null, false);
// Set up the input
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builderPass.setView(viewInflated);

// Set up the buttons
            builderPass.setMessage("For Encryption you have to give Password").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    m_Text = input.getText().toString();
                    //animation
                    filePath=getRealPathFromUri(MainActivity.this,selectedFilePath);
                    EncryptionAndDecryption encryptionAndDecryption= new EncryptionAndDecryption(MainActivity.this);
                    encryptionAndDecryption.encryption(m_Text,filePath);
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

    }
    private String getRealPathFromUri(Activity activity, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void videoPicker() {
        //Intent intent = new Intent(MainActivity.this,FilePickerActivity.class);
    }
    public void seeFileDialog(Activity activity)
    {
        androidx.appcompat.app.AlertDialog.Builder seeFileBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        seeFileBuilder.setTitle("Show File");
        seeFileBuilder.setMessage("Do You Want to see Your Decrypted File");
        seeFileBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this,activity.getClass()));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        seeFileBuilder.create();
        seeFileBuilder.show();
    }
    private void init() {
        //selectEncrypt=findViewById(R.id.selectEncrypt);
        //selectDecrypt=findViewById(R.id.selectDecrypt);
        selectEncrypt=findViewById(R.id.encryptBtn);
        selectDecrypt=findViewById(R.id.decryptBtn);
        decrypted_file=findViewById(R.id.decrypted_file_btn);
        toolbar =(MaterialToolbar)findViewById(R.id.toolbar);
        openBtn=findViewById(R.id.openVideo);
        drawerLayout =(DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        anim();
        navigationView.getMenu().findItem(R.id.keygen).setActionView(new Switch(this));
        // To set whether switch is on/off use:
        //((Switch) navigationView.getMenu().findItem(R.id.keygen).getActionView()).setChecked(false);
        auto_key=((Switch) navigationView.getMenu().findItem(R.id.keygen).getActionView());

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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                String sMessage = "Message: failed to create" + "\nPath " + Environment.getExternalStorageDirectory() +
                        "\nmkdirs" + file.mkdirs();
                //builder.setMessage(sMessage);
                //builder.show();
                //file.mkdirs();
            }
        }
    }
    private void deleteuser(String email, String password) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                                                Toast.makeText(MainActivity.this, "Deleted User Successfully,", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}