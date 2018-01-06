package com.ider.yzg.db;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * Created by Eric on 2017/8/29.
 */

public class BoxFile extends DataSupport {

    private int fileTypes;

    private boolean selected;

    private boolean isOpenOp;
    private String fileName;

    private String parentPath;
    private String filePath;

    private long createTime;
    private String savePath;
    private int fileCount;
    private long fileSize;

    public BoxFile(){

    }
    public BoxFile(int types,String fileName,long size){
        this.fileTypes = types;
        this.fileName = fileName;
        this.fileSize = size;
    }
    public BoxFile(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }
    public BoxFile(String fileName,String filePath,String savePath){
        this.fileName = fileName;
        this.filePath = filePath;
        this.savePath = savePath;
    }
    public BoxFile(int types,String fileName,long createTime,long size,String filePath){
        this.fileTypes = types;
        this.fileName = fileName;
        this.createTime = createTime;
        this.fileSize = size;
        this.filePath = filePath;
    }
    public BoxFile(String parentPath,int types,String fileName,long createTime,long size){
        this.parentPath = parentPath;
        this.fileTypes = types;
        this.fileName = fileName;
        this.createTime = createTime;
        this.fileSize = size;
    }
    public BoxFile(String parentPath,int types,String fileName,long createTime,long size,String filePath){
        this.parentPath = parentPath;
        this.fileTypes = types;
        this.fileName = fileName;
        this.createTime = createTime;
        this.fileSize = size;
        this.filePath = filePath;
    }
    public BoxFile(int types,String fileName,long createTime,long size,String filePath,String savePath){
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

    public void setParentPath(String parentPath){
        this.parentPath = parentPath;
    }

    public String getParentPath(){
        return parentPath;
    }

    public void setFilePath(String path){
        this.filePath = path;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setFileSize(long size){
        this.fileSize = size;
    }

    public long getFileSize(){
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

    public void setFileCount(int count){
        this.fileCount = count;
    }
    public int getFileCount(){
        return fileCount;
    }
    public void setSelect(boolean selected){
        this.selected = selected;
    }

    public boolean isSelect(){
        return selected;
    }
    public void setCreateTime(long createTime){
        this.createTime = createTime;
    }
    public long getCreateTime(){
        return createTime;
    }

    public void setOpenOp(boolean isOpenOp){
        this.isOpenOp = isOpenOp;
    }

    public boolean isOpenOp(){
        return isOpenOp;
    }

    public boolean containsPathOf(List<BoxFile> list){
        for (BoxFile boxFile:list){
            if (boxFile.getFilePath().equals(this.filePath)){
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean equals(Object object){
        if (object instanceof BoxFile){
            BoxFile boxFile= (BoxFile) object;
            if (boxFile.fileName.equalsIgnoreCase(this.fileName)){
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
