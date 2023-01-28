package com.example.vidkrypt.model;

public class FileData {
    private String key;
    private String file_name;
    private String password;

    public FileData() {
    }

    public FileData(String key, String file_name, String password) {
        this.key = key;
        this.file_name = file_name;
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
