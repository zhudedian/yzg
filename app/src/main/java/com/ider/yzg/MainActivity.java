package com.ider.yzg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ider.yzg.view.AppsFragment;
import com.ider.yzg.view.BottomMenu;
import com.ider.yzg.view.CustomViewPager;
import com.ider.yzg.view.MyFragmentPagerAdapter;
import com.ider.yzg.view.RemoteFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CustomViewPager viewpager;
    private List<Fragment> fragmentList = new ArrayList<>();

    private RemoteFragment remoteFragment;
    private AppsFragment appsFragment;
    private BottomMenu apps,remote,transmitter,tool;

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
        //replaceFragment(remoteFragment);
        setListener();
        //remote.performClick();
        fragmentList.add(appsFragment);
        fragmentList.add(remoteFragment);
        viewpager = (CustomViewPager)findViewById(R.id.view_pager);
        remoteFragment.setViewPager(viewpager);
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setDate(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                break;
            case 1:
                apps.setSelect(false);
                remote.setSelect(true);
                transmitter.setSelect(false);
                tool.setSelect(false);
                break;
            case 2:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(true);
                tool.setSelect(false);
                break;
            case 3:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(false);
                tool.setSelect(true);
                break;
        }
    }

//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frame_layout,fragment);
//        transaction.commit();
//    }
}
