package com.ider.yzg.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.ider.yzg.popu.PopupUtil;
import com.ider.yzg.popu.ProgressPopup;
import com.ider.yzg.util.CopyUtil;
import com.ider.yzg.util.DownloadUtil;
import com.ider.yzg.util.FileFind;
import com.ider.yzg.util.FileUtil;
import com.ider.yzg.util.FindUtil;
import com.ider.yzg.util.FragmentInter;
import com.ider.yzg.util.ListSort;
import com.ider.yzg.util.MoveUtil;
import com.ider.yzg.util.RequestUtil;
import com.ider.yzg.util.UploadUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.ider.yzg.db.MyData.fileSelect;


/**
 * Created by Eric on 2017/11/30.
 */

public class TransportFragment extends Fragment implements View.OnClickListener,FragmentInter {
    private String TAG = "TransportFragment";
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private InputMethodManager imm;

    private OperateBar operateBar;
    private TextView tvbox, mobile;
    private LinearLayout disConnectLinear,pathLinearView;
    private TextView pathTextView;
    private ProgressDialog progressDialog;
    private OkHttpClient okHttpClient;
    private ProgressBar progressBar;
    private ListView listView;
    private EditView editView;
    private FileAdapter adapter,boxAdapter;
    private String fileName;
    private int copySize;
    private boolean isLoadLocal;
    private boolean isIniting = false,isClick=false;
    private boolean isHandle;
    private BoxFile openOpBoxFile;
    private List<BoxFile> toCopyFiles = new ArrayList<>();

