package com.ider.yzg.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by Eric on 2017/12/13.
 */

public class ConfirmPopu  {

    private Context context;
    private PopupWindow popupWindow;
    private RelativeLayout outsideRelative;
    private LinearLayout innerLinear,allCheckLinear;
    private TextView title,notice;
    private CheckBox allCheck;
    private Button ok,cancel;
    private boolean outsideTouchable;

    public ConfirmPopu(Context context,String titleStr,String noticeStr,String okStr,String cancelStr,boolean outsideTouchable,OnOkListener listener){
        this.context = context;
        this.outsideTouchable = outsideTouchable;
        View view = getView(listener);
        title.setText(titleStr);
        notice.setText(noticeStr);
        ok.setText(okStr);
        cancel.setText(cancelStr);
        popupWindow = new PopupWindow(view,-1,-1);
    }
    private View getView(final OnOkListener listener){
        View view = View.inflate(context, R.layout.confirm_popu, null);
        outsideRelative = (RelativeLayout)view.findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) view.findViewById(R.id.inner_linear);
        allCheckLinear = (LinearLayout)view.findViewById(R.id.all_select);
        title = (TextView)view.findViewById(R.id.title);
        notice = (TextView)view.findViewById(R.id.file_name);
        allCheck = (CheckBox)view.findViewById(R.id.all_select_check);
        cancel = (Button)view.findViewById(R.id.cancel_action);
        ok = (Button)view.findViewById(R.id.ok_action);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOkClick(false,allCheck.isChecked());
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOkClick(true,allCheck.isChecked());
            }
        });
        return view;
    }
    public void show(View parent){
        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }
    public void setNotice(String noticeStr){
        notice.setText(noticeStr);
    }
    public void setAllCheckLinearVisible(int visible){
        allCheckLinear.setVisibility(visible);
    }

    public interface OnOkListener{
        void onOkClick(boolean isOk,boolean isAllCheck);
    }
}
