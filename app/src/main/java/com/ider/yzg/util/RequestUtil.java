package com.ider.yzg.util;

import android.util.Log;

import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;

import org.litepal.crud.DataSupport;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ider.yzg.util.SocketClient.mHandler;

/**
 * Created by Eric on 2017/12/12.
 */

public class RequestUtil {
    private static String TAG = "RequestUtil";
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
                            MyData.isRequesting = false;
                            handleResult.resultHandle(result);
                            break;
                        }
                        sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public interface HandleResult{
        void resultHandle(String result);
    }
}
