package com.ider.yzg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.yzg.R;

/**
 * Created by Eric on 2017/12/13.
 */

public class OperateBar extends LinearLayout {

    private Context context;
    private OnMenuClickListener listener;
    private boolean isAllCheck;
    private ItemOpMenu cancel,newcreate,trans,move,rename,remove,copy,allcheck;

    public OperateBar(Context context) {
        this(context, null);
    }
    public OperateBar (Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.operate_bar, this);
        cancel = (ItemOpMenu)findViewById(R.id.item_cancel);
        newcreate = (ItemOpMenu)findViewById(R.id.item_new_create);
        trans = (ItemOpMenu)findViewById(R.id.item_trans);
        move = (ItemOpMenu)findViewById(R.id.item_move);
        rename = (ItemOpMenu)findViewById(R.id.item_rename);
        remove = (ItemOpMenu)findViewById(R.id.item_remove);
        copy = (ItemOpMenu)findViewById(R.id.item_copy);
        allcheck = (ItemOpMenu)findViewById(R.id.item_allcheck);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&cancel.isSelect()){
                    listener.onMenuClick(false,false,false,false,false,false,false,true);
                }
            }
        });
        newcreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&newcreate.isSelect()){
                    listener.onMenuClick(false,false,false,false,false,false,true,false);
                }
            }
        });
        trans.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&trans.isSelect()){
                    listener.onMenuClick(false,false,false,false,false,true,false,false);
                }
            }
        });
        move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&move.isSelect()){
                    listener.onMenuClick(false,false,false,false,true,false,false,false);
                }
            }
        });
        rename.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&rename.isSelect()){
                    listener.onMenuClick(false,false,true,false,false,false,false,false);
                }
            }
        });
        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&remove.isSelect()){
                    listener.onMenuClick(false,false,false,true,false,false,false,false);
                }
            }
        });
        copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&&copy.isSelect()){
                    listener.onMenuClick(false,true,false,false,false,false,false,false);
                }
            }
        });
        allcheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onMenuClick(true,false,false,false,false,false,false,false);
                }
            }
        });
    }

    public void setAllcheck(boolean allCheck){
        if (allCheck) {
            allcheck.setSelect(true);
        }else {
            allcheck.setSelect(false);
        }
    }
    public boolean isAllCheck(){
        return allcheck.isSelect();
    }

    public void showNoCheckMenu(boolean page1){
        allcheck.setVisibility(VISIBLE);
        copy.setVisibility(VISIBLE);
        remove.setVisibility(VISIBLE);
        rename.setVisibility(VISIBLE);
        if (page1) {
            move.setVisibility(VISIBLE);
        }else {
            move.setVisibility(GONE);
        }
        trans.setVisibility(VISIBLE);
        newcreate.setVisibility(GONE);
        cancel.setVisibility(GONE);

        copy.setSelect(false);
        remove.setSelect(false);
        rename.setSelect(false);
        move.setSelect(false);
        trans.setSelect(false);
    }

    public void showOneCheckMenu(boolean page1){
        allcheck.setVisibility(VISIBLE);
        copy.setVisibility(VISIBLE);
        remove.setVisibility(VISIBLE);
        rename.setVisibility(VISIBLE);
        if (page1) {
            move.setVisibility(VISIBLE);
        }else {
            move.setVisibility(GONE);
        }
        trans.setVisibility(VISIBLE);
        newcreate.setVisibility(GONE);
        cancel.setVisibility(GONE);

        copy.setSelect(true);
        remove.setSelect(true);
        rename.setSelect(true);
        move.setSelect(true);
        trans.setSelect(true);
    }
    public void showMoreCheckMenu(boolean page1){
        allcheck.setVisibility(VISIBLE);
        copy.setVisibility(VISIBLE);
        remove.setVisibility(VISIBLE);
        rename.setVisibility(GONE);
        if (page1) {
            move.setVisibility(VISIBLE);
        }else {
            move.setVisibility(GONE);
        }
        trans.setVisibility(VISIBLE);
        newcreate.setVisibility(GONE);
        cancel.setVisibility(GONE);

        copy.setSelect(true);
        remove.setSelect(true);
        move.setSelect(true);
        trans.setSelect(true);
    }
    public void showTransMenu(){
        allcheck.setVisibility(GONE);
        copy.setVisibility(GONE);
        remove.setVisibility(GONE);
        rename.setVisibility(GONE);
        move.setVisibility(GONE);
        trans.setVisibility(VISIBLE);
        newcreate.setVisibility(VISIBLE);
        cancel.setVisibility(VISIBLE);

        trans.setSelect(true);
    }
    public void showMoveMenu(){
        allcheck.setVisibility(GONE);
        copy.setVisibility(GONE);
        remove.setVisibility(GONE);
        rename.setVisibility(GONE);
        move.setVisibility(VISIBLE);
        trans.setVisibility(GONE);
        newcreate.setVisibility(VISIBLE);
        cancel.setVisibility(VISIBLE);

        move.setSelect(true);
    }
    public void showCopyMenu(){
        setVisibility(VISIBLE);
        allcheck.setVisibility(GONE);
        copy.setVisibility(VISIBLE);
        remove.setVisibility(GONE);
        rename.setVisibility(GONE);
        move.setVisibility(GONE);
        trans.setVisibility(GONE);
        newcreate.setVisibility(VISIBLE);
        cancel.setVisibility(VISIBLE);

        copy.setSelect(true);
    }

    public void setListener(OnMenuClickListener listener){
        this.listener = listener;
    }

    public interface OnMenuClickListener{
        void onMenuClick(boolean isAllCheck,boolean isCopy,boolean isRename, boolean isRemove,boolean isMove,
                         boolean isTrans,boolean isNewCreate,boolean isCancel);
    }
}
