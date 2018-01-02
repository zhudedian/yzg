package com.ider.yzg.db;

/**
 * Created by Eric on 2018/1/2.
 */

public class Picture {

    private String picName;
    private String picPath;
    private long picTime;
    public Picture(String picName,String picPath,long picTime){
        this.picName = picName;
        this.picPath = picPath;
        this.picTime = picTime;
    }

    public void setPicName(String picName){
        this.picName = picName;
    }
    public String getPicName(){
        return picName;
    }
    public void setPicPath(String picPath){
        this.picPath = picPath;
    }
    public String getPicPath(){
        return picPath;
    }
    public void setPicTime(long picTime){
        this.picTime = picTime;
    }
    public long getPicTime(){
        return picTime;
    }
}
