package com.ider.yzg.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ider.yzg.R;

/**
 * Created by Eric on 2017/11/29.
 */

public class AppsFragment extends Fragment implements View.OnClickListener{

    private Context context;

    private TextView recommend,local,uninstall;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_apps,container,false);
        recommend = (TextView) view.findViewById(R.id.recommend_button);
        local = (TextView) view.findViewById(R.id.local_button);
        uninstall = (TextView)view.findViewById(R.id.uninstall_button);

        listView = (ListView)view.findViewById(R.id.list_view);

        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        setListener();
        local.performClick();
    }
    private void setListener(){
        recommend.setOnClickListener(this);
        uninstall.setOnClickListener(this);
        local.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.recommend_button:
                clickRecommend();
                break;
            case R.id.local_button:
                clickLocal();
                break;
            case R.id.uninstall_button:
                clickUninstall();
                break;
        }
    }
    private void clickRecommend(){
        recommend.setSelected(true);
        local.setSelected(false);
        uninstall.setSelected(false);
        recommend.setTextColor(getResources().getColor(R.color.black));
        local.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.white));

    }
    private void clickLocal(){
        local.setSelected(true);
        recommend.setSelected(false);
        uninstall.setSelected(false);
        local.setTextColor(getResources().getColor(R.color.black));
        recommend.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.white));

    }
    private void clickUninstall(){
        uninstall.setSelected(true);
        recommend.setSelected(false);
        local.setSelected(false);
        recommend.setTextColor(getResources().getColor(R.color.white));
        local.setTextColor(getResources().getColor(R.color.white));
        uninstall.setTextColor(getResources().getColor(R.color.black));

    }
}
