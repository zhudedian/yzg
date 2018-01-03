package com.ider.yzg.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.yzg.R;
import com.ider.yzg.db.BoxFile;
import com.ider.yzg.db.MyData;
import com.ider.yzg.popu.PopupUtil;
import com.ider.yzg.util.DownloadUtil;
import com.ider.yzg.util.FragmentInter;
import com.ider.yzg.util.RequestUtil;
import com.ider.yzg.util.TextUtil;

import java.io.File;

import static android.R.attr.name;

/**
 * Created by Eric on 2018/1/2.
 */

public class ToolFragment extends Fragment implements FragmentInter {

    private Context context;
    private LinearLayout disConnectLinear;
    private TextView deviceInfoText;
    private TextView noticeText;
    private ToolView screenShot,screenPic,clean,reboot;
    private boolean isShoting = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_tool,container,false);
        disConnectLinear = (LinearLayout)view.findViewById(R.id.notice_linear_layout);
        deviceInfoText = (TextView)view.findViewById(R.id.device_info_text);
        noticeText = (TextView)view.findViewById(R.id.notice_text);
        screenShot = (ToolView)view.findViewById(R.id.screen_shot);
        screenPic = (ToolView)view.findViewById(R.id.screen_picture);
        clean = (ToolView)view.findViewById(R.id.tool_clean);
        reboot = (ToolView)view.findViewById(R.id.tool_reboot);
        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        initView();
        setListener();
    }
    @Override
    public void fragmentInit() {
        if (isAdded())
        initView();

    }
    @Override
    public  void fragmentHandleMsg(String msg){
        initView();
    }
    public boolean fragmentBack(){
        if (PopupUtil.isPopupShow()){
            PopupUtil.dismissPopup();
            return true;
        }
        return false;
    }
    private void initView(){
        if (MyData.isConnect){
            disConnectLinear.setVisibility(View.GONE);
        }else {
            disConnectLinear.setVisibility(View.VISIBLE);
            noticeText.setText(getString(R.string.disconnect_notice));
        }
        init();
    }
    private void setListener(){
        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShoting){
                    return;
                }
                isShoting = true;
                String comment = "screenshot";
                RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                    @Override
                    public void resultHandle(String result) {
                        handScreenshotResult(result);
                    }
                });
            }
        });
        screenPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtil.getScreenPicPopup(context).show(screenPic);
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = "\"cleanProgress\"";
                RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                    @Override
                    public void resultHandle(String result) {

                    }
                });
            }
        });
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = "\"reboot\"";
                RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                    @Override
                    public void resultHandle(String result) {

                    }
                });
            }
        });
    }
    private void init(){
        if (MyData.isConnect){
            String comment = "\"deviceInfo\"";
            RequestUtil.requestWithComment(comment, new RequestUtil.HandleResult() {
                @Override
                public void resultHandle(String result) {
                    if (!result.equals("")) {
                        deviceInfoText.setText(result);
                        TextUtil.autoSplitText(deviceInfoText,"设备型号：");
                    }
                }
            });
        }else {
            deviceInfoText.setText(getString(R.string.def_device_info));
        }
    }
    private void handScreenshotResult(String result){
        if (result.equals("failed")) {
            isShoting = false;
            Toast.makeText(context,"截图失败！",Toast.LENGTH_SHORT).show();
            return;
        }
        String savePath = MyData.screenshotSavePath;
        File dir = new File(savePath);
        dir.mkdirs();
        String[] files = result.split("\"name=\"");
        final String picDownPath = files[0];
        final String fileName = files[1];
        final BoxFile boxFile = new BoxFile(fileName,picDownPath+File.separator+fileName,savePath);
        Toast.makeText(context,"截图取回本地中……",Toast.LENGTH_LONG).show();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DownloadUtil.download(boxFile, new DownloadUtil.OnCompleteListener() {
                    @Override
                    public void complete() {
                        isShoting = false;
                        PopupUtil.notifyDataChange();
                        Toast.makeText(context,"截图已取回本地！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();

    }
}