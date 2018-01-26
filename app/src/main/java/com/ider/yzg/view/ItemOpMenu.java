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
 * Created by Eric on 2017/12/11.
 */

public class ItemOpMenu extends LinearLayout {
    private boolean isSelect = false;

    private ImageView image;
    private TextView text;

    public ItemOpMenu(Context context) {
        this(context, null);
    }
    public ItemOpMenu (Context context, AttributeSet attrs){
        super(context,attrs);
        this.setFocusable(true);
        LayoutInflater.from(context).inflate(R.layout.item_op_menu, this);
        image = (ImageView)findViewById(R.id.image);
        text = (TextView)findViewById(R.id.text);
        freshView();
    }

    public void freshView(){
        if (getTag().equals("1")){
            if (isSelect){
                image.setImageResource(R.drawable.ic_trans_select);
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
            }else {
                image.setImageResource(R.drawable.ic_trans_unselect);
                text.setTextColor(getResources().getColor(R.color.item_menu_unselect));
            }
            text.setText(R.string.item_trans);
        }else if (getTag().equals("2")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_move_select);
            }else {
                text.setTextColor(getResources().getColor(R.color.item_menu_unselect));
                image.setImageResource(R.drawable.ic_move_unselect);
            }
            text.setText(R.string.item_move);
        }else if (getTag().equals("3")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_rename_select);
            }else {
                text.setTextColor(getResources().getColor(R.color.item_menu_unselect));
                image.setImageResource(R.drawable.ic_rename_unselect);
            }
            text.setText(R.string.item_rename);
        }else if (getTag().equals("4")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_remove_select);
            }else {
                text.setTextColor(getResources().getColor(R.color.item_menu_unselect));
                image.setImageResource(R.drawable.ic_remove_unselect);
            }
            text.setText(R.string.item_remove);
        }else if (getTag().equals("5")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_copy_select);
            }else {
                text.setTextColor(getResources().getColor(R.color.item_menu_unselect));
                image.setImageResource(R.drawable.ic_copy_unselect);
            }
            text.setText(R.string.item_copy);
        }else if (getTag().equals("6")){
            if (isSelect){
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_allcheck_select);
            }else {
                text.setTextColor(getResources().getColor(R.color.item_menu_select));
                image.setImageResource(R.drawable.ic_allcheck_unselect);
            }
            text.setText(R.string.item_allcheck);
        }else if (getTag().equals("7")){
            text.setTextColor(getResources().getColor(R.color.item_menu_select));
            image.setImageResource(R.drawable.ic_cancel);
            text.setText(R.string.item_cancel);
        }else if (getTag().equals("8")){
            text.setTextColor(getResources().getColor(R.color.item_menu_select));
            image.setImageResource(R.drawable.ic_new_create);
            text.setText(R.string.item_new_create);
        }

    }
    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
        freshView();
    }
    public void setText(String str){
        text.setText(str);
    }
    public boolean isSelect(){
        return isSelect;
    }
}
