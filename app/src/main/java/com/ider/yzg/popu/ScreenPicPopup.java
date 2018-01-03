package com.ider.yzg.popu;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.adapter.PicAdapter;
import com.ider.yzg.util.PicUtil;

import static android.R.string.cancel;
import static com.ider.yzg.R.id.parent;
import static com.ider.yzg.R.id.rate;

/**
 * Created by Eric on 2018/1/2.
 */

public class ScreenPicPopup {

    private Context context;
    private PopupWindow popupWindow;
    private RelativeLayout outsideRelative;
    private LinearLayout innerLinear;
    private RecyclerView recyclerView;
    private boolean outsideTouchable;
    private PicAdapter picAdapter;
    private boolean cancelable = true;

    public ScreenPicPopup(Context context,boolean outsideTouchable){
        this.context = context;
        this.outsideTouchable = outsideTouchable;

        View view = getView();
        popupWindow = new PopupWindow(view,-1,-1);
    }
    private View getView(){
        View view = View.inflate(context, R.layout.screen_pic_popup, null);
        outsideRelative = (RelativeLayout)view.findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) view.findViewById(R.id.inner_linear);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        outsideRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outsideTouchable){
                    PopupUtil.dismissPopup();
                }
            }
        });
        return view;
    }
    public boolean isShowing(){
        return popupWindow.isShowing();
    }
    public void notifyDataChange(){
        if (picAdapter!=null) {
            picAdapter.notifyDataChange();
        }
    }
    public void dismiss(){
        if (popupWindow!=null&&popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    public boolean isCancelable(){
        return this.cancelable;
    }
    public void show(View parent){
        picAdapter = new PicAdapter(PicUtil.getScreenShotPic());
        recyclerView.setAdapter(picAdapter);
        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }
}
