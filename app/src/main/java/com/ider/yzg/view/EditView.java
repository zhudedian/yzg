package com.ider.yzg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.popu.EditPopup;
import com.ider.yzg.popu.PopupUtil;

import static android.R.string.cancel;
import static android.R.transition.move;

/**
 * Created by Eric on 2017/12/19.
 */

public class EditView extends RelativeLayout {

    private Context context;
    private RelativeLayout outsideRelative;
    private EditView.OnOkClickListener listener;
    private LinearLayout innerLinear;
    private TextView title;
    private EditText editText;
    private Button ok,cancel;
    private boolean isShowing = false;
    private boolean outsideTouchable;


    public EditView(Context context) {
        this(context, null);
    }
    public EditView (Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.edit_view, this);
        outsideRelative = (RelativeLayout)findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) findViewById(R.id.inner_linear);
        title = (TextView)findViewById(R.id.title);
        editText = (EditText)findViewById(R.id.edit_text);
        cancel = (Button)findViewById(R.id.cancel_action);
        ok = (Button)findViewById(R.id.ok_action);
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
                listener.click(false,editText.getText().toString());
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.click(true,editText.getText().toString());
            }
        });
    }
    public void show(String titleStr,String editStr,OnOkClickListener listener){
        this.listener = listener;
        isShowing = true;
        title.setText(titleStr);
        editText.setText(editStr);
        setVisibility(VISIBLE);
    }
    public void show(String titleStr,OnOkClickListener listener){
        this.listener = listener;
        isShowing = true;
        title.setText(titleStr);
        setVisibility(VISIBLE);
    }
    public EditText getEditTextView(){
        return editText;
    }
    public void dismiss(){
        isShowing = false;
        setVisibility(GONE);
    }
    public boolean isShowing(){
        return this.isShowing;
    }
    public interface OnOkClickListener{
        void click(boolean isOk,String editStr);
    }
}
