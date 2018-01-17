package com.ider.yzg.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.yzg.R;
import com.ider.yzg.adapter.ApkAdapter;
import com.ider.yzg.adapter.AppsAdapter;
import com.ider.yzg.db.ApkFile;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.db.TvApp;
import com.ider.yzg.net.CustomerHttpClient;
import com.ider.yzg.net.HTTPFileDownloadTask;
import com.ider.yzg.net.UploadUtil;
import com.ider.yzg.popu.PopuUtils;
import com.ider.yzg.popu.PopupDialog;
import com.ider.yzg.popu.PopupUtil;
import com.ider.yzg.popu.Popus;
import com.ider.yzg.util.ApkUtil;
import com.ider.yzg.util.FileUtil;
import com.ider.yzg.util.FragmentInter;
import com.ider.yzg.util.RequestUtil;
import com.ider.yzg.util.TvAppSort;

import org.apache.http.client.HttpClient;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Eric on 2017/11/29.
 */

public class AppsFragment extends Fragment implements View.OnClickListener,FragmentInter {

    private String TAG = "AppsFragment";
    private Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private PackageManager packageManager;
    private TextView recommend,local,uninstall;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private NoticeBar noticeBar;
    private ListView listView;
    private File[] files;
    private ApkAdapter adapter;
    private AppsAdapter appsAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<ApkFile> findApks;
    private List<ApkFile> localApps;
    private boolean isFirstOpen = true;
    private boolean isDataSave;
    private int page = 2;


