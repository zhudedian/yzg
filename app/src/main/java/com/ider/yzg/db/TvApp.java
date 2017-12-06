package com.ider.yzg.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Eric on 2017/10/16.
 */

public class TvApp extends DataSupport {
    private String packageName;

    private String label;

    private String type;

    private String versionName;

    private int versionCode;

    private boolean uninstalled;

    public TvApp(String type,String label,String packageName,int versionCode,String versionName){
        this.type = type;
        this.packageName = packageName;
        this.label = label;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
    public TvApp(String packageName,int versionCode){
        this.packageName = packageName;
        this.versionCode = versionCode;
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

    public void setUninstalled(boolean uninstalled){
        this.uninstalled = uninstalled;
    }
    public boolean isUninstalled(){
        return uninstalled;
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
