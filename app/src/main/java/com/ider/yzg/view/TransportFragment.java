package com.ider.yzg.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.yzg.R;
import com.ider.yzg.adapter.FileAdapter;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.db.TvApp;
import com.ider.yzg.popu.PopuUtils;
import com.ider.yzg.popu.PopupDialog;
import com.ider.yzg.popu.PopupUtil;
import com.ider.yzg.popu.Popus;
import com.ider.yzg.util.FileFind;
import com.ider.yzg.util.FragmentInter;

import com.ider.yzg.util.ListSort;
import com.ider.yzg.util.RequestUtil;
import com.ider.yzg.util.TvAppSort;
import com.ider.yzg.util.UploadUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.M;
import static com.ider.yzg.R.id.apps;
import static com.ider.yzg.db.MyData.fileSelect;
import static com.ider.yzg.db.MyData.uploadingFiles;
import static com.ider.yzg.util.SocketClient.mHandler;

/**
 * Created by Eric on 2017/11/30.
 */

public class TransportFragment extends Fragment implements View.OnClickListener,FragmentInter {
    private String TAG = "RemoteFragment";
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private OperateBar operateBar;
    private TextView tvbox, mobile;
    private LinearLayout disConnectLinear,pathLinearView;
    private TextView pathTextView;
    private ProgressDialog progressDialog;
    private OkHttpClient okHttpClient;
    private ProgressBar progressBar;
    private ListView listView;
    private FileAdapter adapter,boxAdapter;
    private String fileName;
    private int copySize;
    private boolean isLoadLocal;
    private boolean isIniting = false;
    private boolean isHandle;
    private BoxFile openOpBoxFile;
    private List<BoxFile> toCopyFiles = new ArrayList<>();

