package com.ider.yzg.util;

import android.util.Log;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.ider.yzg.util.SocketClient.mHandler;

/**
 * Created by Eric on 2017/12/14.
 */

public class FindUtil {

    private static final String TAG = "FindUtil";
    private static List<BoxFile> findList;
    private static long totalBytes;
    private static FindCompleteListener listener;
    public static void findNoDirUploadBoxFile(List<BoxFile> list,FindCompleteListener listener){
        FindUtil.listener = listener;
        totalBytes = 0;
        if (findList==null){
            findList = new ArrayList<>();
        }
        findList.addAll(list);
        list.clear();
        for (BoxFile boxFile:findList){
            if (boxFile.getFileType()==1){
                addNoDirFile(new File(boxFile.getFilePath()),boxFile.getSavePath(),list);
            }else {
                totalBytes = totalBytes+FileUtil.getLSize(new File(boxFile.getFilePath()));
                list.add(boxFile);
            }
        }
        listener.complete(totalBytes,list);
        findList = null;
    }
    public static void findNoDirDownloadBoxFile(final List<BoxFile> list, FindCompleteListener listener){
        FindUtil.listener = listener;
        if (findList==null){
            findList = new ArrayList<>();
        }
        findList.addAll(list);
        list.clear();
        totalBytes = 0;
        addNoDirFile(list);
    }

    private static void addNoDirFile(final List<BoxFile> list){
        if (findList.size()>0){
            final BoxFile boxFile = findList.get(0);
            final String savePath = boxFile.getSavePath();
            if (boxFile.getFileType()==1){
                String comment = changeToUnicode(boxFile.getFilePath());
                RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                    @Override
                    public void resultHandle(String result) {
                        handResult(savePath+File.separator+boxFile.getFileName(),result,list);
                    }
                });
            }else {
                totalBytes = totalBytes+boxFile.getFileSize();
                list.add(boxFile);
                findList.remove(0);
                addNoDirFile(list);
            }
        }else {
            listener.complete(totalBytes,list);
        }
    }
    private static void addNoDirFile(File file, String savePath,List<BoxFile> list){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:files){
                if (f.isDirectory()){
                    addNoDirFile(f,savePath+File.separator+file.getName(),list);
                }else {
                    totalBytes = totalBytes+FileUtil.getLSize(f);
                    addBoxFile(f,savePath+File.separator+file.getName(),list);
                }
            }
        }
    }
    private static void addBoxFile(File f,String savePath,List<BoxFile> list){
        if (f.isDirectory()) {
            list.add(new BoxFile(1, f.getName(),FileUtil.getLTime(f), FileUtil.getFileLCount(f), f.getPath(),savePath));
        } else if (FileUtil.getFileType(f).equals(FileUtil.str_video_type)) {
            list.add(new BoxFile(2, f.getName(),FileUtil.getLTime(f), FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_audio_type)){
            list.add(new BoxFile(3, f.getName(), FileUtil.getLTime(f),FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_image_type)){
            list.add(new BoxFile(4, f.getName(), FileUtil.getLTime(f),FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_apk_type)){
            list.add(new BoxFile(5, f.getName(), FileUtil.getLTime(f),FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_zip_type)){
            list.add(new BoxFile(6, f.getName(),FileUtil.getLTime(f), FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_pdf_type)){
            list.add(new BoxFile(7, f.getName(),FileUtil.getLTime(f), FileUtil.getLSize(f), f.getPath(),savePath));
        }else if (FileUtil.getFileType(f).equals(FileUtil.str_txt_type)){
            list.add(new BoxFile(8, f.getName(),FileUtil.getLTime(f), FileUtil.getLSize(f), f.getPath(),savePath));
        }else {
            list.add(new BoxFile(9, f.getName(), FileUtil.getLTime(f), FileUtil.getLSize(f), f.getPath(),savePath));
        }
    }
    private static void handResult(String savePath ,String result,List<BoxFile> list) {
        if (result.equals("null")) {
            findList.remove(0);
//            new File(savePath).mkdirs();
            addNoDirFile(list);
            return;
        }
        String[] files = result.split("\"type=\"");
        if(files.length==1){
            new File(savePath).mkdirs();
        }
        for (int i = 1; i < files.length; i++) {
            String[] fils = files[i].split("\"name=\"");
            int type = Integer.parseInt(fils[0]);
            String[] fis = fils[1].split("\"size=\"");
            String[] fi = fis[1].split("\"time=\"");
            long size=0;
            long time =  Long.parseLong(fi[1]);
//                Log.i(TAG,fis[0]);
            BoxFile boxFile;
            if (type==0){
                String[] names = fis[0].split("\"path=\"");
                size = Long.parseLong(fi[0]);
                boxFile = new BoxFile(type, names[0],time, size, names[1]);
            }else if (type==1){
                String[] sizes = fi[0].split("\"count=\"");
                size = Long.parseLong(sizes[0]);
                boxFile = new BoxFile(type, fis[0], time, size, files[0] + "/" + fis[0]);
                boxFile.setFileCount(Integer.parseInt(sizes[1]));
            }else {
                size = Long.parseLong(fi[0]);
                boxFile = new BoxFile(type, fis[0], time, size, files[0] + "/" + fis[0]);
            }
            boxFile.setSavePath(savePath);
            findList.add(boxFile);
        }
        findList.remove(0);
        addNoDirFile(list);

    }
    private static String changeToUnicode(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = str.length(); i < length; i++) {
            char c = str.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", (int) c));
            } else {
                stringBuffer.append(c);
            }
        }
        String unicode = stringBuffer.toString();
        return unicode;
    }
    public interface FindCompleteListener{
        void complete(long totalBytes,List<BoxFile> list);
    }
}
