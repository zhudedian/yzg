package com.ider.yzg.popu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.util.FileUtil;
import com.ider.yzg.view.ConfirmPopu;

import static android.R.attr.max;
import static android.R.string.cancel;

/**
 * Created by Eric on 2017/12/15.
 */

public class ProgressPopup extends PopupWindow {

    private Context context;
    private PopupWindow popupWindow;
    private RelativeLayout outsideRelative;
    private LinearLayout innerLinear;
    private TextView title,parent,rate;
    private CheckBox allCheck;
    private ProgressBar progressBar;
    private Button cancel;
    private boolean outsideTouchable;
    private boolean cancelable = false;

    public ProgressPopup(Context context,String titleStr,boolean outsideTouchable,OnCancelListener listener){
        this.context = context;
        this.outsideTouchable = outsideTouchable;
        View view = getView(listener);
        title.setText(titleStr);
        popupWindow = new PopupWindow(view,-1,-1);
    }
    private View getView(final OnCancelListener listener){
        View view = View.inflate(context, R.layout.progress_popup, null);
        outsideRelative = (RelativeLayout)view.findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) view.findViewById(R.id.inner_linear);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        title = (TextView)view.findViewById(R.id.title);
        parent = (TextView)view.findViewById(R.id.parent);
        rate = (TextView)view.findViewById(R.id.rate);
        cancel = (Button)view.findViewById(R.id.cancel_action);
        outsideRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outsideTouchable){
                    PopupUtil.dismissPopup();
                }
            }
        });
        innerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancelClick();
            }
        });
        return view;
    }
    public ProgressPopup setCancelable(boolean cancelable){
        this.cancelable = cancelable;
        return this;
    }
    public boolean isCancelable(){
        return this.cancelable;
    }
    public boolean isShowing(){
        if (popupWindow!=null) {
            return popupWindow.isShowing();
        }
        return false;
    }
    public void dismiss(){
        if (popupWindow!=null&&popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    public void show(View parent){
        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }
    public void update(long numBytes, long totalBytes,float parents,float speed){
        //if (parent!=null&&progressBar!=null&&rate!=null) {
            parent.setText(FileUtil.getSize(numBytes) + "/" + FileUtil.getSize(totalBytes));
            progressBar.setProgress((int) parents * 100);
            rate.setText(FileUtil.getSize(speed) + "/s");
        //}
    }
    public void setMaxProgress(int maxProgress){
        progressBar.setMax(maxProgress);
    }

    public interface OnCancelListener{
        void onCancelClick();
    }
}