    private List<BoxFile> moFiles = new ArrayList<>();
    private List<BoxFile> moSelectFiles = new ArrayList<>();
    private int page = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport, container, false);
        operateBar = (OperateBar)view.findViewById(R.id.operate_bar);
        disConnectLinear = (LinearLayout)view.findViewById(R.id.notice_linear_layout);
        pathLinearView = (LinearLayout)view.findViewById(R.id.path_linear_view);
        pathTextView = (TextView)view.findViewById(R.id.path_text_view);
        tvbox = (TextView) view.findViewById(R.id.tvbox_button);
        mobile = (TextView) view.findViewById(R.id.mobile_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        preferences = context.getSharedPreferences("yzg_prefers", Context.MODE_PRIVATE);
        editor = preferences.edit();
        okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        setListener();
        if (adapter==null){
            adapter = new FileAdapter(context,R.layout.file_list_item,moFiles,moSelectFiles);
            adapter.setOnMenuOpClickListener(new FileAdapter.OnMenuOpClickListener() {
                @Override
                public void menuClick(View view,BoxFile boxFile) {
                    menuOnClick(view,boxFile);
                }
            });
            boxAdapter = new FileAdapter(getContext(), R.layout.box_file_list_item, MyData.boxFiles, MyData.selectBoxFiles);
            boxAdapter.setOnMenuOpClickListener(new FileAdapter.OnMenuOpClickListener() {
                @Override
                public void menuClick(View view,BoxFile boxFile) {
                    menuOnClick(view,boxFile);
                }
            });
            listView.setAdapter(boxAdapter);
            tvbox.performClick();
        }
        initView();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void fragmentInit() {
        initView();
        if (MyData.boxFiles.size()==0||isLoadLocal)
        init();
    }
    @Override
    public  void fragmentHandleMsg(String msg){
        if (msg.contains("connect_success")) {
            init();

        }else if (msg.contains("connect_failed")) {

        }
        initView();
    }
    private void initView(){
        if (MyData.isConnect){
            disConnectLinear.setVisibility(View.GONE);
        }else {
            disConnectLinear.setVisibility(View.VISIBLE);
        }
        if (page==1){
            clickTvbox();
        }else {
            clickMobile();
        }
        if (MyData.disPlayMode.equals(MyData.NORMAL)){
            operateBar.setVisibility(View.GONE);
        }else {
            operateBar.setVisibility(View.VISIBLE);
        }
    }
    private void setListener() {
        tvbox.setOnClickListener(this);
        mobile.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (page==1) {
                    if (!MyData.isShowCheck) {
                        if (MyData.isConnect) {
                            BoxFile boxFile = MyData.boxFiles.get(position);
                            if (boxFile.getFileType() == 1) {
                                fileName = boxFile.getFileName();
                                if (MyData.boxFilePath.equals("/")) {
                                    MyData.boxFilePath = MyData.boxFilePath + fileName;
                                } else {
                                    MyData.boxFilePath = MyData.boxFilePath + "/" + fileName;
                                }
                                init();
                            } else {
                                MyData.selectBoxFiles.clear();
                                MyData.selectBoxFiles.add(boxFile);
                                //showMenuDialog();
                            }
                        }else {
                            Toast.makeText(context, context.getString(R.string.disconnect_notice), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        BoxFile boxFile = MyData.boxFiles.get(position);
                        if (MyData.selectBoxFiles.contains(boxFile)) {
                            MyData.selectBoxFiles.remove(boxFile);
                        } else {
                            MyData.selectBoxFiles.add(boxFile);
                        }
                        if (MyData.selectBoxFiles.size()==0){
                            operateBar.showNoCheckMenu();
                        }else if (MyData.selectBoxFiles.size()==1){
                            operateBar.showOneCheckMenu();
                        }else {
                            operateBar.showMoreCheckMenu();
                        }
                        boxAdapter.notifyDataSetChanged();
                    }
                }else {
                    BoxFile boxFile = moFiles.get(position);
                    if (!MyData.isShowCheck) {
                        if (boxFile.getFileType() == 1) {
                            moFiles.clear();
                            MyData.fileSelect = new File(boxFile.getFilePath());
                            synchronized (adapter) {
                                FileFind.findFiles(moFiles, fileSelect, mHandler);
                            }
                            Log.i("findFiles", "moFiles.size()="+moFiles.size());
                        } else {

                        }
                    }else {
                        if (moSelectFiles.contains(boxFile)){
                            moSelectFiles.remove(boxFile);
                        }else {
                            moSelectFiles.add(boxFile);
                        }
                        if (moSelectFiles.size()==0){
                            operateBar.showNoCheckMenu();
                        }else if (moSelectFiles.size()==1){
                            operateBar.showOneCheckMenu();
                        }else {
                            operateBar.showMoreCheckMenu();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MyData.isShowCheck = true;
                operateBar.setVisibility(View.VISIBLE);
                operateBar.showNoCheckMenu();
                if (page==1){
                    boxAdapter.notifyDataSetChanged();
                }else {
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        operateBar.setListener(new OperateBar.OnMenuClickListener() {
            @Override
            public void onMenuClick(boolean isAllCheck, boolean isCopy, boolean isRename, boolean isRemove,
                                    boolean isMove, boolean isTrans, boolean isNewCreate, boolean isCancel) {
                Log.i("menuOnClick", isAllCheck+","+isCopy+","+isRename+","+isRemove+","+isMove+","+isTrans+","+isNewCreate+","+isCancel);
                if (isRemove){

                    PopupUtil.buildConfirmPopup(context, context.getResources().getString(R.string.popup_remove_title),
                            getNoticeInfo(), context.getResources().getString(R.string.popup_ok_button),
                            context.getResources().getString(R.string.popup_cancel_button), true, new ConfirmPopu.OnOkListener() {
                        @Override
                        public void onOkClick(boolean isOk, boolean isAllCheck) {
                            Log.i("menuOnClick","onOkClick");
                        }
                    }).show(listView);
                }else if (isTrans){
                    if (MyData.disPlayMode.equals(MyData.NORMAL)){
                        MyData.uploadingFiles.addAll(moSelectFiles);
                        MyData.disPlayMode=MyData.TRANS;
                        if (page==2) {
                            page = 1;
                        }
                        initView();
                        operateBar.showTransMenu();
                    }else {
                        for (BoxFile boxFile:MyData.uploadingFiles){
                            boxFile.setSavePath(MyData.fileSelect.getPath());
                        }
                        UploadUtil.uploading(mHandler);
                        showProgressDialog();
                    }
                }
            }
        });

    }

    private void menuOnClick(View view,BoxFile boxFile) {
        Log.i("menuOnClick", "menuOnClick");
        if (page==1) {
            switch (view.getId()) {
                case R.id.item_menu_op:
                    if (openOpBoxFile != null && openOpBoxFile.equals(boxFile)) {
                        if (boxFile.isOpenOp()) {
                            boxFile.setOpenOp(false);
                        } else {
                            boxFile.setOpenOp(true);
                        }
                        openOpBoxFile = boxFile;
                    } else {
                        if (openOpBoxFile != null) {
                            openOpBoxFile.setOpenOp(false);
                        }
                        openOpBoxFile = boxFile;
                        boxFile.setOpenOp(true);
                    }
                    boxAdapter.notifyDataSetChanged();
                    break;
                case R.id.item_trans:
                    MyData.disPlayMode=MyData.TRANS;
                    page = 2;
                    initView();
                    operateBar.showTransMenu();
                    break;
                case R.id.item_remove:
                    if (page == 1 && !MyData.isConnect) {
                        Toast.makeText(context, context.getString(R.string.disconnect_notice), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MyData.boxFiles.remove(boxFile);
                    mHandler.sendEmptyMessage(0);
                    break;
            }
        }else {
            switch (view.getId()) {
                case R.id.item_menu_op:
                    if (openOpBoxFile != null && openOpBoxFile.equals(boxFile)) {
                        if (boxFile.isOpenOp()) {
                            boxFile.setOpenOp(false);
                        } else {
                            boxFile.setOpenOp(true);
                        }
                        openOpBoxFile = boxFile;
                    } else {
                        if (openOpBoxFile != null) {
                            openOpBoxFile.setOpenOp(false);
                        }
                        openOpBoxFile = boxFile;
                        boxFile.setOpenOp(true);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.item_remove:
                    if (page == 1 && !MyData.isConnect) {
                        Toast.makeText(context, context.getString(R.string.disconnect_notice), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MyData.boxFiles.remove(boxFile);
                    mHandler.sendEmptyMessage(0);
                    break;
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvbox_button:
                page = 1;
                clickTvbox();
                break;
            case R.id.mobile_button:
                page = 2;
                clickMobile();
                break;

        }
    }


    private void clickTvbox() {
        tvbox.setSelected(true);
        mobile.setSelected(false);
        tvbox.setTextColor(getResources().getColor(R.color.black));
        mobile.setTextColor(getResources().getColor(R.color.white));
        if (MyData.boxFiles.size()==0||isLoadLocal) {
            init();
        }else {
            mHandler.sendEmptyMessage(0);
        }
        if (MyData.isShowCheck){
            MyData.isShowCheck = false;
            operateBar.setVisibility(View.GONE);
        }
    }

    private void clickMobile() {
        mobile.setSelected(true);
        tvbox.setSelected(false);
        tvbox.setTextColor(getResources().getColor(R.color.white));
        mobile.setTextColor(getResources().getColor(R.color.black));
        if (moFiles.size()==0||!MyData.disPlayMode.equals(MyData.NORMAL)){
            init();
        }else {
            mHandler.sendEmptyMessage(0);
        }
        if (MyData.isShowCheck){
            MyData.isShowCheck = false;
            operateBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //init();
    }

    private void init() {
        if (isIniting){
            return;
        }
        isIniting = true;
        //progressBar.setVisibility(View.VISIBLE);
        if (page == 1) {
            if (MyData.isConnect) {
                final String comment = changeToUnicode(MyData.boxFilePath);
                isLoadLocal = false;
                MyData.boxFiles.clear();
                RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                    @Override
                    public void resultHandle(String result) {
                        handResult(result);
                    }
                });
            } else {
                isLoadLocal = true;
                boolean isDataSave = preferences.getBoolean("data_save_boxfile", false);
                if (isDataSave) {
                    synchronized (boxAdapter) {
                        List<BoxFile> files = DataSupport.findAll(BoxFile.class);
                        MyData.boxFiles.clear();
                        MyData.boxFiles.addAll(files);
                        ListSort.sort(MyData.boxFiles);
                        mHandler.sendEmptyMessage(0);
                    }

                }else {
                    String info = getString(R.string.def_path_info);
                    handResult(info);
                }

            }
        }else {
            synchronized (adapter) {
                FileFind.findFiles(moFiles, MyData.fileSelect, mHandler);
            }
            Log.i("findFiles", "moFiles.size()="+moFiles.size());

        }
        isIniting = false;
    }

    private void delete() {
        fileName = "\"delete=\"" + MyData.boxFilePath;
        for (int i = 0; i < MyData.selectBoxFiles.size(); i++) {
            fileName = fileName + "name=" + MyData.selectBoxFiles.get(i).getFileName();
        }
        final String comment = changeToUnicode(fileName);
        progressBar.setVisibility(View.VISIBLE);
        MyData.boxFiles.clear();
        boxAdapter.notifyDataSetChanged();
        RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {
                handResult(result);
            }
        });
    }

    private void createDir(String dirName) {
        fileName = "\"createDir=\"" + MyData.boxFilePath;
        String filePath = fileName.endsWith("/") ? (fileName + dirName) : (fileName + "/" + dirName);
        final String comment = changeToUnicode(filePath);
        progressBar.setVisibility(View.VISIBLE);
        MyData.boxFiles.clear();
        //boxAdapter.notifyDataSetChanged();
        RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {
                handResult(result);
            }
        });
    }

    private void rename(BoxFile boxFile, String newName) {
        fileName = "\"reNameFile=\"" + boxFile.getFilePath();
        String filePath = fileName + "\"newName=\"" + newName;
        final String comment = changeToUnicode(filePath);
        progressBar.setVisibility(View.VISIBLE);
        MyData.boxFiles.clear();
        boxAdapter.notifyDataSetChanged();
        RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
            @Override
            public void resultHandle(String result) {
                handResult(result);
            }
        });
    }

    private void copy() {
        if (MyData.copingFiles.size() > 0) {
            BoxFile boxFile = MyData.copingFiles.get(0);
            fileName = "\"copyFile=\"" + boxFile.getFilePath() + "\"newPath=\"" + boxFile.getSavePath() + File.separator + boxFile.getFileName();
            final String comment = changeToUnicode(fileName);
            RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                @Override
                public void resultHandle(String result) {
                    if (result.equals("success")) {
                        MyData.copingFiles.remove(0);
                        mHandler.sendEmptyMessage(2);
                        copy();
                    } else {
                        copy();
                    }
                }
            });
        } else {
            mHandler.sendEmptyMessage(3);
        }
    }

    private void move() {
        if (MyData.copingFiles.size() > 0) {
            BoxFile boxFile = MyData.copingFiles.get(0);
            fileName = "\"moveFile=\"" + boxFile.getFilePath() + "\"newPath=\"" + boxFile.getSavePath() + File.separator + boxFile.getFileName();
            final String comment = changeToUnicode(fileName);
            new Thread() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder().header("comment", comment)
                                .url(MyData.downUrl).build();
                        Call call = okHttpClient.newCall(request);
                        Response response = call.execute();
                        String result = response.body().string();
                        Log.i("result", result);
                        if (result.equals("success")) {
                            MyData.copingFiles.remove(0);
                            mHandler.sendEmptyMessage(2);
                            move();
                        } else {
                            move();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        move();
                    }
                }
            }.start();
        } else {
            mHandler.sendEmptyMessage(3);
        }
    }

    private void handResult(String result) {
        if (isHandle){
            return;
        }
        isHandle = true;
        if (result.equals("null")) {
            return;
        }
        boolean isDataSave = preferences.getBoolean("data_save_boxfile",false);
        boolean firstPath ;
        if (MyData.boxFilePath.equals("")){
            if (isDataSave)
                DataSupport.deleteAll(BoxFile.class);
            firstPath = true;
        }else {
            firstPath = false;
        }
        synchronized (boxAdapter) {
            MyData.boxFiles.clear();
            String[] files = result.split("\"type=\"");
            MyData.boxFilePath = files[0];
            for (int i = 1; i < files.length; i++) {
                String[] fils = files[i].split("\"name=\"");
                int type = Integer.parseInt(fils[0]);
                String[] fis = fils[1].split("\"size=\"");
                String[] fi = fis[1].split("\"time=\"");
                BoxFile boxFile = new BoxFile(type, fis[0], fi[1], fi[0], MyData.boxFilePath + "/" + fis[0]);
                if (firstPath) {
                    boxFile.save();
                    editor.putBoolean("data_save_boxfile", true);
                    editor.apply();
                }
                if (type==1||MyData.disPlayMode.equals(MyData.NORMAL)) {
                    MyData.boxFiles.add(boxFile);
                }
            }
            ListSort.sort(MyData.boxFiles);
        }
        mHandler.sendEmptyMessage(0);
        isHandle = false;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        copySize = MyData.copingFiles.size();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("请稍后……");
        progressDialog.setMax(copySize);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyData.copingFiles.clear();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Request request = new Request.Builder().header("comment", "\"stopCopyFile\"")
                                            .url(MyData.downUrl).build();
                                    Call call = okHttpClient.newCall(request);
                                    Response response = call.execute();
                                    String result = response.body().string();
                                    Log.i("result", result);
//                                    mHandler.sendEmptyMessage(3);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
        //progressDialog.setMessage(MyData.copingFiles.get(0).getFileName());
        progressDialog.show();
    }

    private String changeToUnicode(String str) {
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

    private String getNoticeInfo(){
        String noticeStr;
        if (page==1){
            if (MyData.selectBoxFiles.size()==1){
                noticeStr = MyData.selectBoxFiles.get(0).getFileName();
            }else {
                String str = context.getResources().getString(R.string.popup_more_select_notice);
                noticeStr = String.format(str,MyData.selectBoxFiles.size());
            }

        }else {
            if (moSelectFiles.size()==1){
                noticeStr = moSelectFiles.get(0).getFileName();
            }else {
                String str = context.getResources().getString(R.string.popup_more_select_notice);
                noticeStr = String.format(str,moSelectFiles.size());
            }
        }
        return noticeStr;
    }

//    private void showCreateDirDialog() {
//        View view = View.inflate(getContext(), R.layout.create_dir, null);
//        Popus popup = new Popus();
//        popup.setvWidth(-1);
//        popup.setvHeight(-1);
//        popup.setClickable(true);
//        popup.setAnimFadeInOut(R.style.PopupWindowAnimation);
//        popup.setCustomView(view);
//        popup.setContentView(R.layout.activity_file_select);
//        PopupDialog popupDialog = PopuUtils.createPopupDialog(getContext(), popup);
//        popupDialog.showAtLocation(listView, Gravity.CENTER, 0, 0);
//        final EditText dirName = (EditText) view.findViewById(R.id.dir_name);
//        Button cancel = (Button)view.findViewById(R.id.cancel_action);
//        Button ok = (Button)view.findViewById(R.id.ok_action);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopuUtils.dismissPopupDialog();
//            }
//        });
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopuUtils.dismissPopupDialog();
//                String dir = dirName.getText().toString();
//                createDir(dir);
//            }
//        });
//    }
//
//    private void showRenameDialog(final BoxFile boxFile) {
//        View view = View.inflate(getContext(), R.layout.create_dir, null);
//        Popus popup = new Popus();
//        popup.setvWidth(-1);
//        popup.setvHeight(-1);
//        popup.setClickable(true);
//        popup.setAnimFadeInOut(R.style.PopupWindowAnimation);
//        popup.setCustomView(view);
//        popup.setContentView(R.layout.activity_file_select);
//        PopupDialog popupDialog = PopuUtils.createPopupDialog(getContext(), popup);
//        popupDialog.showAtLocation(listView, Gravity.CENTER, 0, 0);
//        final EditText dirName = (EditText) view.findViewById(R.id.dir_name);
//        dirName.setText(boxFile.getFileName());
//        TextView title = (TextView)view.findViewById(R.id.title);
//        title.setText("请编辑");
//        Button cancel = (Button)view.findViewById(R.id.cancel_action);
//        Button ok = (Button)view.findViewById(R.id.ok_action);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopuUtils.dismissPopupDialog();
//            }
//        });
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopuUtils.dismissPopupDialog();
//                String dir = dirName.getText().toString();
//                rename(boxFile,dir);
//                MyData.selectBoxFiles.clear();
//                MyData.isShowCheck = false;
//                allSelect.setChecked(false);
//                menuRel.setVisibility(View.GONE);
//            }
//        });
//    }
//    private void showDeleteDialog(){
//        View view = View.inflate(getContext(), R.layout.confirm_upload, null);
//        Popus popup = new Popus();
//        popup.setvWidth(-1);
//        popup.setvHeight(-1);
//        popup.setClickable(true);
//        popup.setAnimFadeInOut(R.style.PopupWindowAnimation);
//        popup.setCustomView(view);
//        popup.setContentView(R.layout.activity_file_select);
//        PopupDialog popupDialog = PopuUtils.createPopupDialog(getContext(), popup);
//        popupDialog.showAtLocation(listView, Gravity.CENTER, 0, 0);
//        TextView title = (TextView)view.findViewById(R.id.title);
//        final TextView fileText = (TextView)view.findViewById(R.id.file_name);
//        Button cancel = (Button)view.findViewById(R.id.cancel_action);
//        Button ok = (Button)view.findViewById(R.id.ok_action);
//        title.setText("删除警告！");
//        if (MyData.selectBoxFiles.size()==1){
//            fileText.setText(MyData.selectBoxFiles.get(0).getFileName());
//        }else {
//            fileText.setText("已选择"+MyData.selectBoxFiles.size()+"文件");
//        }
//        cancel.setText("取消");
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopuUtils.dismissPopupDialog();
//            }
//        });
//        ok.setText("删除");
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delete();
//                PopuUtils.dismissPopupDialog();
//                progressBar.setVisibility(View.VISIBLE);
//                MyData.selectBoxFiles.clear();
//                allSelect.setChecked(false);
//                upload.setVisibility(View.GONE);
//            }
//        });
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent intent = new Intent(getContext(), DirSelectActivity.class);
//                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }



    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
//                    filePath.setText(MyData.boxFilePath);
                    if (page==1) {
                        listView.setAdapter(boxAdapter);
                        pathTextView.setText(MyData.boxFilePath);
                        progressBar.setVisibility(View.GONE);
                        if (MyData.boxFiles.size() == 0) {
//                        menuRel.setVisibility(View.GONE);
                            MyData.isShowCheck = false;
                        }
                    }else {
                        pathTextView.setText(MyData.fileSelect.getPath());
                        listView.setAdapter(adapter);
                    }
                    break;
                case 1:
                    if (progressDialog!=null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case 2:
                    if (MyData.copingFiles.size()>0) {
                        progressDialog.setMessage(MyData.copingFiles.get(0).getFileName());
                    }
                    progressDialog.incrementProgressBy(1);
                    break;
                case 3:
                    progressDialog.dismiss();
                    init();
                    break;
                case 4:
                    progressDialog.setTitle("正在取消……");
                    break;
                default:
                    break;
            }

        }
    };
//
//    @Override
//    public void onBackPressed(){
//        if (!MyData.isShowCheck) {
//            if (MyData.boxFilePath.equals(File.separator)||MyData.boxFilePath.equals("")){
//                getActivity().finish();
//                return;
//            }
//            Log.i("MyData.boxFilePath",MyData.boxFilePath);
//            if (MyData.boxFilePath.lastIndexOf(File.separator)==0){
//                MyData.boxFilePath=File.separator;
//            }else {
//                MyData.boxFilePath = MyData.boxFilePath.substring(0,MyData.boxFilePath.lastIndexOf(File.separator));
//            }
//            init();
//        }else {
//            MyData.isShowCheck = false;
//            MyData.selectBoxFiles.clear();
//            upload.setVisibility(View.VISIBLE);
//            allSelect.setChecked(false);
//            menuRel.setVisibility(View.GONE);
//            adapter.notifyDataSetChanged();
//        }
//    }
    public boolean fragmentBack(){
        if (PopupUtil.isPopupShow()){
            PopupUtil.dismissPopup();
            return true;
        }
        if (page==1) {
            if (!MyData.isShowCheck) {
                if (MyData.isConnect) {
                    if (MyData.boxFilePath.equals(File.separator) || MyData.boxFilePath.equals("")) {
                        if (!MyData.disPlayMode.equals(MyData.NORMAL)) {
                            MyData.disPlayMode = MyData.NORMAL;
                            page = 2;
                            initView();
                            return true;
                        }
                        return false;
                    }
                    Log.i("MyData.boxFilePath", MyData.boxFilePath);
                    if (MyData.boxFilePath.lastIndexOf(File.separator) == 0) {
                        MyData.boxFilePath = File.separator;
                    } else {
                        MyData.boxFilePath = MyData.boxFilePath.substring(0, MyData.boxFilePath.lastIndexOf(File.separator));
                    }
                    init();
                    return true;
                }else {
                    if (!MyData.disPlayMode.equals(MyData.NORMAL)) {
                        MyData.disPlayMode = MyData.NORMAL;
                        page = 2;
                        initView();
                        return true;
                    }
                    return false;
                }
            } else {
                MyData.isShowCheck = false;
                operateBar.setVisibility(View.GONE);
                MyData.selectBoxFiles.clear();
                boxAdapter.notifyDataSetChanged();
                return true;
            }
        }else {

            if (!MyData.isShowCheck) {
                if (MyData.fileSelect.equals(Environment.getExternalStorageDirectory())) {
                    if (!MyData.disPlayMode.equals(MyData.NORMAL)){
                        MyData.disPlayMode = MyData.NORMAL;
                        page = 1;
                        initView();
                        return true;
                    }
                    return false;
                } else {
                    moFiles.clear();
                    MyData.fileSelect = MyData.fileSelect.getParentFile();
                    pathTextView.setText(MyData.fileSelect.getPath());
                    synchronized (adapter) {
                        FileFind.findFiles(moFiles, fileSelect, mHandler);
                    }
                    Log.i("findFiles", "moFiles.size()="+moFiles.size());
                }
            }else {
                MyData.isShowCheck = false;
                operateBar.setVisibility(View.GONE);
                moSelectFiles.clear();
                adapter.notifyDataSetChanged();
            }
            return true;
        }
    }
}