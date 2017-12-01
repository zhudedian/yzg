package com.ider.yzg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ider.yzg.db.MyData;
import com.ider.yzg.net.Connect;
import com.ider.yzg.util.FragmentInter;
import com.ider.yzg.util.MyApplication;
import com.ider.yzg.util.SocketClient;
import com.ider.yzg.view.AppsFragment;
import com.ider.yzg.view.BottomMenu;
import com.ider.yzg.view.CustomViewPager;
import com.ider.yzg.view.MyFragmentPagerAdapter;
import com.ider.yzg.view.RemoteFragment;
import com.ider.yzg.view.TransportFragment;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.fragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "MainActivity";
    private CustomViewPager viewpager;
    private List<Fragment> fragmentList = new ArrayList<>();

    private RemoteFragment remoteFragment;
    private AppsFragment appsFragment;
    private TransportFragment transportFragment;
    private FragmentInter fragmentInter;
    private BottomMenu apps,remote,transmitter,tool;

    private int endCount;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        apps = (BottomMenu)findViewById(R.id.apps);
        remote = (BottomMenu)findViewById(R.id.remote);
        transmitter = (BottomMenu)findViewById(R.id.transmitter);
        tool = (BottomMenu)findViewById(R.id.tool);

        appsFragment = new AppsFragment();
        remoteFragment = new RemoteFragment();
        transportFragment = new TransportFragment();
        fragmentInter = transportFragment;
        //replaceFragment(remoteFragment);
        setListener();

        fragmentList.add(appsFragment);
        fragmentList.add(remoteFragment);
        fragmentList.add(transportFragment);
        viewpager = (CustomViewPager)findViewById(R.id.view_pager);
        remoteFragment.setViewPager(viewpager);
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"positionOffset="+positionOffset+"positionOffsetPixels="+positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    setDate(position);
                    if (position == 1 && remoteFragment.page == 2) {
                        viewpager.setScanScroll(false);
                    } else {
                        viewpager.setScanScroll(true);
                    }
                    if (position == 2) {
                        fragmentInter.fragmentInit();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        remote.performClick();
    }

    private void setListener(){
        apps.setOnClickListener(this);
        remote.setOnClickListener(this);
        transmitter.setOnClickListener(this);
        tool.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.apps:
                setDate(0);
                viewpager.setCurrentItem(0);
                break;
            case R.id.remote:
                setDate(1);
                viewpager.setCurrentItem(1);
                break;
            case R.id.transmitter:
                setDate(2);
                viewpager.setCurrentItem(2);
                break;
            case R.id.tool:
                setDate(3);
                break;
        }
    }
    private void setDate(int position){
        switch (position){
            case 0:
                apps.setSelect(true);
                remote.setSelect(false);
                transmitter.setSelect(false);
                tool.setSelect(false);
                currentItem = 0;
                break;
            case 1:
                apps.setSelect(false);
                remote.setSelect(true);
                transmitter.setSelect(false);
                tool.setSelect(false);
                currentItem = 1;
                break;
            case 2:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(true);
                tool.setSelect(false);
                currentItem = 2;
                break;
            case 3:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(false);
                tool.setSelect(true);
                currentItem = 3;
                break;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Connect.onBrodacastSend(mHandler);
        init();
    }
    private void init(){

        if (MyData.isConnect){
            if (MyData.client==null){
                MyData.client = new SocketClient();
                MyData.client.clintValue(MyData.boxIP, 7777);
                MyData.client.openClientThread();
                SocketClient.mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String pos = msg.obj.toString();
                        Log.i("msg",pos);
                        if (pos.contains("In")) {
                            Intent intent = new Intent("Box_Message");
                            intent.putExtra("info", pos);
                            MyApplication.getContext().sendBroadcast(intent);
                            endCount = 0;
                        }else {
                            if (endCount >= 4){
                                if (MyData.client!=null) {
                                    MyData.client.close();
                                    MyData.client = null;
                                    Connect.onBrodacastSend(mHandler);
                                    init();
                                    Intent intent = new Intent("connect_failed");
                                    MyApplication.getContext().sendBroadcast(intent);
                                    endCount = 0;
                                }
                            }else {
                                endCount++;
                                Log.i("count",endCount+"");
                            }
                        }

                    }
                };
            }

        }else {

        }
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    init();
                    break;
                default:
                    break;
            }

        }
    };
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frame_layout,fragment);
//        transaction.commit();
//    }
    @Override
   public void onBackPressed(){
        if (currentItem!=2||!fragmentInter.fragmentBack()){
            finish();
        }
    }
}
