package com.ider.yzg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.yzg.R;

/**
 * Created by Eric on 2018/1/2.
 */

public class ToolView extends LinearLayout {

    private boolean isSelect = false;

    private ImageView image;
    private TextView text;

    public ToolView(Context context) {
        this(context, null);
    }
    public ToolView (Context context, AttributeSet attrs){
        super(context,attrs);
        this.setFocusable(true);
        LayoutInflater.from(context).inflate(R.layout.tool_view, this);
        image = (ImageView)findViewById(R.id.image);
        text = (TextView)findViewById(R.id.text);
        freshView();
    }

    public void freshView(){
        if (getTag().equals("1")){
            image.setImageResource(R.drawable.tool_screenshot);
            text.setText(R.string.screenshot);
        }else if (getTag().equals("2")){
            image.setImageResource(R.drawable.tool_picture);
            text.setText(R.string.screen_picture);
        }else if (getTag().equals("3")){
            image.setImageResource(R.drawable.tool_clean);
            text.setText(R.string.tool_clean);
        }else if (getTag().equals("4")){
            image.setImageResource(R.drawable.tool_reboot);
            text.setText(R.string.tool_reboot);
        }
    }
    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
        freshView();
    }
}
