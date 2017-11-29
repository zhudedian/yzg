package com.ider.yzg.db;

import android.graphics.drawable.Drawable;

import static android.R.attr.type;

/**
 * Created by Eric on 2017/8/29.
 */

public class BoxFile {

    private int fileType;

    private boolean select;

    private String fileName;

    private String filePath;

    private Drawable apkDraw;

    private String savePath;

    private String fileSize;

    public BoxFile(){

    }
    public BoxFile(int type,String fileName,String size){
        this.fileType = type;
        this.fileName = fileName;
        this.fileSize = size;
    }
    public BoxFile(String fileName,String filePath){
        this.fileType = type;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public BoxFile(int type,String fileName,String size,String filePath){
        this.fileType = type;
        this.fileName = fileName;
        this.fileSize = size;
        this.filePath = filePath;
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

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
    public String getSavePath(){
        return savePath;
    }

    public void setFileType(int type){
        this.fileType = type;
    }

    public int getFileType(){
        return fileType;
    }

    public void setSelect(boolean select){
        this.select = select;
    }

    public boolean isSelect(){
        return select;
    }

    @Override
    public boolean equals(Object object){
        if (object instanceof BoxFile){
            BoxFile boxFile= (BoxFile) object;
            if (boxFile.fileName.equals(this.fileName)){
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
