package com.example.vidkrypt.Adapter;

import java.io.File;
import java.util.ArrayList;

public class Constant {
    public static String[] videoExtensions = {".mp4",".ts",".mkv",".mov",
            ".3gp",".mv2",".m4v",".webm",".mpeg1",".mpeg2",".mts",".ogm",
            ".bup", ".dv",".flv",".m1v",".m2ts",".mpeg4",".vlc",".3g2",
            ".avi",".mpeg",".mpg",".wmv",".asf"};


    public static String[] imageExtensions={".apng",".avif",".gif",".jpg",".jpeg",".jfif",".pjpeg",".pjp",".png",".svg",".webp"};
    public static String[] audioExtensions={".mp3"};
    //all loaded files will be here
    public static ArrayList<File> allMediaList = new ArrayList<>();
    public static ArrayList<File> allImageList = new ArrayList<>();
    public static ArrayList<File> allAudioList = new ArrayList<>();

}
