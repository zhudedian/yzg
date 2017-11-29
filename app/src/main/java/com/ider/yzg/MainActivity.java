package com.ider.yzg;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ider.yzg.view.BottomMenu;
import com.ider.yzg.view.RemoteFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RemoteFragment remoteFragment;
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

        remoteFragment = new RemoteFragment();
        replaceFragment(remoteFragment);
        setListener();
        remote.performClick();
    }

    private void setListener(){
        apps.setOnClickListener(this);
        remote.setOnClickListener(this);
        transmitter.setOnClickListener(this);
        tool.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.apps:
                apps.setSelect(true);
                remote.setSelect(false);
                transmitter.setSelect(false);
                tool.setSelect(false);
                break;
            case R.id.remote:
                apps.setSelect(false);
                remote.setSelect(true);
                transmitter.setSelect(false);
                tool.setSelect(false);
                break;
            case R.id.transmitter:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(true);
                tool.setSelect(false);
                break;
            case R.id.tool:
                apps.setSelect(false);
                remote.setSelect(false);
                transmitter.setSelect(false);
                tool.setSelect(true);
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();
    }
}
