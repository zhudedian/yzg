package com.ider.yzg.util;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Eric on 2017/12/14.
 */

public class FindUtil {

    public static List<BoxFile> findNoDirUploadBoxFile(List<BoxFile> list){
        List<BoxFile> newList = new ArrayList<>();
        MyData.totalUploadBytes = 0;
        MyData.uploadedBytes = 0;
        for (BoxFile boxFile:list){
            if (boxFile.getFileType()==1){
                addNoDirFile(new File(boxFile.getFilePath()),boxFile.getSavePath(),newList);
            }else {
                MyData.totalUploadBytes = MyData.totalUploadBytes+FileUtil.getLSize(new File(boxFile.getFilePath()));
                newList.add(boxFile);
            }
        }
        return newList;
    }
    private static void addNoDirFile(File file, String savePath,List<BoxFile> list){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:files){
                if (f.isDirectory()){
                    addNoDirFile(f,savePath+File.separator+file.getName(),list);
                }else {
                    MyData.totalUploadBytes = MyData.totalUploadBytes+FileUtil.getLSize(f);
                    addBoxFile(f,savePath+File.separator+file.getName(),list);
                }
            }
        }
    }
    private static void addBoxFile(File f,String savePath,List<BoxFile> list){
        if (f.isDirectory()) {
            list.add(new BoxFile(1, f.getName(),FileUtil.getTime(f), FileUtil.getFileCount(f), f.getPath(),savePath));
        } else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)) {
            list.add(new BoxFile(2, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_audio_type)){
            list.add(new BoxFile(3, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_image_type)){
            list.add(new BoxFile(4, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_apk_type)){
            list.add(new BoxFile(5, f.getName(), FileUtil.getTime(f),FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_zip_type)){
            list.add(new BoxFile(6, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_pdf_type)){
            list.add(new BoxFile(7, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_txt_type)){
            list.add(new BoxFile(8, f.getName(),FileUtil.getTime(f), FileUtil.getSize(f), f.getPath(),savePath));
        }else {
            list.add(new BoxFile(9, f.getName(), FileUtil.getTime(f), FileUtil.getSize(f), f.getPath(),savePath));
        }
    }

}
