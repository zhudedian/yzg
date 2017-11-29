package com.ider.yzg.db;

/**
 * Created by Eric on 2017/10/16.
 */

public class TvApp {
    private String packageName;

    private String label;

    private String type;

    public TvApp(String type,String label,String packageName){
        this.type = type;
        this.packageName = packageName;
        this.label = label;
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

    @Override
    public boolean equals(Object object){
        if (object instanceof ApkFile){
            TvApp apkFile= (TvApp) object;
            if (apkFile.packageName.equals(this.packageName)){
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
