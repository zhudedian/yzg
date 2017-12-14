package com.ider.yzg.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Eric on 2017/8/29.
 */

public class BoxFile extends DataSupport {

    private int fileTypes;

    private boolean selected;

    private boolean isOpenOp;
    private String fileName;

    private String filePath;

    private String createTime;
    private String savePath;

    private String fileSize;

    public BoxFile(){

    }
    public BoxFile(int types,String fileName,String size){
        this.fileTypes = types;
        this.fileName = fileName;
        this.fileSize = size;
    }
    public BoxFile(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public BoxFile(int types,String fileName,String createTime,String size,String filePath){
        this.fileTypes = types;
        this.fileName = fileName;
        this.createTime = createTime;
        this.fileSize = size;
        this.filePath = filePath;
    }
    public BoxFile(int types,String fileName,String createTime,String size,String filePath,String savePath){
        this.fileTypes = types;
        this.fileName = fileName;
        this.createTime = createTime;
        this.fileSize = size;
        this.filePath = filePath;
        this.savePath = savePath;
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

    public void setFileType(int types){
        this.fileTypes = types;
    }

    public int getFileType(){
        return fileTypes;
    }

    public void setSelect(boolean selected){
        this.selected = selected;
    }

    public boolean isSelect(){
        return selected;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return createTime;
    }

    public void setOpenOp(boolean isOpenOp){
        this.isOpenOp = isOpenOp;
    }

    public boolean isOpenOp(){
        return isOpenOp;
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
