package com.ider.yzg.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.yzg.R;
import com.ider.yzg.db.ApkFile;
import com.ider.yzg.db.TvApp;

import org.litepal.crud.DataSupport;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import static android.R.attr.inAnimation;
import static android.R.attr.path;
import static android.R.attr.versionCode;
import static android.R.attr.versionName;

/**
 * Created by Eric on 2017/12/5.
 */

public class ApkUtil {
    private static String TAG = "ApkUtil";
    public static void isApkInstalls(List<ApkFile> localApps,List<TvApp> apps ){
        for (int i = 0 ;i<localApps.size();i++){
            ApkFile apkFile = localApps.get(i);
            for (TvApp app:apps){
                if (apkFile.getPackageName().equals(app.getPackageName())){
                    Log.i(TAG,"packageName:"+apkFile.getPackageName());
                    if (apkFile.getVersionCode()>app.getVersionCode()){
                        apkFile.setInstallLevel(2);
                    }else {
                        apkFile.setInstallLevel(1);
                    }
                    break;
                }else {
                    apkFile.setInstallLevel(3);
                }
            }
            //localApps.set(i,apkFile);
//            DataSupport.deleteAll(ApkFile.class,"filePath = ?",apkFile.getFilePath());
//            apkFile.save();
            Log.i(TAG,"packageName:"+apkFile.getPackageName()+";"+apkFile.getInstallLevel());
        }
    }
    public static void getApksInfo(Context context,List<ApkFile> apks){
        for (ApkFile apkFile:apks){
            getInfo(context,apkFile);
        }
    }
    public static void getInfo(Context context,ApkFile apkFile){
        String path=apkFile.getFilePath();//安装包路径
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if(info != null){
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = path;
            appInfo.publicSourceDir = path;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName;  //得到安装包名称
            String versionName=info.versionName;       //得到版本信息
            int versionCode = info.versionCode;
//            int ver = Integer.getInteger(version);
            //Log.i(TAG,"packageName:"+packageName+";versionName:"+versionName+";versionCode"+versionCode);
//            Toast.makeText(context, "packageName:"+packageName+";version:"+version, Toast.LENGTH_LONG).show();
            Drawable icon = appInfo.loadIcon(pm);//得到图标信息
//            TextView tv = (TextView)findViewById(R.id.tv); //显示图标
//            tv.setBackgroundDrawable(icon);
            apkFile.setLabel(appName);
            apkFile.setPackageName(packageName);
            apkFile.setVersionName(versionName);
            apkFile.setVersionCode(versionCode);
//            DataSupport.deleteAll(ApkFile.class,"filePath = ?",apkFile.getFilePath());
//            apkFile.save();
            apkFile.setApkDraw(icon);

        }
    }
}
