package com.example.vidkrypt.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vidkrypt.Algorithm.AES;
import com.example.vidkrypt.Algorithm.EncryptionAndDecryption;
import com.example.vidkrypt.MainActivity;
import com.example.vidkrypt.R;
import com.example.vidkrypt.model.DatabaseHandler;
import com.example.vidkrypt.model.FileData;
import com.example.vidkrypt.player.VideoPlayerActivity;
import com.example.vidkrypt.select.DecryptedFileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String type;
   private android.app.AlertDialog.Builder showDecAnim;
   private android.app.AlertDialog animDecDialog;
    private final static int IV_LENGTH = 16; // Default length with Default 128
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    public RecyclerViewAdapter(Context mContext,String type){
        this.mContext = mContext;
        this.type=type;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list,parent,false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ((FileLayoutHolder)holder).videoTitle.setText(Constant.allMediaList.get(position).getName());
        //we will load thumbnail using glid library
        Uri uri = Uri.fromFile(Constant.allMediaList.get(position));


        if(type.equals("decrypted"))
        {
            ((FileLayoutHolder) holder).ic_more_btn.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(uri).thumbnail(0.1f).into(((FileLayoutHolder)holder).thumbnail);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, VideoPlayerActivity.class);
                    intent.putExtra("position",position);
                    intent.putExtra("video_title",Constant.allMediaList.get(position).getName());
                    mContext.startActivity(intent);
                }
            });

            ((FileLayoutHolder) holder).ic_more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.getMenuInflater().inflate(R.menu.pop_menu_for_file, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.shareBtn:
                                    //shareVideo(Constant.allMediaList.get(position).getName(),Constant.allMediaList.get(position).getPath());
                                    Uri uri = FileProvider.getUriForFile(mContext, "com.example.vidkrypt.fileprovider",
                                            new File(Constant.allMediaList.get(position).getPath()));
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey This is encrypted file ");
                                    shareIntent.setType("video/*");
                                    mContext.startActivity(Intent.createChooser(shareIntent, null));
                                    break;
                                case R.id.deleteBtn:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("Delete")
                                            .setMessage("Are you sure you want to delete this file")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Do nothing
                                                }
                                            })
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteFile(position);


                                                }
                                            });

                                    builder.create();
                                    builder.show();
                                    break;
                                case R.id.renameBtn:
                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                                    bottomSheetDialog.setContentView(R.layout.rename_layout);
                                    EditText rename_text=bottomSheetDialog.findViewById(R.id.rename_get_text);
                                    android.widget.Button ok_btn=bottomSheetDialog.findViewById(R.id.ok_btn);
                                    android.widget.Button cancel_btn=bottomSheetDialog.findViewById(R.id.cancel_btn);
                                    String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                    rename_text.setText(type[1]);

                                    bottomSheetDialog.show();
                                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    ok_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newNameTxt=rename_text.getText().toString().trim();
                                            String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                            String newName=newNameTxt+type[0];
                                            renameFile(Constant.allMediaList.get(position).getName(),newName,position);
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    break;
                            }
                            return true;
                        }
                    });

                }
            });
        }else if(type.equals("encrypted"))
        {
             DatabaseHandler databaseHandler= new DatabaseHandler(mContext);
             FileData fileData=databaseHandler.getData(Constant.allMediaList.get(position).getName());

            ((FileLayoutHolder) holder).thumbnail.setImageResource(R.drawable.encrypted);
            ((FileLayoutHolder) holder).key_layout.setVisibility(View.VISIBLE);
            ((FileLayoutHolder) holder).password.setText(fileData.getPassword());
             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     PopupMenu popupMenu = new PopupMenu(mContext,v);
                     popupMenu.getMenuInflater().inflate(R.menu.pop_menu,popupMenu.getMenu());
                     popupMenu.show();
                     popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                         @Override
                         public boolean onMenuItemClick(MenuItem item) {
                             switch(item.getItemId())
                             {
                                 case R.id.shareBtn:
                                     //shareVideo(Constant.allMediaList.get(position).getName(),Constant.allMediaList.get(position).getPath());
                                     Uri uri= FileProvider.getUriForFile(mContext,"com.example.vidkrypt.fileprovider",
                                             new File(Constant.allMediaList.get(position).getPath()));
                                     Intent shareIntent = new Intent();
                                     shareIntent.setAction(Intent.ACTION_SEND);
                                     shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                     shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey This is encrypted file ");
                                     shareIntent.setType("video/*");
                                     mContext.startActivity(Intent.createChooser(shareIntent, null));
                                     break;
                                 case R.id.deleteBtn:
                                     AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                     builder.setTitle("Delete")
                                             .setMessage("Are you sure you want to delete this file")
                                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     //Do nothing
                                                 }
                                             })
                                             .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     databaseHandler.delete(fileData.getKey());
                                                     deleteFile(position);
                                                 }
                                             });

                                     builder.create();
                                     builder.show();
                                     break;
                                 case R.id.decryptBtn:
                                     AlertDialog.Builder builderr = new AlertDialog.Builder(v.getRootView().getContext());
                                     builderr.setTitle("Password..");
                                     View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.text_input_password,null, false);

                                     final EditText input = (EditText) viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                     builderr.setView(viewInflated);

                                     builderr.setMessage("For Decryption you have to give Password").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             dialog.dismiss();

                                             String m_Text = input.getText().toString();
                                             String filePath=Constant.allMediaList.get(position).getPath();

                                             EncryptionAndDecryption encryptionAndDecryption= new EncryptionAndDecryption(mContext);

                                             encryptionAndDecryption.decryption(m_Text,filePath);
                                         }
                                     });
                                     builderr.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             dialog.cancel();
                                         }
                                     });
                                     builderr.show();
                                     break;
                                 case R.id.renameBtn:
                                     final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                                     bottomSheetDialog.setContentView(R.layout.rename_layout);
                                     EditText rename_text=bottomSheetDialog.findViewById(R.id.rename_get_text);
                                     android.widget.Button ok_btn=bottomSheetDialog.findViewById(R.id.ok_btn);
                                     android.widget.Button cancel_btn=bottomSheetDialog.findViewById(R.id.cancel_btn);
                                     String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                     rename_text.setText(type[1]);

                                     bottomSheetDialog.show();
                                     cancel_btn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             bottomSheetDialog.dismiss();
                                         }
                                     });
                                     ok_btn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String newNameTxt=rename_text.getText().toString().trim();
                                             String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                             String newName=newNameTxt+type[0];
                                             String id=fileData.getKey();
                                             long idd=databaseHandler.update(id,newName);
                                             renameFile(Constant.allMediaList.get(position).getName(),newName,position);
                                             bottomSheetDialog.dismiss();
                                         }
                                     });
                                     break;
                             }
                             return true;
                         }
                     });
                     //Toast.makeText(mContext, "More Button encrypted", Toast.LENGTH_SHORT).show();


                 }
             });
            ((FileLayoutHolder) holder).ic_more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.pop_menu,popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId())
                            {
                                case R.id.shareBtn:
                                    //shareVideo(Constant.allMediaList.get(position).getName(),Constant.allMediaList.get(position).getPath());
                                  Uri uri= FileProvider.getUriForFile(mContext,"com.example.vidkrypt.fileprovider",
                                          new File(Constant.allMediaList.get(position).getPath()));
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey This is encrypted file ");
                                    shareIntent.setType("video/*");
                                    mContext.startActivity(Intent.createChooser(shareIntent, null));
                                    break;
                                case R.id.deleteBtn:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("Delete")
                                            .setMessage("Are you sure you want to delete this file")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Do nothing
                                                }
                                            })
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteFile(position);


                                                }
                                            });

                                     builder.create();
                                     builder.show();
                                    break;
                                case R.id.renameBtn:
                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                                    bottomSheetDialog.setContentView(R.layout.rename_layout);
                                    EditText rename_text=bottomSheetDialog.findViewById(R.id.rename_get_text);
                                    android.widget.Button ok_btn=bottomSheetDialog.findViewById(R.id.ok_btn);
                                    android.widget.Button cancel_btn=bottomSheetDialog.findViewById(R.id.cancel_btn);
                                    String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                    rename_text.setText(type[1]);

                                    bottomSheetDialog.show();
                                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    ok_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newNameTxt=rename_text.getText().toString().trim();
                                            String []type=fileNameAndType(Constant.allMediaList.get(position).getPath());
                                            String newName=newNameTxt+type[0];
                                            renameFile(Constant.allMediaList.get(position).getName(),newName,position);
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                    break;
                            }
                            return true;
                        }
                    });
                    //Toast.makeText(mContext, "More Button encrypted", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
    public void renameFile(String oldName, String newName,int pos)
    {
        boolean result;
        try {

            File from=new File(Objects.requireNonNull(Constant.allMediaList.get(pos).getParent()).toString(),oldName);

            File to =new File(Objects.requireNonNull(Constant.allMediaList.get(pos).getParent()).toString(),newName);

            result=from.renameTo(to);
            if(result)
            {
                Toast.makeText(mContext, "Renamed", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext,mContext.getClass());
                ((Activity) mContext).overridePendingTransition(0,0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0,0);
                ((Activity)mContext).finish();
            }else
            {
                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e)
        {
            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
        }



    }

    private String[] fileNameAndType(String uri)
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
    public void seeFileDialog()
    {
        AlertDialog.Builder seeFileBuilder = new AlertDialog.Builder(mContext);
        seeFileBuilder.setTitle("Show File");
        seeFileBuilder.setMessage("Do You Want to see Your Decrypted File");
        seeFileBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(mContext, DecryptedFileActivity.class));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        seeFileBuilder.create();
        seeFileBuilder.show();
    }
   public void deleteFile(int pos)
   {
       File file = Constant.allMediaList.get(pos);
       boolean result;
//       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//       {
//           Toast.makeText(mContext, "Version Not supported", Toast.LENGTH_SHORT).show();
//       }else
//       {
           result =file.delete();
           if(result)
           {
               Constant.allMediaList.remove(pos);
               Intent intent=new Intent(mContext,mContext.getClass());
               ((Activity) mContext).overridePendingTransition(0,0);
               mContext.startActivity(intent);
               ((Activity) mContext).overridePendingTransition(0,0);
               ((Activity)mContext).finish();


           }else
           {
               Toast.makeText(mContext, "Deletion Failed !", Toast.LENGTH_SHORT).show();
           }
      // }
   }
    @Override
    public int getItemCount() {

        return Constant.allMediaList.size();
    }
    public void shareVideo(final String title, String path) {

        MediaScannerConnection.scanFile(mContext, new String[] { path },

                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        uri=Uri.parse(path);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        mContext.startActivity(Intent.createChooser(shareIntent, "Share"));

                    }
                });
    }
    static class FileLayoutHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView videoTitle;
        ImageView ic_more_btn;
        LinearLayout key_layout;
        TextView password;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.videotitle);
            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
            key_layout = itemView.findViewById(R.id.key_layout_visible);
            password = itemView.findViewById(R.id.password);

        }
    }

}