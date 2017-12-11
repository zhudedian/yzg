package com.ider.yzg.util;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2017/12/11.
 */

public class FileFind {

    public static void findFiles(List<BoxFile> fileList, File file){
        if (fileList==null){
            fileList = new ArrayList<>();
        }
        fileList.clear();
        File[] files = file.listFiles();
        if (files != null){
            for(File f:files){
                addFile(f,fileList);
            }
        }
    }
    private static void addFile(File f,List<BoxFile> uploadFiles){
        if (f.isDirectory()) {
            uploadFiles.add(new BoxFile(1, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath()));
        } else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)) {
            uploadFiles.add(new BoxFile(2, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_audio_type)){
            uploadFiles.add(new BoxFile(3, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_image_type)){
            uploadFiles.add(new BoxFile(4, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_apk_type)){
            uploadFiles.add(new BoxFile(5, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_zip_type)){
            uploadFiles.add(new BoxFile(6, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_pdf_type)){
            uploadFiles.add(new BoxFile(7, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath()));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_txt_type)){
            uploadFiles.add(new BoxFile(8, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath()));
        }else {
            uploadFiles.add(new BoxFile(9, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath()));
        }
    }
}
