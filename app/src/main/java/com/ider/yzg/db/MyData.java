package com.ider.yzg.db;

import android.os.Environment;


import com.ider.yzg.util.SocketClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2017/8/28.
 */

public class MyData {

    public static boolean isConnect ;
    public static String boxIP;
    public static String infoUrl;
    public static String downUrl;
    public static String uploadUrl;
    public static String installUrl;
    public static String appIconUrl;
    public static String editInfo;
    public static boolean isShowCheck = false;
    public static boolean isUploading = false;
    public static boolean isDownloading = false;
    public static File dirSelect = Environment.getExternalStorageDirectory();
    public static File fileSelect = Environment.getExternalStorageDirectory();
    public static List<BoxFile> boxFiles = new ArrayList<>();
    public static List<BoxFile> selectBoxFiles = new ArrayList<>();
    public static List<BoxFile> hideFiles;
    public static List<BoxFile> copingFiles = new ArrayList<>();
    public static String boxFilePath="";
    public static String picIconSavePath;
    public static String screenshotSavePath;
    public static SocketClient client;
    public static boolean isRequesting;
    public static String disPlayMode = "normal";

    public final static String NORMAL = "normal";
    public final static String TRANS = "trans";
    public final static String SELECT = "select";

}
