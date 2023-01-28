package com.example.vidkrypt.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "vidkrypt.db";
    private static final String TABLE_KEY = "KEY_TABLE";

    private static final String KEY_ID = "id";
    private static final String VIDEO_NAME = "video_name";
    private static final String VIDEO_KEY = "video_key";

    private Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    public long insertKey(String videoName,String videoKey)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VIDEO_NAME,videoName);
        values.put(VIDEO_KEY,videoKey);
        long id=db.insert(TABLE_KEY,null,values);
        return id;
    }
    public long checkFile(String name)
    {
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_KEY,new String[]{KEY_ID,VIDEO_NAME,VIDEO_KEY},VIDEO_NAME+"=?",new String[]{name}, null, null, null, null);


        if(cursor.getCount()>0)
        {

            if (cursor != null)
            {
                cursor.moveToFirst();

                long idd = Long.parseLong(cursor.getString(0));

                return idd;
            }

        }

        return 0;
    }
    public FileData getData(String videoName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_KEY,new String[]{KEY_ID,VIDEO_NAME,VIDEO_KEY},VIDEO_NAME+"=?",new String[]{videoName}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FileData fileData = new FileData(cursor.getString(0),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return fileData;

    }
    public long updateKey(String key,String newKey)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VIDEO_KEY,newKey);

        long id=db.update(TABLE_KEY,values,KEY_ID+"=?",new String[]{key});
        return id;
    }
    public long update(String key,String newName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VIDEO_NAME,newName);

        long id=db.update(TABLE_KEY,values,KEY_ID+"=?",new String[]{key});
        return id;
    }
    public void delete(String key)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KEY, KEY_ID + " = ?",
                new String[] { key });
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
             String CreateTable="create table "+TABLE_KEY+"("+KEY_ID+" integer primary key autoincrement, "
                     +VIDEO_NAME+" text not null, "
                     +VIDEO_KEY+" text not null"+");";
             try {
                 db.execSQL(CreateTable);
             }catch(Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
