package com.ider.yzg.popu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.view.ConfirmPopu;

/**
 * Created by Eric on 2017/12/18.
 */

public class EditPopup {
    private Context context;
    private PopupWindow popupWindow;
    private RelativeLayout outsideRelative;
    private LinearLayout innerLinear;
    private TextView title;
    private EditText editText;
    private Button ok,cancel;
    private boolean outsideTouchable;
    private boolean cancelable = true;
    private OnOkListener listener;

    public EditPopup(Context context,String titleStr,String editStr,String okStr,
                        String cancelStr,boolean outsideTouchable,EditPopup.OnOkListener listener){
        this.context = context;
        this.outsideTouchable = outsideTouchable;
        this.listener = listener;
        View view = getView();
        title.setText(titleStr);
        editText.setText(editStr);
        ok.setText(okStr);
        cancel.setText(cancelStr);
        popupWindow = new PopupWindow(view,-1,-1);
    }

    private View getView(){
        View view = View.inflate(context, R.layout.edit_popup, null);
        outsideRelative = (RelativeLayout)view.findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) view.findViewById(R.id.inner_linear);
        title = (TextView)view.findViewById(R.id.title);
        editText = (EditText)view.findViewById(R.id.edit_text);
        cancel = (Button)view.findViewById(R.id.cancel_action);
        ok = (Button)view.findViewById(R.id.ok_action);
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
                listener.onOkClick(false,editText.getText().toString());
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOkClick(true,editText.getText().toString());
            }
        });
        return view;
    }
    public EditPopup setCancelable(boolean cancelable){
        this.cancelable = cancelable;
        return this;
    }
    public void setText(String noticeStr,String checkStr){

    }
    public boolean isCancelable(){
        return this.cancelable;
    }
    public boolean isShowing(){
        return popupWindow.isShowing();
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


    public interface OnOkListener{
        void onOkClick(boolean isOk,String editStr);
    }
}
