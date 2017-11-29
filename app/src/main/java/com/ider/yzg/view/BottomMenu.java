package com.ider.yzg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;

/**
 * Created by Eric on 2017/10/26.
 */

public class BottomMenu extends LinearLayout {

    private boolean isSelect = false;

    private ImageView image;
    private TextView text;

    public BottomMenu(Context context) {
        this(context, null);
    }
    public BottomMenu (Context context, AttributeSet attrs){
        super(context,attrs);
        this.setFocusable(true);
        LayoutInflater.from(context).inflate(R.layout.bottom_menu, this);
        image = (ImageView)findViewById(R.id.image);
        text = (TextView)findViewById(R.id.text);
        freshView();
    }

    public void freshView(){
        if (getTag().equals("1")){
            if (isSelect){
                image.setImageResource(R.drawable.application_white);
                text.setTextColor(getResources().getColor(R.color.white));
            }else {
                image.setImageResource(R.drawable.application_grey);
                text.setTextColor(getResources().getColor(R.color.grey_text_bottom));
            }
            text.setText(R.string.appliction);
        }else if (getTag().equals("2")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.white));
                image.setImageResource(R.drawable.remote_white);
            }else {
                text.setTextColor(getResources().getColor(R.color.grey_text_bottom));
                image.setImageResource(R.drawable.remote_grey);
            }
            text.setText(R.string.remote);
        }else if (getTag().equals("3")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.white));
                image.setImageResource(R.drawable.transmitter_white);
            }else {
                text.setTextColor(getResources().getColor(R.color.grey_text_bottom));
                image.setImageResource(R.drawable.transmitter_grey);
            }
            text.setText(R.string.transmitter);
        }else if (getTag().equals("4")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.white));
                image.setImageResource(R.drawable.tool_white);
            }else {
                text.setTextColor(getResources().getColor(R.color.grey_text_bottom));
                image.setImageResource(R.drawable.tool_grey);
            }
            text.setText(R.string.tool);
        }
    }
    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
        freshView();
    }
}
