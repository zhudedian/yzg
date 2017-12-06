package com.ider.yzg.db;

import android.graphics.drawable.Drawable;

import org.litepal.crud.DataSupport;

import static com.ider.yzg.R.string.installed;

/**
 * Created by Eric on 2017/8/26.
 */

public class ApkFile extends DataSupport {
    private String fileName;

    private String filePath;

    private Drawable apkDraw;

    private String fileSize;

    private int installLevel = 1;
    private String packageName;

    private String label;

    private String type;

    private String versionName;

    private int versionCode;


    public ApkFile(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }
    public ApkFile(String fileName,String filePath,String fileSize){
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
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

    public void setFileSize(String size){
        this.fileSize = size;
    }

    public String getFileSize(){
        return fileSize;
    }

    public void setApkDraw(Drawable draw){
        this.apkDraw = draw;
    }
    public Drawable getApkDraw(){
        return apkDraw;
    }

    public void setInstallLevel(int installLevel){
        this.installLevel = installLevel;
    }
    public int getInstallLevel(){
        return installLevel;
    }
    public void setPackageName(String name){
        this.packageName = name;
    }

    public String getPackageName(){
        return packageName;
    }

    public void setLabel(String label){
        this.label = label;
    }
    public String getLabel(){
        return label;
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }


    public void setVersionCode(int versionCode){
        this.versionCode = versionCode;
    }
    public int getVersionCode(){
        return versionCode;
    }
    public void setVersionName(String versionName){
        this.versionName = versionName;
    }
    public String getVersionName(){
        return versionName;
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
