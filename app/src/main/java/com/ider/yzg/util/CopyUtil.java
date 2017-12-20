package com.ider.yzg.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.popu.PopupUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ider.yzg.util.SocketClient.mHandler;


/**
 * Created by Eric on 2017/12/19.
 */

public class CopyUtil {

    private static OnCompleteListener listener;
    private static OkHttpClient okHttpClient;
    private static List<BoxFile> copyingFiles;
    private static BoxFile boxFile;
    private static long lastRefreshTime = 0L;
    private static long lastBytesWritten = 0L;
    private static long minTime = 1000;
    private static int repeatCount;
    private static int maxRepeat = 40;
    private static long totalCopyBytes;
    private static long copyBytes;
    private static boolean isCanceled,isComplete;
    private static boolean isAddCopyBytes;
    private static boolean isUpdateUI;

    public static void startCopyTvFile(List<BoxFile> list, long totalBytes, OnCompleteListener listener){
        CopyUtil.listener = listener;
        isCanceled = false;
        isComplete = false;
        copyingFiles = list;
        totalCopyBytes = totalBytes;
        copyBytes = 0;
        okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        RequestUtil.requestWithComment("\"startCopyFile\"", new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {
                if (result.equals("success")){
                    copying();
                    new Thread(){
                        @Override
                        public void run(){
                            while (!isCanceled&&!isComplete){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                requestCopyByte();
                            }
                        }
                    }.start();

                }
            }
        });

    }

    private static void copying(){
        if (copyingFiles.size()>0){
            copy(copyingFiles.get(0));
        }else {
            PopupUtil.forceDismissPopup();
            copyingFiles = null;
            isComplete = true;
            okHttpClient =null;
            listener.complete();
        }
    }

    private static void copy(final BoxFile boxFile){
        CopyUtil.boxFile = boxFile;
        PopupUtil.setFileName(boxFile.getFileName());
        repeatCount = 0;
        String str = "\"copyFile=\"" + boxFile.getFilePath() + "\"newPath=\"" + boxFile.getSavePath() + File.separator + boxFile.getFileName();
        final String comment = StringUtil.changeToUnicode(str);
        RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {

            }
        });
    }
    public static void cancel(){
        RequestUtil.requestWithComment("\"stopCopyFile\"", new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {

            }
        });
        isCanceled = true;
    }
    private static void requestCopyByte(){
        request("\"copyByte\"");
    }
    private static void request(final String comment){
        new Thread() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().header("comment",comment)
                            .url(MyData.downUrl).build();
                    Call call = okHttpClient.newCall(request);
                    Response response = call.execute();
                    String result = response.body().string();
                    Message message = mHandler.obtainMessage();
                    message.what = 0;
                    Bundle data = new Bundle();
                    data.putString("result",result);
                    message.setData(data);
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();

    }
    public static void updateUI(String result){
        if (!isCanceled&&!isComplete) {
            Log.i("result",result+"copyBytes+boxFile.getSize"+(copyBytes+boxFile.getFileSize()));
            if (result.equals("")){
//                requestCopyByte();
                return;
            }
            if (result.equals("success")){
//                copyingFiles.remove(0);
//                copying();
//                requestCopyByte();
                return;
            }else if (result.equals("failed")){
//                copying();
//                requestCopyByte();
                return;
            }
            if (result.contains("\"copyByte=\"")) {
                result = result.replace("\"copyByte=\"","");
                long numBytes = Long.parseLong(result);
                if (numBytes>=(copyBytes+boxFile.getFileSize())){
                    copyingFiles.remove(0);
                    copyBytes = numBytes;
                    copying();
                    return;
                }
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastRefreshTime >= minTime) {
                    long intervalTime = (currentTime - lastRefreshTime);
                    if (intervalTime == 0) {
                        intervalTime += 1;
                    }
                    long updateBytes = numBytes - lastBytesWritten;
                    if (updateBytes==0){
                        repeatCount++;
                        if (repeatCount>=maxRepeat){
                            repeatCount = 0;
                            copying();
                        }
                    }else {
                        repeatCount = 0;
                    }
                    final long copySpeed = updateBytes / intervalTime * 1000;
                    if (!isComplete && !isCanceled) {
                        PopupUtil.update(numBytes, totalCopyBytes, (float) numBytes / totalCopyBytes, copySpeed);
                    }
                    lastRefreshTime = System.currentTimeMillis();
                    lastBytesWritten = numBytes;
                    return;
                }
            }

        }
    }
    private static Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    Bundle data = msg.getData();
                    if (data == null) {
                        return;
                    }
                    if (!isUpdateUI) {
                        isUpdateUI = true;
                        CopyUtil.updateUI(data.getString("result"));
                        isUpdateUI = false;
                    }
                    break;
                case 1:

                    break;
            }
        }
    };
    public static void startCopyLocalFile(List<BoxFile> list, long totalBytes, OnCompleteListener listener){
        CopyUtil.listener = listener;
        isCanceled = false;
        isComplete = false;
        copyingFiles = list;
        totalCopyBytes = totalBytes;
        copyBytes = 0;
    }
    private static void copyingLocal(){
        if (copyingFiles.size()>0){
            BoxFile boxFile = copyingFiles.get(0);
            copy(boxFile.getFilePath(),boxFile.getSavePath()+File.separator+boxFile.getFileName());
        }else {
            PopupUtil.forceDismissPopup();
            copyingFiles = null;
            isComplete = true;
            listener.complete();
        }
    }
    private static void updateUI(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRefreshTime >= minTime) {
            long intervalTime = (currentTime - lastRefreshTime);
            if (intervalTime == 0) {
                intervalTime += 1;
            }
            long updateBytes = copyBytes - lastBytesWritten;
            final long copySpeed = updateBytes / intervalTime * 1000;
            PopupUtil.update(copyBytes , totalCopyBytes, (float) copyBytes / totalCopyBytes, copySpeed);
            lastRefreshTime = System.currentTimeMillis();
            lastBytesWritten = copyBytes;
            return;
        }
    }
    public static boolean copy(String oldPath,String newPath){
        File file = new File(oldPath);
        if (file.isDirectory()){
            return copyFolder(oldPath,newPath);
        }else {
            return copyFile(oldPath,newPath);
        }
    }
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldfile.exists()&&!isCanceled) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[4096];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    if (!isCanceled) {
                        copyBytes += byteread; //字节数 文件大小
                        fs.write(buffer, 0, byteread);
                        updateUI();
                    }else {
                        newFile.delete();
                        return false;
                    }
                }
                fs.close();
                inStream.close();
                copyingFiles.remove(0);
                copyingLocal();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp;
            for (int i = 0; i < file.length&&!isCanceled; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    copyFile(temp.getPath(),newPath + File.separator + temp.getName());
                } else if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface OnCompleteListener{
        void complete();
    }
}
