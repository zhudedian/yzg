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
    public static String root_path = "/storage";
    public static String boxFilePath="/storage";
    public static String picIconSavePath;
    public static String screenshotSavePath = Environment.getExternalStorageDirectory().getPath()+File.separator+"Ider/Screenshot";
    public static SocketClient client;
    public static boolean isRequesting;
    public static String disPlayMode = "normal";

    public final static String NORMAL = "normal";
    public final static String TRANS = "trans";
    public final static String RENAME = "rename";
    public final static String COPY = "copy";
    public final static String MOVE = "move";
    public final static String SELECT = "select";

}