    private List<BoxFile> moFiles = new ArrayList<>();
    private List<BoxFile> moSelectFiles = new ArrayList<>();
    private List<BoxFile> overWriteFiles;
    private List<String> diskPath = new ArrayList<>();
    private String moveToPath,copyAtPath,selectPath;
    private long dirAvaSize;
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
        editView = (EditView)view.findViewById(R.id.edit_view);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        preferences = context.getSharedPreferences("yzg_prefers", Context.MODE_PRIVATE);
        editor = preferences.edit();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        okHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        setListener();
        if (adapter==null){
            adapter = new FileAdapter(context,R.layout.file_list_item,moFiles,moSelectFiles);
            adapter.setOnMenuOpClickListener(new FileAdapter.OnMenuOpClickListener() {
                @Override
                public void menuClick(BoxFile boxFile,boolean isOpOpen,boolean isCopy, boolean isRename, boolean isRemove,
                                      boolean isMove, boolean isTrans) {
                    if (isOpOpen){
                        operateBarAdapterClick(boxFile);
                    }
                    operateBarOnClick(false,isCopy,isRename,isRemove,isMove,isTrans,false,false);
                }
            });
            boxAdapter = new FileAdapter(getContext(), R.layout.box_file_list_item, MyData.boxFiles, MyData.selectBoxFiles);
            boxAdapter.setOnMenuOpClickListener(new FileAdapter.OnMenuOpClickListener() {
                @Override
                public void menuClick(BoxFile boxFile,boolean isOpOpen,boolean isCopy, boolean isRename, boolean isRemove,
                                      boolean isMove, boolean isTrans) {
                    if (isOpOpen){
                        operateBarAdapterClick(boxFile);
                    }
                    operateBarOnClick(false,isCopy,isRename,isRemove,isMove,isTrans,false,false);
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
                if (isIniting){
                    return;
                }
                if (page==1) {
                    if (!MyData.isShowCheck) {
                        if (MyData.isConnect) {
                            BoxFile boxFile = MyData.boxFiles.get(position);
                            if (boxFile.getFileType() == 0){
                                MyData.boxFilePath = boxFile.getFilePath();
                                if (MyData.disPlayMode.equals(MyData.MOVE)){
                                    moveToPath = boxFile.getFilePath();
                                }else {
                                    if (moveToPath ==null){
                                        moveToPath = boxFile.getFilePath();
                                    }
                                    copyAtPath = boxFile.getFilePath();
                                }
                                init();
                            }else if (boxFile.getFileType() == 1) {
                                fileName = boxFile.getFileName();
                                dirAvaSize = boxFile.getFileSize();
                                if (MyData.boxFilePath.equals("/")) {
                                    MyData.boxFilePath = MyData.boxFilePath + fileName;
                                } else {
                                    MyData.boxFilePath = MyData.boxFilePath + "/" + fileName;
                                }
                                init();
                            } else {
//                                MyData.selectBoxFiles.clear();
//                                MyData.selectBoxFiles.add(boxFile);
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
                        notifyChanged();
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
                        notifyChanged();
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MyData.isShowCheck = true;
                operateBar.setVisibility(View.VISIBLE);
                operateBar.setAllcheck(false);
                if (page==1){
                    operateBar.showNoCheckMenu(true);
                    selectPath = MyData.boxFilePath;
                    boxAdapter.notifyDataSetChanged();
                }else {
                    operateBar.showNoCheckMenu(false);
                    selectPath = MyData.fileSelect.getPath();
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
                operateBarOnClick(isAllCheck,isCopy,isRename,isRemove,isMove,isTrans,isNewCreate,isCancel);
            }
        });

    }
    private void operateBarAdapterClick(BoxFile boxFile){
        if (page ==1) {
            if (openOpBoxFile != null && openOpBoxFile.equals(boxFile)) {
                if (boxFile.isOpenOp()) {
                    boxFile.setOpenOp(false);
                    MyData.selectBoxFiles.clear();
                } else {
                    boxFile.setOpenOp(true);
                    MyData.selectBoxFiles.clear();
                    MyData.selectBoxFiles.add(boxFile);
                }
                openOpBoxFile = boxFile;
            } else {
                if (openOpBoxFile != null) {
                    openOpBoxFile.setOpenOp(false);
                }
                openOpBoxFile = boxFile;
                boxFile.setOpenOp(true);
                MyData.selectBoxFiles.clear();
                MyData.selectBoxFiles.add(boxFile);
            }
            boxAdapter.notifyDataSetChanged();
        }else {
            if (openOpBoxFile != null && openOpBoxFile.equals(boxFile)) {
                if (boxFile.isOpenOp()) {
                    moSelectFiles.clear();
                    boxFile.setOpenOp(false);
                } else {
                    boxFile.setOpenOp(true);
                    MyData.selectBoxFiles.clear();
                    MyData.selectBoxFiles.add(boxFile);
                }
                openOpBoxFile = boxFile;
            } else {
                if (openOpBoxFile != null) {
                    openOpBoxFile.setOpenOp(false);
                }
                openOpBoxFile = boxFile;
                boxFile.setOpenOp(true);
                MyData.selectBoxFiles.clear();
                MyData.selectBoxFiles.add(boxFile);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void notifyChanged(){
        if (page == 1){
            if (MyData.selectBoxFiles.size()==0){
                operateBar.showNoCheckMenu(true);
            }else if (MyData.selectBoxFiles.size()==1){
                operateBar.showOneCheckMenu(true);
            }else {
                operateBar.showMoreCheckMenu(true);
            }
            boxAdapter.notifyDataSetChanged();
        }else {
            if (moSelectFiles.size()==0){
                operateBar.showNoCheckMenu(false);
            }else if (moSelectFiles.size()==1){
                operateBar.showOneCheckMenu(false);
            }else {
                operateBar.showMoreCheckMenu(false);
            }
            adapter.notifyDataSetChanged();
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
        if (MyData.boxFiles.size()==0||isLoadLocal||!MyData.disPlayMode.equals(MyData.NORMAL)) {
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
        MyData.boxFiles.clear();
        boxAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
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
    private void operateBarOnClick(boolean isAllCheck, boolean isCopy, boolean isRename, boolean isRemove,
                                   boolean isMove, boolean isTrans, boolean isNewCreate, boolean isCancel){
        if (!MyData.isConnect&&page ==1){
            Toast.makeText(context, context.getString(R.string.disconnect_notice), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isRemove){
            showDeleteConfirm();
        }else if (isTrans){
            trans();
        }else if (isCancel){
            cancel();
        }else if (isRename){
            rename();
        }else if (isCopy){
            copy();
        }else if(isAllCheck){
            allCheck();
        }else if (isMove){
            move();
        }else if (isNewCreate){
            create();
        }
    }
    private void startMove(List<BoxFile> list){
        operateBar.setVisibility(View.GONE);
        if(page == 1) {
            if (list.size() > 0) {
                PopupUtil.getMovePopup(context, new ProgressPopup.OnCancelListener() {
                    @Override
                    public void onCancelClick() {
                        MoveUtil.cancel();
                        PopupUtil.forceDismissPopup();
                        MyData.disPlayMode = MyData.NORMAL;
                        init();
                    }
                }).show(listView);
                FindUtil.findNoDirDownloadBoxFile(list, new FindUtil.FindCompleteListener() {
                    @Override
                    public void complete(long totalBytes, List<BoxFile> list) {
                        if (totalBytes>dirAvaSize){
                            PopupUtil.forceDismissPopup();
                            Toast.makeText(context,getString(R.string.notice_space_not_enough),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                            MyData.disPlayMode = MyData.NORMAL;
                            operateBar.setVisibility(View.GONE);
                            init();
                            return;
                        }
                        if (copyAtPath.equals(moveToPath)) {
                            MoveUtil.startMoveTvFile(list, totalBytes, new MoveUtil.OnCompleteListener() {
                                @Override
                                public void complete() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    MyData.disPlayMode = MyData.NORMAL;
                                    operateBar.setVisibility(View.GONE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            init();
                                        }
                                    }, 1000);
                                }
                            });
                        }else {
                            MoveUtil.startCutTvFile(list, totalBytes, new MoveUtil.OnCompleteListener() {
                                @Override
                                public void complete() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    MyData.disPlayMode = MyData.NORMAL;
                                    operateBar.setVisibility(View.GONE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            init();
                                        }
                                    }, 1000);
                                }
                            });
                        }
                    }
                });
            }else {
                MyData.disPlayMode = MyData.NORMAL;
                initView();
            }
        }
    }
    private void startCopy(List<BoxFile> list){
        operateBar.setVisibility(View.GONE);
        if(page == 1) {
            if (list.size() > 0) {
                PopupUtil.getCopyPopup(context, new ProgressPopup.OnCancelListener() {
                    @Override
                    public void onCancelClick() {
                        CopyUtil.cancel();
                        PopupUtil.forceDismissPopup();
                        MyData.disPlayMode = MyData.NORMAL;
                        init();
                    }
                }).show(listView);
                FindUtil.findNoDirDownloadBoxFile(list, new FindUtil.FindCompleteListener() {
                    @Override
                    public void complete(long totalBytes, List<BoxFile> list) {
                        if (totalBytes>dirAvaSize){
                            PopupUtil.forceDismissPopup();
                            Toast.makeText(context,getString(R.string.notice_space_not_enough),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                            MyData.disPlayMode = MyData.NORMAL;
                            operateBar.setVisibility(View.GONE);
                            init();
                            return;
                        }
                        CopyUtil.startCopyTvFile(list, totalBytes, new CopyUtil.OnCompleteListener() {
                            @Override
                            public void complete() {
                                progressBar.setVisibility(View.VISIBLE);
                                MyData.disPlayMode = MyData.NORMAL;
                                operateBar.setVisibility(View.GONE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        init();
                                    }
                                }, 1000);
                            }
                        });
                    }
                });
            }else {
                MyData.disPlayMode = MyData.NORMAL;
                initView();
            }
        }else {
            PopupUtil.getCopyPopup(context, new ProgressPopup.OnCancelListener() {
                @Override
                public void onCancelClick() {
                    if (page==1) {
                        CopyUtil.cancel();
                    }else {
                        CopyUtil.cancelLocal();
                    }
                    PopupUtil.forceDismissPopup();
                    MyData.disPlayMode = MyData.NORMAL;
                    initView();
                }
            }).show(listView);
            FindUtil.findNoDirUploadBoxFile(list, new FindUtil.FindCompleteListener() {
                @Override
                public void complete(long totalBytes, List<BoxFile> list) {
                    CopyUtil.startCopyLocalFile(list, totalBytes, new CopyUtil.OnCompleteListener() {
                        @Override
                        public void complete() {
                            MyData.disPlayMode = MyData.NORMAL;
                            operateBar.setVisibility(View.GONE);
                            init();
                        }
                    });
                }
            });
        }
    }
    private void startDownload(List<BoxFile> list){
        if (list.size()>0) {
            PopupUtil.getDownloadPopup(context, new ProgressPopup.OnCancelListener() {
                @Override
                public void onCancelClick() {
                    DownloadUtil.cancel();
                    PopupUtil.forceDismissPopup();
                    MyData.disPlayMode = MyData.NORMAL;
                    page = 1;
                    initView();
                }
            }).show(listView);
            PopupUtil.setDownloadTitle(context.getString(R.string.popup_plan_download_title));
            FindUtil.findNoDirDownloadBoxFile(list, new FindUtil.FindCompleteListener() {
                @Override
                public void complete(long totalBytes, List<BoxFile> list) {
                    PopupUtil.setDownloadTitle(context.getString(R.string.popup_download_title));
                    DownloadUtil.startDownload(list,totalBytes,new DownloadUtil.OnCompleteListener() {
                        @Override
                        public void complete() {
                            MyData.disPlayMode = MyData.NORMAL;
                            operateBar.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    init();
                                }
                            }, 1000);
                        }
                    });
                }
            });

        }else {
            MyData.disPlayMode = MyData.NORMAL;
            page = 1;
            initView();
        }
    }
    private void startUpload(List<BoxFile> list){
        if (list.size()>0) {
            FindUtil.findNoDirUploadBoxFile(list, new FindUtil.FindCompleteListener() {
                @Override
                public void complete(long totalBytes, List<BoxFile> list) {
                    if (totalBytes>dirAvaSize){
                        PopupUtil.forceDismissPopup();
                        Toast.makeText(context,getString(R.string.notice_space_not_enough),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                        MyData.disPlayMode = MyData.NORMAL;
                        operateBar.setVisibility(View.GONE);
                        init();
                        return;
                    }
                    UploadUtil.startUpload(list,totalBytes,new UploadUtil.OnCompleteListener() {
                        @Override
                        public void complete() {
                            MyData.disPlayMode = MyData.NORMAL;
                            operateBar.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    init();
                                }
                            }, 1000);
                        }
                    });
                }
            });
            PopupUtil.getUploadPopup(context, new ProgressPopup.OnCancelListener() {
                @Override
                public void onCancelClick() {
                    UploadUtil.cancel();
                    PopupUtil.forceDismissPopup();
                    MyData.disPlayMode = MyData.NORMAL;
                    page = 2;
                    MyData.isShowCheck = true;
                    initView();
                }
            }).show(listView);


        }else {
            MyData.disPlayMode = MyData.NORMAL;
            page = 2;
            initView();
        }
    }
    private void showOverConfirmPopup(final List<BoxFile> list,final List<BoxFile> overList){
        String checkStr = "剩下"+(overList.size()-1)+" 全选：";
        PopupUtil.getOverConfirmPopup(context, overList.get(0).getFileName(), checkStr,new ConfirmPopu.OnOkListener() {
            @Override
            public void onOkClick(boolean isOk, boolean isAllCheck) {
                if (isOk) {
                    if (PopupUtil.isAllCheck()) {
                        overList.clear();
                    } else{
                        overList.remove(0);
                    }
                    PopupUtil.forceDismissPopup();
                    if (overList.size()>0){
                        showOverConfirmPopup(list,overList);
                    }else {
                        if (MyData.disPlayMode.equals(MyData.TRANS)){
                            if (page == 1) {
                                startUpload(list);
                            } else {
                                startDownload(list);
                            }
                        }else if (MyData.disPlayMode.equals(MyData.COPY)){
                            startCopy(list);
                        }else if (MyData.disPlayMode.equals(MyData.MOVE)){
                            startMove(list);
                        }
                    }
                }else {
                    if (PopupUtil.isAllCheck()) {
                        list.removeAll(overList);
                        overList.clear();
                    } else{
                        list.remove(overList.get(0));
                        overList.remove(0);
                    }
                    PopupUtil.forceDismissPopup();
                    if (overList.size()>0){
                        showOverConfirmPopup(list,overList);
                    }else {
                        if (MyData.disPlayMode.equals(MyData.TRANS)){
                            if (page == 1) {
                                startUpload(list);
                            } else {
                                startDownload(list);
                            }
                        }else if (MyData.disPlayMode.equals(MyData.COPY)){
                            startCopy(list);
                        }else if (MyData.disPlayMode.equals(MyData.MOVE)){
                            startMove(list);
                        }
                    }
                }
            }
        }).show(listView);
        if (overList.size()==1){
            PopupUtil.setAllCheckVisible(View.GONE);
        }

    }
    private void showCreateEditPopup(){
        MyData.isShowCheck = false;
        initView();
        editView.show(context.getString(R.string.edit_create_title),new EditView.OnOkClickListener() {
            @Override
            public void click(boolean isOk, String editStr) {
                if (isOk) {
                    if (editStr.equals("")) {
                    } else {
                        createDir(editStr);
                    }
                }
                editView.dismiss();
                imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
            }
        });
        EditText editText = editView.getEditTextView();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
    }
    private void showRenameEditPopup(final BoxFile boxFile){
        MyData.isShowCheck = false;
        initView();
        editView.show(context.getString(R.string.edit_rename_title), boxFile.getFileName(), new EditView.OnOkClickListener() {
            @Override
            public void click(boolean isOk, String editStr) {

                if (isOk) {
                    if (editStr.equals(boxFile.getFileName())) {
                    } else {
                        rename(boxFile, editStr);
                    }
                }
                editView.dismiss();
                imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
            }
        });
        EditText editText = editView.getEditTextView();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
    }
    private void showDeleteConfirm(){
        String noticeStr = "已选";
        if (page == 1){
            noticeStr = noticeStr+getNoticeInfo(MyData.selectBoxFiles);
        }else {
            noticeStr = noticeStr+getNoticeInfo(moSelectFiles);
        }
        PopupUtil.getDeleteConfirmPopup(context, noticeStr, new ConfirmPopu.OnOkListener() {
            @Override
            public void onOkClick(boolean isOk, boolean isAllCheck) {
                if (isOk){
                    delete();
                    MyData.selectBoxFiles.clear();
                    moSelectFiles.clear();
                    PopupUtil.forceDismissPopup();
                    MyData.disPlayMode = MyData.NORMAL;
                    initView();
                }else {
                    PopupUtil.forceDismissPopup();
                }
            }
        }).show(listView);
    }
    private void allCheck(){
        if (page==1) {
            if (operateBar.isAllCheck()) {
                operateBar.setAllcheck(false);
                MyData.selectBoxFiles.clear();
            } else {
                operateBar.setAllcheck(true);
                MyData.selectBoxFiles.clear();
                MyData.selectBoxFiles.addAll(MyData.boxFiles);
            }
        }else {
            if (operateBar.isAllCheck()) {
                operateBar.setAllcheck(false);
                moSelectFiles.clear();
            } else {
                operateBar.setAllcheck(true);
                moSelectFiles.clear();
                moSelectFiles.addAll(moFiles);
            }
        }
        notifyChanged();
    }
    private void cancel(){
        if (MyData.disPlayMode.equals(MyData.TRANS)) {
            if (page == 1) {
                MyData.disPlayMode = MyData.NORMAL;
                page = 2;
                initView();
            } else {
                MyData.disPlayMode = MyData.NORMAL;
                page = 1;
                initView();
            }
        }else if (MyData.disPlayMode.equals(MyData.COPY)){
            MyData.disPlayMode = MyData.NORMAL;
            init();
        }
    }
    private void move() {
        if (MyData.disPlayMode.equals(MyData.NORMAL)){
            MyData.disPlayMode = MyData.MOVE;
            MyData.isShowCheck = false;
            initView();
            operateBar.showMoveMenu();
        }else {
            final List<BoxFile> list = new ArrayList<>();
            final List<BoxFile> overList = new ArrayList<>();
            if (page ==1) {
                list.addAll(MyData.selectBoxFiles);
                MyData.selectBoxFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.boxFilePath);
                    if (MyData.boxFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if (overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startMove(list);
                }
            }else {
                list.addAll(moSelectFiles);
                moSelectFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.fileSelect.getPath());
                    if (moFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if ( overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startMove(list);
                }
            }
        }
    }
    private void copy() {
        if (MyData.disPlayMode.equals(MyData.NORMAL)){
            MyData.disPlayMode = MyData.COPY;
            MyData.isShowCheck = false;
            initView();
            operateBar.showCopyMenu();
        }else {
            final List<BoxFile> list = new ArrayList<>();
            final List<BoxFile> overList = new ArrayList<>();
            if (page ==1) {
                list.addAll(MyData.selectBoxFiles);
                MyData.selectBoxFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.boxFilePath);
                    if (MyData.boxFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if (overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startCopy(list);
                }
            }else {
                list.addAll(moSelectFiles);
                moSelectFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.fileSelect.getPath());
                    if (moFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if ( overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startCopy(list);
                }
            }
        }
    }
    private void trans(){
        if (MyData.disPlayMode.equals(MyData.NORMAL)){
            MyData.disPlayMode=MyData.TRANS;
            if (page==2) {
                page = 1;
            }else {
                page = 2;
            }
            initView();
            operateBar.showTransMenu();
        }else {
            final List<BoxFile> list = new ArrayList<>();
            final List<BoxFile> overList = new ArrayList<>();
            if (page ==1) {
                list.addAll(moSelectFiles);
                moSelectFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.boxFilePath);
                    if (MyData.boxFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if (overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startUpload(list);
                }
            }else {
                list.addAll(MyData.selectBoxFiles);
                MyData.selectBoxFiles.clear();
                for (BoxFile boxFile : list) {
                    boxFile.setSavePath(MyData.fileSelect.getPath());
                    if (moFiles.contains(boxFile)){
                        overList.add(boxFile);
                    }
                    if (MyData.hideFiles.contains(boxFile)) {
                        overList.add(boxFile);
                    }
                }
                if ( overList.size() > 0) {
                    showOverConfirmPopup(list,overList);
                } else {
                    startDownload(list);
                }
            }
        }
    }
    private void delete() {
        if (page==1) {
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
        }else {
            for (int i=0;i<moSelectFiles.size();i++){
                File file = new File(moSelectFiles.get(i).getFilePath());
                if (file.isDirectory()&&file.exists()){
                    FileUtil.dirDelete(file);
                }else {
                    if (file.exists()){
                        file.delete();
                    }
                }
            }
            moSelectFiles.clear();
            init();
        }
    }

    private void create(){
       showCreateEditPopup();
    }
    private void createDir(String dirName) {
        if (page==1) {
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
        }else {
            new File(MyData.fileSelect.getPath()+File.separator+dirName).mkdirs();
            init();
        }
    }
    private void rename(){
        if (page==1) {
            showRenameEditPopup(MyData.selectBoxFiles.get(0));
            MyData.selectBoxFiles.clear();
        }else {
            showRenameEditPopup(moSelectFiles.get(0));
            moSelectFiles.clear();
        }
    }
    private void rename(BoxFile boxFile, String newName) {
        if (page ==1) {
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
        }else {
            File old = new File(boxFile.getFilePath());
            File newFile = new File(old.getParent()+File.separator+newName);
            old.renameTo(newFile);
            adapter.notifyDataSetChanged();
        }
    }





    private void handResult(String result) {
        if (isHandle){
            return;
        }
        isHandle = true;
        if (result.equals("null")) {
            isHandle = false;
            return;
        }
        String[] files = result.split("\"type=\"");
        MyData.boxFilePath = files[0];
        if (MyData.boxFilePath.equals(MyData.root_path)){
            diskPath.clear();
        }
        if (files.length==1){
            mHandler.sendEmptyMessage(0);
            isHandle = false;
            return;
        }
        if (MyData.hideFiles==null){
            MyData.hideFiles=new ArrayList<>();
        }
        MyData.hideFiles.clear();
        boolean isDataSave = preferences.getBoolean("data_save_boxfile",false);
        boolean firstPath ;
        if (MyData.boxFilePath.equals("/storage/emulated/0")){
            if (isDataSave)
                DataSupport.deleteAll(BoxFile.class);
            firstPath = true;
        }else {
            firstPath = false;
        }
        synchronized (boxAdapter) {
            MyData.boxFiles.clear();

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
                    diskPath.add(names[1]);
                }else if (type==1){
                    String[] sizes = fi[0].split("\"count=\"");
                    size = Long.parseLong(sizes[0]);
                    boxFile = new BoxFile(type, fis[0], time, size, MyData.boxFilePath + "/" + fis[0]);
                    boxFile.setFileCount(Integer.parseInt(sizes[1]));
                }else {
                    size = Long.parseLong(fi[0]);
                    boxFile = new BoxFile(type, fis[0], time, size, MyData.boxFilePath + "/" + fis[0]);
                }
                if (firstPath) {
                    boxFile.save();
                    editor.putBoolean("data_save_boxfile", true);
                    editor.apply();
                }
                if (type==1||MyData.disPlayMode.equals(MyData.NORMAL)) {
                    if (!MyData.selectBoxFiles.contains(boxFile)){
                        MyData.boxFiles.add(boxFile);
                    }else {
                        MyData.hideFiles.add(boxFile);
                    }

                }else {
                    MyData.hideFiles.add(boxFile);
                }
            }
            ListSort.sort(MyData.boxFiles);
        }
//        if (!MyData.disPlayMode.equals(MyData.NORMAL)){
//
//        }
//        boxAdapter.notifyDataSetChanged();
//        listView.setSelection(0);
//        progressBar.setVisibility(View.GONE);
        mHandler.sendEmptyMessage(0);
        isHandle = false;
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

