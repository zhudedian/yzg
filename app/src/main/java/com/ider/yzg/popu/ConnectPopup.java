package com.ider.yzg.popu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;
import com.ider.yzg.view.ConfirmPopu;

import java.util.List;

import static android.R.attr.data;
import static android.R.string.cancel;
import static android.R.string.ok;

/**
 * Created by Eric on 2018/1/16.
 */

public class ConnectPopup {
    private Context context;
    private PopupWindow popupWindow;
    private RelativeLayout outsideRelative;
    private LinearLayout innerLinear;
    private TextView title;
    private ListView listView;
    private boolean outsideTouchable;
    private String[] data = {"/192.168.2.8"};
    private boolean cancelable = true;

    public ConnectPopup(Context context,List<String> data1 ,String titleStr,boolean outsideTouchable,OnOkListener listener){
        this.context = context;
//        this.data = data;
        this.outsideTouchable = outsideTouchable;
        View view = getView(listener);
        title.setText(titleStr);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,data);
//        listView.setAdapter(adapter);
        popupWindow = new PopupWindow(view,-1,-1);
    }

    private View getView(final OnOkListener listener){
        View view = View.inflate(context, R.layout.connect_popup, null);
        outsideRelative = (RelativeLayout)view.findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) view.findViewById(R.id.inner_linear);
        title = (TextView)view.findViewById(R.id.title);
        listView = (ListView)view.findViewById(R.id.list_view);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener!=null){
                    listener.onOkClick(data[position]);
                }
            }
        });
        return view;
    }
    public ConnectPopup setCancelable(boolean cancelable){
        this.cancelable = cancelable;
        return this;
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
        void onOkClick(String ip);
    }
}