    private OkHttpClient okHttpClient;
    private AppsFragment.HTTPdownloadHandler mHttpDownloadHandler;
    private HTTPFileDownloadTask mHttpTask;
    private HttpClient mHttpClient;
    private URI mHttpUri;
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    private List<TvApp> apps = new ArrayList<>();
    private String picDownPath;
    private boolean isLoadLocal;
    private List<String> downApps = new ArrayList<>();
    private boolean isDownload = false;
    private boolean isFinding = false;
    private boolean isLocalOk,isTvAppOk;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_apps,container,false);
        noticeBar = (NoticeBar)view.findViewById(R.id.notice_bar);
        recommend = (TextView) view.findViewById(R.id.recommend_button);
        local = (TextView) view.findViewById(R.id.local_button);
        uninstall = (TextView)view.findViewById(R.id.uninstall_button);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        listView = (ListView)view.findViewById(R.id.list_view);

        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        initData();
        setListener();
        if (appsAdapter==null) {
            appsAdapter = new AppsAdapter(context, R.layout.apk_list_item, apps);
            appsAdapter.setOnApkInstallClickListener(new AppsAdapter.OnApkInstallClickListener() {
                @Override
                public void installClick(final TvApp tvApp) {
                    if (MyData.isConnect) {
                        if (tvApp.getType().equals("1")) {
//                            String packageName = tvApp.getPackageName();
//                            Log.i(TAG, tvApp.getPackageName());
                            PopupUtil.buildConfirmPopup(getContext(), "确认卸载？", tvApp.getLabel(), "卸载", "取消", true, new ConfirmPopu.OnOkListener() {
                                @Override
                                public void onOkClick(boolean isOk, boolean isAllCheck) {
                                    if (isOk){
                                        final String comment = changeToUnicode("\"uninstall=\"" +tvApp.getPackageName());
                                        RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                                            @Override
                                            public void resultHandle(String result) {
                                                Toast.makeText(getContext(),"卸载成功！",Toast.LENGTH_SHORT).show();
                                                initTvState();
                                            }
                                        });
                                    }
                                    PopupUtil.forceDismissPopup();
                                }
                            }).show(listView);
                        }else {
                            Toast.makeText(context,context.getString(R.string.system_app_uninstall_notice),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context,context.getString(R.string.disconnect_notice2),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            local.performClick();
        }

        if (apps==null||apps.size()==0||isLoadLocal)
        initTvState();
        initView();
    }
    @Override
    public void fragmentInit() {
//        Log.i("fragmentInit",localApps.size()+"");
        initView();
        if (page == 1){
            recommend.performClick();
        }else if (page == 2){
            if (localApps.size()==0||(isLoadLocal&&MyData.isConnect)) {
                local.performClick();
                initTvState();
            }

        }else if (page ==3){
            if (apps.size()==0||isLoadLocal) {
                uninstall.performClick();
            }
        }

    }
    private void initView(){
        if (MyData.isConnect){
            noticeBar.setGONE();
        }else {
            noticeBar.setVISIBLE();
        }
        if (page==2){
            clickLocal();
        }else {
            clickUninstall();
        }
    }
    @Override
    public  void fragmentHandleMsg(String msg){
        if (msg.contains("connect_success")) {
            initTvState();
        }else if (msg.contains("connect_failed")) {

        }else if (msg.contains("InUnCp")) {
            initTvApp();
        }
        initView();
    }

    private void initData(){
        preferences = context.getSharedPreferences("yzg_prefers", Context.MODE_PRIVATE);
        editor = preferences.edit();
        okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        mHttpClient = CustomerHttpClient.getHttpClient();
        mHttpDownloadHandler = new AppsFragment.HTTPdownloadHandler();
        try {
            if (MyData.downUrl !=null)
            mHttpUri = new URI(MyData.downUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        MyData.picIconSavePath = rootPath.endsWith(File.separator)?rootPath+"Ider/icon":rootPath+File.separator+"Ider/icon";
    }
    private void setListener(){
        recommend.setOnClickListener(this);
        uninstall.setOnClickListener(this);
        local.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.recommend_button:
                Toast.makeText(getContext(),"暂未开放该功能！",Toast.LENGTH_SHORT).show();
//                clickRecommend();
                break;
            case R.id.local_button:
                page = 2;
                clickLocal();
                break;
            case R.id.uninstall_button:
                page = 3;
                clickUninstall();
                break;
        }
    }
    private void clickRecommend(){
        recommend.setSelected(true);
        local.setSelected(false);
        uninstall.setSelected(false);
        recommend.setTextColor(getResources().getColor(R.color.black));
        local.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.white));

    }
    private void clickLocal(){
        local.setSelected(true);
        recommend.setSelected(false);
        uninstall.setSelected(false);
        local.setTextColor(getResources().getColor(R.color.black));
        recommend.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.white));
        if (adapter!=null&&localApps.size()>0&&!isLoadLocal){
            mHandler.sendEmptyMessage(0);
            if (localApps.size()==0){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
        }else {
            showApk();
        }
    }
    private void clickUninstall(){
        uninstall.setSelected(true);
        recommend.setSelected(false);
        local.setSelected(false);
        recommend.setTextColor(getResources().getColor(R.color.white));
        local.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.black));
        if (appsAdapter!=null&&apps.size()>0&&!isLoadLocal){
            mHandler.sendEmptyMessage(0);
            if (apps.size()==0){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
        }else {
            initTvApp();
        }
    }
    private void initTvState(){

        if (MyData.isConnect) {
            isLoadLocal = false;
            final String comment = changeToUnicode("\"RequestAllApps\"");

            RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                @Override
                public void resultHandle(String result) {
                    handResult(result);
                }
            });
        }else {
            isLoadLocal = true;
            boolean isDataSave = preferences.getBoolean("data_save_tvapp", false);
            if (isDataSave) {
                synchronized (appsAdapter) {
                    List<TvApp> tvApps = DataSupport.findAll(TvApp.class);
                    apps.clear();
                    apps.addAll(tvApps);
                    TvAppSort.sort(apps);
                    isTvAppOk = true;
                    mHandler.sendEmptyMessage(1);
                    mHandler.sendEmptyMessage(0);
                }
            }else {
                String info = getString(R.string.def_tvapp_info);
                handResult(info);
            }
        }
    }
    private void initTvApp(){
        progressBar.setVisibility(View.VISIBLE);
        page = 3;
        initTvState();
    }
    private void showApk() {
        progressBar.setVisibility(View.VISIBLE);
        page = 2;
        isDataSave = preferences.getBoolean("data_save", false);
        if (isDataSave) {
            if (localApps==null){
                localApps = DataSupport.findAll(ApkFile.class);
                for (int i = 0;i<localApps.size();i++){
                    ApkFile apk = localApps.get(i);
//                    Log.i(TAG,"apkFile.getFileName()="+apk.getFileName());
                    File file = new File(apk.getFilePath());
                    if (!file.exists()){
                        DataSupport.deleteAll(ApkFile.class,"filePath = ?",apk.getFilePath());
                    }else {
                        dataList.add(file.getName());
                    }
                }
                localApps= DataSupport.findAll(ApkFile.class);

            }
            adapter = new ApkAdapter(getContext(), R.layout.apk_list_item, localApps);
            adapter.setOnApkInstallClickListener(new ApkAdapter.OnApkInstallClickListener() {
                @Override
                public void installClick(final ApkFile apkFile) {
                    Log.i(TAG,"apkFile.getFileName()="+apkFile.getFileName());
                    if (MyData.isConnect&&apkFile.getInstallLevel()>1){
                        PopupUtil.buildConfirmPopup(getContext(), "确认安装？", apkFile.getFileName(), "安装", "取消", true, new ConfirmPopu.OnOkListener() {
                            @Override
                            public void onOkClick(boolean isOk, boolean isAllCheck) {
                                if (isOk){
                                    final String apkPath = apkFile.getFilePath();
                                    showProgressDialog(apkFile.getFileName());
                                    new Thread(){
                                        @Override
                                        public void run(){
                                            File file = new File(apkPath);
                                            if(file!=null)
                                            {
                                                int res = UploadUtil.uploadFile( file, MyData.installUrl,"");
                                                Log.i("tag","request="+res);
                                                if (res==200){
                                                    apkFile.setInstallLevel(1);
                                                    mHandler.sendEmptyMessage(3);
                                                }else {
                                                    mHandler.sendEmptyMessage(4);
                                                }

                                            }
                                        }
                                    }.start();
                                }
                                PopupUtil.forceDismissPopup();
                            }
                        }).show(listView);
                    }
                }
            });
            mHandler.sendEmptyMessage(0);
            new Thread(){
                @Override
                public void run(){
                    ApkUtil.getApksInfo(context,localApps);
                    isLocalOk = true;
                    mHandler.sendEmptyMessage(0);
                    mHandler.sendEmptyMessage(1);
                }
            }.start();
//            Log.i("apks",localApps.size()+"");
            if (isFirstOpen) {
                isFirstOpen = false;
                File file = Environment.getExternalStorageDirectory(); //从SD的根目录开始
                files = file.listFiles();     //本方法返回该文件夹展开后的所有文件的数组
                if (files != null) {
                    if (!isFinding) {
                        isFinding = true;
                        new Thread() {
                            @Override
                            public void run() {
                                findApks = new ArrayList<ApkFile>();
                                findApk(files, false);
                                isFinding = false;
                                synchronized (adapter) {
                                    localApps.clear();
                                    localApps.addAll(findApks);
                                    ApkUtil.getApksInfo(context, localApps);
                                    isLocalOk = true;
                                    mHandler.sendEmptyMessage(0);

                                    mHandler.sendEmptyMessage(1);
                                }

                            }
                        }.start();
                    }
                }
            }else {

            }
        } else {

            localApps = new ArrayList<>();
            adapter = new ApkAdapter(getContext(),R.layout.apk_list_item, localApps);

//            listView.setAdapter(adapter);
            qurryApk();
        }
    }
    public boolean fragmentBack(){
        return false;
    }
    public void qurryApk(){
//        localApps.clear();
//        adapter.notifyDataSetChanged();
//        progressBar.setVisibility(View.VISIBLE);
        File file = Environment.getExternalStorageDirectory(); //从SD的根目录开始
        files = file.listFiles();     //本方法返回该文件夹展开后的所有文件的数组
        if (files != null) {
            if (!isFinding) {
                isFinding = true;
                new Thread() {
                    @Override
                    public void run() {
                        findApks = new ArrayList<ApkFile>();
                        findApk(files, true);
                        isFinding = false;
                        //localApps.addAll(findApks);
                        ApkUtil.getApksInfo(context, localApps);
                        mHandler.sendEmptyMessage(0);
                        isLocalOk = true;
                        mHandler.sendEmptyMessage(1);
                    }
                }.start();
            }
        }

    }
    private void findApk(File[] files,boolean isFirst) {
//        Log.i("findApk",localApps.size()+"");
        for(File f:files){//遍历展开后的文件夹的文件
            if(f.isDirectory()){//如果是文件夹，继续展开
                File[] filess = f.listFiles();
                if (filess!=null)findApk(filess,isFirst);//用递归递归
            }else if(FileUtil.isApk(f)){
                ApkFile apkFile = new ApkFile(f.getName(),f.getPath(),FileUtil.getSize(f));
//                ApkUtil.getInfo(context,apkFile);
                if (isFirst){
                    if (!localApps.contains(apkFile)){
                        synchronized (adapter){
                            localApps.add(apkFile);
                        }
                        mHandler.sendEmptyMessage(0);
                        if (isDataSave) {
                            DataSupport.deleteAll(ApkFile.class, "filePath = ?", f.getPath());
                        }
                        apkFile.save();
                    }
                }else {
                    if (!findApks.contains(apkFile)) {
                        synchronized (adapter) {
                            findApks.add(apkFile);
                        }
//                    mHandler.sendEmptyMessage(0);
                        if (isDataSave) {
                            DataSupport.deleteAll(ApkFile.class, "filePath = ?", f.getPath());
                        }
                        apkFile.save();
//                        Log.i(TAG,"localApps.size("+DataSupport.findAll(ApkFile.class).size());
                    }
                }
            }
        }
        if (localApps.size()>0){
            editor.putBoolean("data_save", true);
            editor.apply();
        }
    }
    private void downloadPic(){
        isDownload = true;
        String name = downApps.get(0)+".jpg";
        String path = picDownPath+File.separator+name;
        File dir = new File(MyData.picIconSavePath);
        dir.mkdirs();
        BoxFile boxFile = new BoxFile();
        boxFile.setFilePath(path);
        try {
            if (MyData.downUrl !=null)
                mHttpUri = new URI(MyData.downUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mHttpTask = new HTTPFileDownloadTask(boxFile, mHttpClient, mHttpUri, MyData.picIconSavePath, name, 1);
        mHttpTask.setProgressHandler(mHttpDownloadHandler);
        mHttpTask.start();
    }

    private void handResult(String result){
        if (result.equals("null")) {
            mHandler.sendEmptyMessage(0);
            return;
        }
        boolean isDataSave = preferences.getBoolean("data_save_tvapp", false);
        synchronized (appsAdapter) {
            String[] files = result.split("\"type=\"");
            picDownPath = files[0];
            apps.clear();
            for (int i = 1; i < files.length; i++) {
                String[] fil = files[i].split("\"label=\"");
                String type = fil[0];
                String[] fi = fil[1].split("\"pckn=\"");
                String label = fi[0];
                String[] f = fi[1].split("\"verC=\"");
                String pckn = f[0];
                String[] f2 = f[1].split("\"verN=\"");
                int verC = Integer.parseInt(f2[0]);
                String verN = f2[1];
//                Log.i(TAG, "pckn=" + pckn + "verC=" + verC);
                TvApp app = new TvApp(type, label, pckn, verC, verN);
                if (isDataSave)
                DataSupport.deleteAll(TvApp.class, "packageName = ?", app.getPackageName());
                app.save();
                editor.putBoolean("data_save_tvapp", true);
                editor.apply();
                apps.add(app);
                File file = new File(MyData.picIconSavePath + File.separator + pckn + ".jpg");
                //Log.i(TAG, "pckn=" + pckn);
                if (!downApps.contains(app) && !file.exists()) {
                    downApps.add(app.getPackageName());
                }
            }
            TvAppSort.sort(apps);
        }
        isTvAppOk = true;
        mHandler.sendEmptyMessage(1);
        if (!isDownload&&downApps.size()>0) {
            downloadPic();
        }

        mHandler.sendEmptyMessage(0);
    }
    private void initLocalState(){
//        Log.i(TAG,"initLocalState");
        if (localApps.size()>0&&apps.size()>0){
            new Thread(){
                @Override
                public void run(){
                    synchronized (localApps) {
                        ApkUtil.isApkInstalls(localApps, apps);
                        mHandler.sendEmptyMessage(0);
                    }

                }
            }.start();
        }
    }
    private String changeToUnicode(String str){
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

    private void showProgressDialog(String name){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在安装，请稍后……");
        progressDialog.setMessage(name);
        progressDialog.show();
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
//                    progressBar.setVisibility(View.GONE);
//                    listView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    if (page == 2) {
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else if (page == 3){
                        listView.setAdapter(appsAdapter);
                        appsAdapter.notifyDataSetChanged();
                    }

                    break;
                case 1:
                    if (isLocalOk&&isTvAppOk)
                    initLocalState();
                    break;
//                case 2:
//                    Toast.makeText(getContext(),installResult,Toast.LENGTH_SHORT).show();
//                    break;
                case 3:
                    progressDialog.dismiss();
                    initTvState();
                    Toast.makeText(getContext(),"安装成功",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"安装失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };
    private class HTTPdownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int whatMassage = msg.what;
            switch(whatMassage) {
                case HTTPFileDownloadTask.PROGRESS_DOWNLOAD_COMPLETE : {
                    mHandler.sendEmptyMessage(0);
                    downApps.remove(0);
                    if (downApps.size()>0) {
                        downloadPic();
                    }else {
                        isDownload = false;
                    }
                }
                break;
                default:
                    break;
            }
        }
    }
}
