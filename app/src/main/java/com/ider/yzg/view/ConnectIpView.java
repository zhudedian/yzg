package com.ider.yzg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by Eric on 2018/1/16.
 */

public class ConnectIpView extends RelativeLayout {
    private Context context;
    private RelativeLayout outsideRelative;
    private OnOkClickListener listener;
    private LinearLayout innerLinear;
    private TextView title;
    private ListView listView;
    private boolean isShowing = false;
    private boolean outsideTouchable;


    public ConnectIpView(Context context) {
        this(context, null);
    }
    public ConnectIpView (Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.connect_ip_view, this);
        outsideRelative = (RelativeLayout)findViewById(R.id.outside_relative);
        innerLinear = (LinearLayout) findViewById(R.id.inner_linear);
        title = (TextView)findViewById(R.id.title);
        listView = (ListView)findViewById(R.id.list_view);
        outsideRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outsideTouchable){
                    setVisibility(GONE);
                }
            }
        });
        innerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void show(String titleStr, final List<String> list, final OnOkClickListener listener){
        this.listener = listener;
        isShowing = true;
        title.setText(titleStr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener!=null){
                    listener.click(list.get(position));
                }
            }
        });
        setVisibility(VISIBLE);
    }

    public void dismiss(){
        isShowing = false;
        setVisibility(GONE);
    }
    public boolean isShowing(){
        return this.isShowing;
    }
    public interface OnOkClickListener{
        void click(String ip);
    }
}
