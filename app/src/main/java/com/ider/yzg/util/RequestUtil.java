package com.ider.yzg.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Eric on 2017/12/12.
 */

public class RequestUtil {
    private static String TAG = "RequestUtil";
    private static HandleResult handleResult;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
    public static void requestWithComment(final String comment,final HandleResult handleResult){
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (!MyData.isRequesting) {
                            MyData.isRequesting = true;
                            Request request = new Request.Builder().header("comment", comment)
                                    .url(MyData.downUrl).build();
                            Call call = okHttpClient.newCall(request);
                            Response response = call.execute();
                            String result = response.body().string();
                            Log.i(TAG, "result:"+result);
                            Message message = mHandler.obtainMessage();
                            message.what = 0;
                            Bundle data = new Bundle();
                            data.putString("result",result);
                            message.setData(data);
                            RequestUtil.handleResult = handleResult;
                            mHandler.sendMessage(message);
                            MyData.isRequesting = false;
                            break;
                        }
                        sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }
    public static void createDir(String dirPath,final HandleResult handleResult){
        String comments = "\"createDir=\"" + dirPath;
        final String comment = StringUtil.changeToUnicode(comments);
        requestWithComment(comment,handleResult);
    }
    public static void delete(List<BoxFile> list, final HandleResult handleResult){
        String fileName = "\"delete=\"" + list.get(0).getParentPath();
        for (int i = 0; i < list.size(); i++) {
            fileName = fileName + "name=" + list.get(i).getFileName();
        }
        final String comment = StringUtil.changeToUnicode(fileName);
        requestWithComment(comment,handleResult);
    }
    public static void sendInfo(String info){
        final String infos = StringUtil.changeToUnicode(info);
        new Thread() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().header("info",infos)
                            .url(MyData.infoUrl).build();
                    Call call = okHttpClient.newCall(request);
                    call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void closeIME(final String info){
        final String infos = StringUtil.changeToUnicode(info);
        new Thread() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().header("info","\"closeIME\""+infos)
                            .url(MyData.infoUrl).build();
                    Call call = okHttpClient.newCall(request);
                    call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void okayIME(final String info){
        final String infos = StringUtil.changeToUnicode(info);
        new Thread() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().header("info","\"okayIME\""+infos)
                            .url(MyData.infoUrl).build();
                    Call call = okHttpClient.newCall(request);
                    call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void requestInfo(final HandleResult handleResult){
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (!MyData.isRequesting) {
                            MyData.isRequesting = true;
                            Request request = new Request.Builder().header("info","\"requestInfo\"" )
                                    .url(MyData.infoUrl).build();
                            Call call = okHttpClient.newCall(request);
                            Response response = call.execute();
                            final String result = response.body().string();
                            Log.i(TAG, "result:"+result);
                            Message message = mHandler.obtainMessage();
                            message.what = 0;
                            Bundle data = new Bundle();
                            data.putString("result",result);
                            message.setData(data);
                            RequestUtil.handleResult = handleResult;
                            mHandler.sendMessage(message);
                            MyData.isRequesting = false;
                            break;
                        }
                        sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
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
                    handleResult.resultHandle(data.getString("result"));
                    break;
                case 1:
                    handleResult.resultHandle("exception");
                    break;
            }
        }
    };

    public interface HandleResult{
        void resultHandle(String result);
    }
}
