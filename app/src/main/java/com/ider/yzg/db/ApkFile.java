package com.ider.yzg.db;

import android.graphics.drawable.Drawable;

import org.litepal.crud.DataSupport;

/**
 * Created by Eric on 2017/8/26.
 */

public class ApkFile extends DataSupport {
    private String fileName;

    private String filePath;

    private Drawable apkDraw;

    private int fileSize;

    public ApkFile(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }
    public ApkFile(String fileName,String filePath,Drawable drawable){
        this.fileName = fileName;
        this.filePath = filePath;
        this.apkDraw = drawable;
    }

    public void setFileName(String name){
        this.fileName = name;
    }

    public String getFileName(){
        return fileName;
    }

    public void setFilePath(String path){
        this.filePath = path;
    }
    public String getFilePath(){
        return filePath;
    }

    public void setFileSize(int size){
        this.fileSize = size;
    }

    public int getFileSize(){
        return fileSize;
    }

    public void setApkDraw(Drawable draw){
        this.apkDraw = draw;
    }
    public Drawable getApkDraw(){
        return apkDraw;
    }
    @Override
    public boolean equals(Object object){
        if (object instanceof ApkFile){
            ApkFile apkFile= (ApkFile) object;
            if (apkFile.filePath.equals(this.filePath)&&apkFile.fileName.equals(this.fileName)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    @Override
    public int hashCode(){
        return 2;
    }
}
