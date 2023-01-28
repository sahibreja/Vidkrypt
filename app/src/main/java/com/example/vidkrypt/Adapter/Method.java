package com.example.vidkrypt.Adapter;

import java.io.File;

public class Method {

    public static void load_Directory_Files(File directory){
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){

            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                    load_Directory_Files(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.videoExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            Constant.allMediaList.add(fileList[i]);
                            //when we found file
                            break;
                        }
                    }
                }
            }

        }
    }
    public static void load_image_files(File directory)
    {
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){

            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                    load_image_files(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.imageExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            Constant.allImageList.add(fileList[i]);
                            //when we found file
                            break;
                        }
                    }
                }
            }

        }
    }

    public static void load_audio_files(File directory)
    {
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){

            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                    load_audio_files(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.audioExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            Constant.allAudioList.add(fileList[i]);
                            //when we found file
                            break;
                        }
                    }
                }
            }

        }
    }

}
