package com.ider.yzg.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.yzg.MainActivity;
import com.ider.yzg.R;
import com.ider.yzg.db.MyData;
import com.ider.yzg.util.TextUtil;

import static android.R.attr.visibility;

/**
 * Created by Eric on 2017/12/25.
 */

public class NoticeBar extends LinearLayout {

    private String TAG = "NoticeBar";
    private Context context;
    private TextView noticeText;
    private boolean isAuto = true;
    public NoticeBar(Context context) {
        this(context, null);
    }
    public NoticeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.notice_bar, this);
        noticeText = (TextView)findViewById(R.id.notice_text);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyData.isConnect){
                    ((MainActivity)getContext()).connecting();
                }
            }
        });
        if (MyData.isConnect){
            setVisibility(GONE);
        }

    }
    public void setText(String text){
        noticeText.setText(text);
    }
    public NoticeBar setVisibility(boolean isVisiable){
        if (isVisiable) {
            isAuto = false;
            setVisibility(VISIBLE);
        }else {
            isAuto = true;
            setVisibility(GONE);
            noticeText.setText(getResources().getString(R.string.disconnect_notice));
        }
        return this;
    }
    public void setVISIBLE(){
        if (isAuto) {
            setVisibility(VISIBLE);
        }
    }
    public void setGONE(){
        if (isAuto) {
            setVisibility(GONE);
        }
    }



}
