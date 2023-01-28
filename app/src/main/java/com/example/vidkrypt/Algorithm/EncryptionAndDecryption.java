package com.example.vidkrypt.Algorithm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.vidkrypt.Adapter.Constant;
import com.example.vidkrypt.MainActivity;
import com.example.vidkrypt.R;
import com.example.vidkrypt.model.DatabaseHandler;
import com.example.vidkrypt.select.DecryptedFileActivity;
import com.example.vidkrypt.select.decryptFile.AudioDecryptActivity;
import com.example.vidkrypt.select.decryptFile.ImageDecryptActivity;
import com.example.vidkrypt.select.decryptFile.VideoDecryptActivity;
import com.example.vidkrypt.select.decryptedFile.AudioFileActivity;
import com.example.vidkrypt.select.decryptedFile.ImageFileActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionAndDecryption {
    private final static int IV_LENGTH = 16; // Default length with Default 128
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";

    private AlertDialog.Builder encDialogBuilder,decDialogBuilder,showEncAnim,showDecAnim;
    private AlertDialog encDialog,decDialog,animEncDialog,animDecDialog;
    private Context context;
    private String type,imageType,audioType="";

    public EncryptionAndDecryption(Context context) {
        this.context = context;
    }

    public  void encryption(String password, String filePath)
    {
        showEncAnim=new AlertDialog.Builder(context);
        final View contactPopupView= LayoutInflater.from(context).inflate(R.layout.enc_anim,null);
        showEncAnim.setView(contactPopupView);
        showEncAnim.setCancelable(false);
        animEncDialog=showEncAnim.create();
        animEncDialog.show();
        File inFile =new File(filePath);
        String[] file=fileNameAndType(filePath);
        String filename=file[1];
        String filetypee=file[0];
        String encName=filename+filetypee;
        //String decName=fileName+filetype;
        File outFile = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Encrypted"+"/"+encName);
        //File outFile_dec = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt"+"/"+decName);
        try {
            byte[] keyyData= password.getBytes("UTF-16LE");
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
                    animEncDialog.dismiss();
                    Toast.makeText(context, "Encryption Success", Toast.LENGTH_SHORT).show();
                    seeFileDialog(encName,"enc");
                    DatabaseHandler databaseHandler=new DatabaseHandler(context);
                    long id=databaseHandler.checkFile(encName);
                    if(id>0)
                    {
                        String key=String.valueOf(id);
                        databaseHandler.updateKey(key,password);
                    }else
                    {
                        databaseHandler.insertKey(encName,password);
                    }

                }
            },time);
            //AES.decrypt(key2, new FileInputStream(outFile), new FileOutputStream(outFile_dec));
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            animEncDialog.dismiss();
        }
    }

    public void decryption(String password,String filepath)
    {
        showDecAnim=new android.app.AlertDialog.Builder(context);
        final View contactPopupView=LayoutInflater.from(context).inflate(R.layout.dec_anim,null);
        showDecAnim.setView(contactPopupView);
        showDecAnim.setCancelable(false);
        animDecDialog=showDecAnim.create();
        animDecDialog.show();
        File inFile =new File(filepath);
        String[] file=fileNameAndType(filepath);
        String filename=file[1];
        String filetypee=file[0];
        String encName=filename+filetypee;
        String decName=filename+filetypee;
        File outFile = new File(filepath);
        File outFile_dec = new File("/storage/emulated/0/Android/data/com.example.vidkrypt/files/VidKrypt/Decrypted"+"/"+decName);
        try {
            byte[] keyyData= password.getBytes("UTF-16LE");
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
                    Toast.makeText(context, "Decryption Success", Toast.LENGTH_SHORT).show();
                    seeFileDialog(decName,"dec");
                }
            },time);
            //
        } catch (Exception e) {
            Toast.makeText(context, "Wrong Password!!", Toast.LENGTH_SHORT).show();
            animDecDialog.dismiss();
        }
    }

    private  String[] fileNameAndType(String uri)
    {
        File inFile =new File(uri);
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

    public void seeFileDialog(String fileName,String encOrDec)
    {

        for (String extension: Constant.videoExtensions){
            //check the type of file
            if(fileName.endsWith(extension)){
                //when we found file
                type="video";
                break;
            }
        }

            for (String extension: Constant.imageExtensions){
                //check the type of file
                if(fileName.endsWith(extension)){
                    //when we found file
                    type="image";
                    break;
                }
            }

            for (String extension: Constant.audioExtensions){
                //check the type of file
                if(fileName.endsWith(extension)){
                    //when we found file
                    type="audio";
                    break;
                }
            }

        androidx.appcompat.app.AlertDialog.Builder seeFileBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        seeFileBuilder.setTitle("Show File");
        if(encOrDec.equals("enc"))
        {
            seeFileBuilder.setMessage("Do You Want to see Your Encrypted File");
            seeFileBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    if(type.equals("video"))
                    {
                        context.startActivity(new Intent(context, VideoDecryptActivity.class));
                    }else if(type.equals("image"))
                    {
                        context.startActivity(new Intent(context, ImageDecryptActivity.class));
                    }else if(type.equals("audio"))
                    {
                        context.startActivity(new Intent(context, AudioDecryptActivity.class));
                    }

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }else if(encOrDec.equals("dec"))
        {
            seeFileBuilder.setMessage("Do You Want to see Your Decrypted File");
            seeFileBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    if(type.equals("video"))
                    {
                        context.startActivity(new Intent(context, DecryptedFileActivity.class));
                    }else if(type.equals("image"))
                    {
                        context.startActivity(new Intent(context, ImageFileActivity.class));
                    }else if(type.equals("audio"))
                    {
                        context.startActivity(new Intent(context, AudioFileActivity.class));
                    }

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        seeFileBuilder.create();
        seeFileBuilder.show();
    }
}
