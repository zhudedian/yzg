package com.ider.yzg.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ider.yzg.R;

/**
 * Created by Eric on 2017/10/25.
 */

public class RemoteFragment extends Fragment implements View.OnClickListener{

    private Context context;

    private TextView push,touch;
    private LinearLayout touchLinear;
    private RelativeLayout pushRelative;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_remote,container,false);
        push = (TextView) view.findViewById(R.id.push_button);
        touch = (TextView) view.findViewById(R.id.touch_button);

        touchLinear = (LinearLayout)view.findViewById(R.id.touch_linear);
        pushRelative = (RelativeLayout)view.findViewById(R.id.push_relative);

        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        setListener();
        push.performClick();
    }
    private void setListener(){
        push.setOnClickListener(this);
        touch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.push_button:
                clickPush();
                break;
            case R.id.touch_button:
                clickTouch();
                break;
        }
    }

    private void clickPush(){
        push.setSelected(true);
        touch.setSelected(false);
        push.setTextColor(getResources().getColor(R.color.black));
        touch.setTextColor(getResources().getColor(R.color.white));
        touchLinear.setVisibility(View.GONE);
        pushRelative.setVisibility(View.VISIBLE);
    }
    private void clickTouch(){
        touch.setSelected(true);
        push.setSelected(false);
        push.setTextColor(getResources().getColor(R.color.white));
        touch.setTextColor(getResources().getColor(R.color.black));
        touchLinear.setVisibility(View.VISIBLE);
        pushRelative.setVisibility(View.GONE);
    }
}
