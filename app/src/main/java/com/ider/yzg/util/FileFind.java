package com.ider.yzg.util;

import android.os.Handler;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2017/12/11.
 */

public class FileFind {

    public static void findFiles(List<BoxFile> fileList, File file, Handler handler){
        if (fileList==null){
            fileList = new ArrayList<>();
        }
        if (MyData.hideFiles==null){
            MyData.hideFiles=new ArrayList<>();
        }
        MyData.hideFiles.clear();
        fileList.clear();
        File[] files = file.listFiles();
        if (files != null){
            for(File f:files){
                addFile(f,fileList);
            }
        }
        //ListSort.sort(fileList);
        handler.sendEmptyMessage(0);
    }
    private static void addFile(File f,List<BoxFile> uploadFiles){
        if (f.isDirectory()) {
            uploadFiles.add(new BoxFile(1, f.getName(),FileUtil.getTime(f), FileUtil.getFileLCount(f), f.getPath()));
        } else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)&&MyData.disPlayMode.equals(MyData.NORMAL)) {
            uploadFiles.add(new BoxFile(2, f.getName(),FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_audio_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(3, f.getName(), FileUtil.getTime(f),FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_image_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(4, f.getName(), FileUtil.getTime(f),FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_apk_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(5, f.getName(), FileUtil.getTime(f),FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_zip_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(6, f.getName(),FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_pdf_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(7, f.getName(),FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_txt_type)&&MyData.disPlayMode.equals(MyData.NORMAL)){
            uploadFiles.add(new BoxFile(8, f.getName(),FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
        }else {
            if (MyData.disPlayMode.equals(MyData.NORMAL)) {
                uploadFiles.add(new BoxFile(9, f.getName(), FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
            }else {
                MyData.hideFiles.add(new BoxFile(9, f.getName(), FileUtil.getTime(f), FileUtil.getLSize(f), f.getPath()));
            }
        }
    }
}