    private String getNoticeInfo(List<BoxFile> list){
        String noticeStr;
        if (list.size()==1){
            noticeStr = list.get(0).getFileName();
        }else {
            String str = context.getResources().getString(R.string.popup_more_select_notice);
            noticeStr = String.format(str,list.size());
        }
        return noticeStr;
    }




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



    Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
//                    filePath.setText(MyData.boxFilePath);
                    if (page==1) {
                        listView.setAdapter(boxAdapter);
                        pathTextView.setText(MyData.boxFilePath);
                        if (MyData.boxFilePath.equals(MyData.root_path)){
                            pathTextView.setText("已挂载设备");
                        }
                        if (MyData.boxFiles.size() == 0) {
//                        menuRel.setVisibility(View.GONE);
                            MyData.isShowCheck = false;
                        }
                    }else {
                        pathTextView.setText(MyData.fileSelect.getPath());
                        listView.setAdapter(adapter);
                    }

                    progressBar.setVisibility(View.GONE);
                    break;
                case 1:
                    if (progressDialog!=null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case 2:
//                    if (MyData.copingFiles.size()>0) {
//                        progressDialog.setMessage(MyData.copingFiles.get(0).getFileName());
//                    }
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
        }else if (editView.isShowing()){
            editView.dismiss();
            return true;
        }
        if (page==1) {
            if (!MyData.isShowCheck) {
                if (MyData.isConnect) {
                    if (MyData.boxFilePath.equals(MyData.root_path) || MyData.boxFilePath.equals("")) {
                        if (!MyData.disPlayMode.equals(MyData.NORMAL)) {
                            MyData.disPlayMode = MyData.NORMAL;
                            page = 2;
                            initView();
                            return true;
                        }
                        return false;
                    }
                    if (diskPath.contains(MyData.boxFilePath)){
                        MyData.boxFilePath = MyData.root_path;
                    }else if (MyData.boxFilePath.lastIndexOf(File.separator) == 0) {
                        MyData.boxFilePath = File.separator;
                    } else {
                        MyData.boxFilePath = MyData.boxFilePath.substring(0, MyData.boxFilePath.lastIndexOf(File.separator));
                    }
                    Log.i("MyData.boxFilePath", MyData.boxFilePath);
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