package com.ider.yzg.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

/**
 * Created by Eric on 2017/8/29.
 */

public class FileUtil {


    public static String str_audio_type = "audio/*";
    public static String str_video_type = "video/*";
    public static String str_image_type = "image/*";
    public static String str_txt_type = "text/plain";
    public static String str_pdf_type = "application/pdf";
    public static String str_xls_type = "application/vnd.ms-excel";
    public static String str_doc_type = "application/msword";
    public static String str_zip_type = "application/zip";
    public static String str_jar_type = "application/java-archive";
    public static String str_htm_type = "text/html";
    public static String str_tar_type = "application/x-tar";
    public static String str_ppt_type = "application/vnd.ms-powerpoint";
    public static String str_epub_type = "application/epub+zip";
    public static String str_apk_type = "application/vnd.android.package-archive";
    public static String getFileType(File f)
    {
        String type="";
        String fName=f.getName();
        String end=fName.substring(fName.lastIndexOf(".")+1,
                fName.length()).toLowerCase();

      /* get type name  by MimeType */
        if(end.equalsIgnoreCase("mp3")||end.equalsIgnoreCase("wma")
                ||end.equalsIgnoreCase("mp1")||end.equalsIgnoreCase("mp2")
                ||end.equalsIgnoreCase("ogg")||end.equalsIgnoreCase("oga")
                ||end.equalsIgnoreCase("flac")||end.equalsIgnoreCase("ape")
                ||end.equalsIgnoreCase("wav")||end.equalsIgnoreCase("aac")
                ||end.equalsIgnoreCase("m4a")||end.equalsIgnoreCase("m4r")
                ||end.equalsIgnoreCase("amr")||end.equalsIgnoreCase("mid")
                ||end.equalsIgnoreCase("asx"))
        {
            type = str_audio_type;
        }
        else if(end.equalsIgnoreCase("3gp")||end.equalsIgnoreCase("mp4")
                ||end.equalsIgnoreCase("rmvb")||end.equalsIgnoreCase("3gpp")
                ||end.equalsIgnoreCase("avi")||end.equalsIgnoreCase("rm")
                ||end.equalsIgnoreCase("mov")||end.equalsIgnoreCase("flv")
                ||end.equalsIgnoreCase("mkv")||end.equalsIgnoreCase("wmv")
		  ||end.equalsIgnoreCase("divx")||end.equalsIgnoreCase("bob")
		  ||end.equalsIgnoreCase("mpg") || end.equalsIgnoreCase("mpeg")
		  ||end.equalsIgnoreCase("ts") || end.equalsIgnoreCase("dat")
		  ||end.equalsIgnoreCase("m2ts")||end.equalsIgnoreCase("vob")
		  ||end.equalsIgnoreCase("asf")||end.equalsIgnoreCase("evo")
		  ||end.equalsIgnoreCase("iso"))
        {
            type = str_video_type;
//        if(end.equalsIgnoreCase("3gpp")){
//        	if(isVideoFile(f)){
//        		type = str_video_type;
//        	}else{
//        		type = str_audio_type;
//        	}
//        }
        }
        else if(end.equalsIgnoreCase("jpg")||end.equalsIgnoreCase("gif")
                ||end.equalsIgnoreCase("png")||end.equalsIgnoreCase("jpeg")
                ||end.equalsIgnoreCase("bmp"))
        {
            type = str_image_type;
        }
        else if(end.equalsIgnoreCase("txt"))
        {
            type = str_txt_type;
        }else if (end.equalsIgnoreCase("xls")){
            type = str_xls_type;
        }else if (end.equalsIgnoreCase("doc")){
            type = str_doc_type;
        }else if (end.equalsIgnoreCase("zip")){
            type = str_zip_type;
        }else if (end.equalsIgnoreCase("ppt")){
            type = str_ppt_type;
        }else if (end.equalsIgnoreCase("jar")){
            type = str_jar_type;
        }else if (end.equalsIgnoreCase("htm")|| end.equalsIgnoreCase("html") || end.equalsIgnoreCase("hts")){
            type = str_htm_type;
        }else if (end.equalsIgnoreCase("tar")|| end.equalsIgnoreCase("taz") || end.equalsIgnoreCase("tgz")){
            type = str_htm_type;
        }
        else if(end.equalsIgnoreCase("epub") || end.equalsIgnoreCase("pdb") || end.equalsIgnoreCase("fb2") || end.equalsIgnoreCase("rtf") )
        {
            type = str_epub_type;
        }
        else if(end.equalsIgnoreCase("pdf"))
        {
            type = str_pdf_type;
        }
        else if(end.equalsIgnoreCase("apk"))
        {
            type = str_apk_type;
        }else if(end.equalsIgnoreCase("zip")||end.equalsIgnoreCase("rar")||end.equalsIgnoreCase("gz")||end.equalsIgnoreCase("bz2")||end.equalsIgnoreCase("Z")
                ||end.equalsIgnoreCase("tar"))
        {
            type = str_zip_type;
        }
        else
        {
            type="*/*";
        }

        return type;
    }
    public static String getTime(File file){
        if (file.exists()) {
            long time = file.lastModified();
            SimpleDateFormat formatter = new
                    SimpleDateFormat("yyyy-MM-dd HH:mm");
            String result = formatter.format(time);
            return result;
        }
        return "";
    }
    public static String getFileCount(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files!=null){
                return files.length+"";
            }else {
                return "0";
            }
        }
        return "0";
    }
    public static long getFileLCount(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files!=null){
                return files.length;
            }else {
                return 0;
            }
        }
        return 0;
    }
    public static void dirDelete(File dir){
        File[] files = dir.listFiles();
        for (File file:files){
            if (file.isDirectory()){
                dirDelete(file);
            }else {
                file.delete();
            }
        }
        dir.delete();
    }
    public static boolean isApk(File file){
        if (getFileType(file).equals(str_apk_type)){
            return true;
        }
        return false;
    }
    public static String getSize(File file){
        if (file.isDirectory()){
            long size = file.getTotalSpace();
            return getSize(size);
        }else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                long size = fileInputStream.available();
                return getSize(size);
            } catch (Exception e) {
				e.printStackTrace();
                return "0B";
            }
        }
    }
    public static long getLSize(File file){
        if (file.isDirectory()){
            long size = file.getTotalSpace();
            return size;
        }else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                long size = fileInputStream.available();
                return size;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    public static String getSize(long size){
        return getSize((float)size);
    }
    public static String getSize(float size){
        if (size<1024){
            return size+"B";
        }else if (size<1024*1024){
            float s = size/1024;
            return String.format("%.2f",s)+"K";
        }else if (size<1024*1024*1024){
            float s = size/1024/1024;
            return String.format("%.2f",s)+"M";
        }else if (size/1024<1024*1024*1024){

            float s = size/1024/1024/1024;
            return String.format("%.2f",s)+"G";
        }else {
            float s = size/1024/1024/1024/1024;
            return String.format("%.2f",s)+"T";
        }
    }
}
