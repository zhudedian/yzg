package com.ider.yzg.util;

import android.util.Log;

import com.ider.yzg.coreprogress.ProgressHelper;
import com.ider.yzg.coreprogress.ProgressUIListener;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.popu.PopupUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;



/**
 * Created by Eric on 2017/12/18.
 */

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static OkHttpClient okHttpClient;
    private static Call call;
    private static DownloadUtil.OnCompleteListener listener;
    private static List<BoxFile> downLoadingFiles ;
    private static long totalDownloadBytes;
    private static long downloadedBytes;
    private static boolean isCanceled;
    private static boolean isComplete;

    public static void startDownload(List<BoxFile> list,long totalBytes,OnCompleteListener listener){
        Log.i(TAG,"list.size()"+list.size());
        DownloadUtil.listener = listener;
        isCanceled = false;
        isComplete = false;
        downLoadingFiles = list;
        totalDownloadBytes = totalBytes;
        downloadedBytes = 0;
        downloading();
    }
    public static void cancel(){
        if (call!=null) {
            call.cancel();
        }
        isCanceled = true;
        okHttpClient = null;
        call = null;
        downLoadingFiles = null;
    }
    private static void downloading(){
        if (downLoadingFiles.size()>0){
            download(downLoadingFiles.get(0));
        }else {
            PopupUtil.forceDismissPopup();
            okHttpClient = null;
            call = null;
            isComplete = true;
            downLoadingFiles = null;
            listener.complete();
        }
    }
    public static void download(BoxFile boxFile,OnCompleteListener listener){
        DownloadUtil.listener = listener;
        isCanceled = false;
        isComplete = false;
        downLoadingFiles = new ArrayList<>();
        downLoadingFiles.add(boxFile);
        downloading();
    }
    private static void download(final BoxFile boxFile) {

        if (okHttpClient==null) {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        }
        Request.Builder builder = new Request.Builder();
        builder.url(MyData.downUrl);
        String comment = StringUtil.changeToUnicode("\"downLoad=\""+boxFile.getFilePath());
        builder.addHeader("comment", comment);
        builder.get();
        call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
                if (!isCanceled) {
                    downloading();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
                ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        PopupUtil.setFileName(boxFile.getFileName());
//                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
//                        Toast.makeText(getApplicationContext(), "开始下载：" + totalBytes, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
//                        Log.e("TAG", "=============start===============");
//                        Log.e("TAG", "numBytes:" + numBytes);
//                        Log.e("TAG", "totalBytes:" + totalBytes);
//                        Log.e("TAG", "percent:" + percent);
//                        Log.e("TAG", "speed:" + speed);
//                        Log.e("TAG", "============= end ===============");
                        PopupUtil.update(downloadedBytes+numBytes,totalDownloadBytes,(float) (downloadedBytes+numBytes)/totalDownloadBytes,speed);
//                        downloadProgeress.setProgress((int) (100 * percent));
//                        downloadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + " MB/秒");
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish(long totalBytes) {
                        super.onUIProgressFinish(totalBytes);
                        downloadedBytes= downloadedBytes+totalBytes;
                        if (!isComplete&&!isCanceled) {
                            downLoadingFiles.remove(0);
                            downloading();
                        }
//                        Log.e("TAG", "onUIProgressFinish:");
//                        Toast.makeText(getApplicationContext(), "结束下载", Toast.LENGTH_SHORT).show();
                    }
                });

                BufferedSource source = responseBody.source();

                File outFile = new File(boxFile.getSavePath()+File.separator+boxFile.getFileName());
                outFile.delete();
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();

                BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                source.readAll(sink);
                sink.flush();
                source.close();
            }
        });
    }
    public interface OnCompleteListener{
        void complete();
    }
}
