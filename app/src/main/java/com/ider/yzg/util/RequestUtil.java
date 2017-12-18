package com.ider.yzg.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ider.yzg.db.MyData;

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
