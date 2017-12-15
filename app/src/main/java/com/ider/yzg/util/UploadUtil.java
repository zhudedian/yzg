package com.ider.yzg.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ider.yzg.coreprogress.ProgressHelper;
import com.ider.yzg.coreprogress.ProgressUIListener;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.popu.PopupUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.list;
import static android.R.id.navigationBarBackground;

/**
 * Created by Eric on 2017/12/14.
 */

public class UploadUtil {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 60*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    public static boolean isStart;

    public static void uploading(final Handler mHandler){
            new Thread() {
                @Override
                public void run() {
                    MyData.uploadingFiles = FindUtil.findNoDirUploadBoxFile(MyData.uploadingFiles);
                    while (MyData.uploadingFiles.size() > 0) {
                        BoxFile boxFile = MyData.uploadingFiles.get(0);
                        File file = new File(boxFile.getFilePath());
                        Log.i("boxFile.getFilePath()", boxFile.getFilePath());
                        int result = uploadFile(file, MyData.uploadUrl, boxFile.getSavePath());
                        Log.i("result", result+"");
                        if (result!=200){
                            continue;
                        }else {
                            if (MyData.uploadingFiles.size()>0) {
                                MyData.uploadingFiles.remove(0);
                            }
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                    mHandler.sendEmptyMessage(1);
                }
            }.start();

    }
    public static void upload(final Context context) {
        MyData.uploadingFiles = FindUtil.findNoDirUploadBoxFile(MyData.uploadingFiles);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(MyData.uploadUrl);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        for (BoxFile boxFile:MyData.uploadingFiles) {
            File apkFile = new File(boxFile.getFilePath());
            bodyBuilder.addPart(Headers.of("savePath",boxFile.getSavePath()),RequestBody.create(null, apkFile));
        }
        MultipartBody build = bodyBuilder.build();

        //callback in original thread.
//        ProgressListener progressListener = new ProgressListener() {
//
//            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
//            @Override
//            public void onProgressStart(long totalBytes) {
//                super.onProgressStart(totalBytes);
//            }
//
//            @Override
//            public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
//
//            }
//
//            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
//            @Override
//            public void onProgressFinish() {
//                super.onProgressFinish();
//            }
//        };

        RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                Log.e("TAG", "onUIProgressStart:" + totalBytes);
                Toast.makeText(context, "开始上传：" + totalBytes, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                Log.e("TAG", "=============start===============");
                Log.e("TAG", "numBytes:" + numBytes);
                Log.e("TAG", "totalBytes:" + totalBytes);
                Log.e("TAG", "percent:" + percent);
                Log.e("TAG", "speed:" + speed);
                Log.e("TAG", "============= end ===============");
                PopupUtil.update(numBytes,totalBytes,percent,speed);
//                uploadProgress.setProgress((int) (100 * percent));
//                uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");

            }

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                PopupUtil.forceDismissPopup();
                Log.e("TAG", "onUIProgressFinish:");
//                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
            }
        });
        builder.post(requestBody);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
            }
        });
    }

    public static int uploadFile(File file, String RequestURL,String savePath)
    {
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("savePath",savePath);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if(file!=null)
            {
                isStart = true;
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"apk\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[4096];
                int len = 0;
                while(isStart&&(len=is.read(bytes))!=-1)
                {

                    dos.write(bytes, 0, len);
                    Log.i(TAG, " dos.write");
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.i(TAG, "response code:"+res);
//                if(res==200)
//                {
                Log.i(TAG, "request success");
                InputStream input =  conn.getInputStream();
                StringBuffer sb1= new StringBuffer();
                int ss ;
                while((ss=input.read())!=-1)
                {
                    sb1.append((char)ss);
                }
                result = sb1.toString();
                Log.i(TAG, "result : "+ result);
//                }
//                else{
//                    Log.e(TAG, "request error");
//                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
        return 404;
    }
}