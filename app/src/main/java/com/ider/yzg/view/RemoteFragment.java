package com.ider.yzg.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.yzg.R;
import com.ider.yzg.db.MyData;
import com.ider.yzg.util.EditChangeListener;
import com.ider.yzg.util.FragmentInter;
import com.ider.yzg.util.RequestUtil;

/**
 * Created by Eric on 2017/10/25.
 */

public class RemoteFragment extends Fragment implements FragmentInter,View.OnTouchListener,GestureDetector.OnGestureListener,View.OnClickListener{

    private String TAG = "RemoteFragment";
    private Context context;

    private TextView push,touch;
    private NoticeBar noticeBar;
    private LinearLayout touchLinear;
    private RelativeLayout pushRelative;
    private EditView editView;
    private InputMethodManager imm;
    private CustomViewPager viewPager;
    private ImageView center,up,down,left,right,power,setting,volumeUp,volumeDown,volumeMute,back,home,menu;

    private int twoTouchTimes;
    private float lastX2,lastY2,lastX1,lastY1;
    private float lastYa,lastYb,lastXa,lastXb,lastXc,lastYc;
    private int lastUpX,lastUpY;
    private boolean twoTouch = false;
    private float lastTwoY,lastTwoX;
    private String msg;
    public static String info,longinfo,lenth;
    private GestureDetector mygesture = new GestureDetector(this);
    private String editTextOriginInfo="",editTextInfo="";
    public int page = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_remote,container,false);
        push = (TextView) view.findViewById(R.id.push_button);
        touch = (TextView) view.findViewById(R.id.touch_button);

        noticeBar = (NoticeBar)view.findViewById(R.id.notice_bar);
        touchLinear = (LinearLayout)view.findViewById(R.id.touch_linear);
        pushRelative = (RelativeLayout)view.findViewById(R.id.push_relative);

        center = (ImageView)view.findViewById(R.id.center_button);
        up = (ImageView)view.findViewById(R.id.up_button);
        down = (ImageView)view.findViewById(R.id.down_button);
        left = (ImageView)view.findViewById(R.id.left_button);
        right = (ImageView)view.findViewById(R.id.right_button);
        volumeUp = (ImageView)view.findViewById(R.id.volume_up_button);
        volumeDown = (ImageView)view.findViewById(R.id.volume_down_button);
        volumeMute = (ImageView)view.findViewById(R.id.volume_mute_button);
        power = (ImageView)view.findViewById(R.id.power_button);
        setting = (ImageView)view.findViewById(R.id.setting_button);
        back = (ImageView)view.findViewById(R.id.back_button);
        home = (ImageView)view.findViewById(R.id.home_button);
        menu = (ImageView)view.findViewById(R.id.menu_button);

        editView = (EditView)view.findViewById(R.id.edit_view);


        return view;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        setListener();
        clickPush();
    }
    @Override
    public void onResume(){
        super.onResume();
        if (page == 2){
            viewPager.setScanScroll(false);
        }
    }
    @Override
    public void fragmentInit() {
        if (!isAdded()){
            return;
        }
    }
    @Override
    public  void fragmentHandleMsg(String msg){
        if (!isAdded()){
            return;
        }
        if (msg.contains("connect_success")) {
            noticeBar.setGONE();
        }else if (msg.contains("connect_failed")) {
            noticeBar.setVISIBLE();
        }else if (msg.contains("InOp")){
            if (editView.isShowing()){
                return;
            }
            editView.show(context.getString(R.string.edit_write_title),new EditView.OnOkClickListener() {
                @Override
                public void click(boolean isOk, String editStr) {
                    if (isOk) {
//                        RequestUtil.sendInfo(editStr);
                        RequestUtil.closeIME(editStr);
                    }else {
                        RequestUtil.closeIME(editTextOriginInfo);
//                        RequestUtil.sendInfo(editTextOriginInfo);
                    }
//                    RequestUtil.closeIME(editStr);
//                    MyData.client.sendMsg("cb ,,,,,,,,,,,,");
                    editView.dismiss();
                    imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
                }
            });
            EditText editText = editView.getEditTextView();
            editText.setFocusable(true);
            editText.addTextChangedListener(new EditChangeListener());
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }else if (msg.contains("InCl")){
            editView.dismiss();
            imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
            editTextInfo = "";
        }else if (msg.contains("InFo")){
            RequestUtil.requestInfo(new RequestUtil.HandleResult() {
                @Override
                public void resultHandle(String result) {
//                    editTextInfo = editView.getText();
//                    if (!editTextInfo.equals(result)) {
//                        editTextInfo = result;
                        editView.setText(result);
                        editTextOriginInfo = result;
//                    }
                }
            });
        }
    }
    public boolean fragmentBack(){

        return false;
    }
    private void setListener(){
        push.setOnClickListener(this);
        touch.setOnClickListener(this);
        touchLinear.setOnTouchListener(this);
        touchLinear.setLongClickable(true);

        center.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        volumeMute.setOnClickListener(this);
        volumeDown.setOnClickListener(this);
        volumeUp.setOnClickListener(this);
        power.setOnClickListener(this);
        setting.setOnClickListener(this);
        home.setOnClickListener(this);
        back.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.push_button:
                clickPush();
                break;
            case R.id.touch_button:
                clickTouch();
                break;
        }
        if (!MyData.isConnect){
            Toast.makeText(context, context.getString(R.string.disconnect_notice), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()){
            case R.id.center_button:
                sendMsg("cocenter ,,,,,,");
                break;
            case R.id.left_button:
                sendMsg("coleft ,,,,,,,,");
                break;
            case R.id.right_button:
                sendMsg("coright ,,,,,,,");
                break;
            case R.id.up_button:
                sendMsg("coup ,,,,,,,,,,");
                break;
            case R.id.down_button:
                sendMsg("codown ,,,,,,,,");
                break;
            case R.id.volume_down_button:
                sendMsg("covoldown ,,,,,");
                break;
            case R.id.volume_up_button:
                sendMsg("covolup ,,,,,,,");
                break;
            case R.id.volume_mute_button:
                sendMsg("covolmute ,,,,,");
                break;
            case R.id.power_button:
                sendMsg("copower ,,,,,,,");
                break;
            case R.id.setting_button:
                sendMsg("cosetting ,,,,,");
                break;
            case R.id.back_button:
                sendMsg("cb ,,,,,,,,,,,,");
                break;
            case R.id.menu_button:
                sendMsg("comenubt ,,,,,,");
                break;
            case R.id.home_button:
                sendMsg("cohome ,,,,,,,,");
                break;
        }
    }

    private void sendMsg(String msg){
        Log.i(TAG,msg);
        if (MyData.client!=null){
            MyData.client.sendMsg(msg);
        }
    }

    private void clickPush(){
        push.setSelected(true);
        touch.setSelected(false);
        push.setTextColor(getResources().getColor(R.color.black));
        touch.setTextColor(getResources().getColor(R.color.white));
        touchLinear.setVisibility(View.GONE);
        pushRelative.setVisibility(View.VISIBLE);
        viewPager.setScanScroll(true);
        page = 1;
    }
    private void clickTouch(){
        touch.setSelected(true);
        push.setSelected(false);
        push.setTextColor(getResources().getColor(R.color.white));
        touch.setTextColor(getResources().getColor(R.color.black));
        touchLinear.setVisibility(View.VISIBLE);
        pushRelative.setVisibility(View.GONE);
        viewPager.setScanScroll(false);
        page = 2;
    }

    public void setViewPager(CustomViewPager viewPager){
        this.viewPager = viewPager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch"+"event.getAction()"+event.getAction());
        if (event.getPointerCount() == 2) {
            if (twoTouchTimes == 0){
                sendMsg("cd ,,,,,,,,,,,,");
                lastTwoX=event.getX(1);
                lastTwoY = event.getY(1);
                lastXa = lastTwoX;
                lastYa = lastTwoY;
            }else {
                lastXc=lastXb;
                lastYc = lastYb;
                lastYb =lastYa;
                lastXb = lastXa;
                lastYa = event.getY(1);
                lastXa = event.getX(1);
            }
            twoTouchTimes++;
            twoTouch = true;
            System.out.println("坐标A：X = " + event.getX(0) + "，Y = " + event.getY(0));
            System.out.println("坐标B：X = " + event.getX(1) + "，Y = " + event.getY(1)+"event.getPointerId(1)="+event.getPointerId(1));
            if (twoTouchTimes!=0){
                if (lastTwoY != -1) {
                    if (lastUpY == (int)(event.getY(1) - lastTwoY)&&lastUpX == (int)(event.getX(1) - lastTwoX)){
                        lastXb = lastXc;
                        lastYb = lastYc;
                    }else {
                        lastUpY = (int) (event.getY(1) - lastTwoY);
                        lastUpX = (int) (event.getX(1) - lastTwoX);
                        int x = lastUpX * 2;
                        int y = lastUpY * 2;
                        msg = "csP" + x + "P" + y + " ";
                        int length = msg.length();
                        if (length < 15) {
                            for (int i = 0; i < 15 - length; i++) {
                                msg = msg + ",";
                            }
                        }
                        sendMsg(msg);
                    }
                }
            }
        }else {
            twoTouchTimes = 0;
        }
        return mygesture.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown"+e.getPointerCount());
        twoTouch = false;
        twoTouchTimes =0 ;
        lastTwoY = -1;
        sendMsg("cn ,,,,,,,,,,,,");
        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG, "onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp");
        if (!twoTouch) {
            MyData.client.sendMsg("cc ,,,,,,,,,,,,");
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("MainActivity,onScroll", "e2.getX()="+e2.getX()+"e1.getX()="+e1.getX()+"e2.getY()="+e2.getY()+"e1.getY()="+e1.getY()+"velocityX="+distanceX+"velocityY="+distanceY);

        String msg;
        if (lastX2!=e1.getX()&&lastY2!=e1.getY()){
            lastX2 =e1.getX();
            lastY2 = e1.getY();
            int x = (int)e2.getX();
            int x2 = (int)e1.getX();
            int y = (int)e2.getY();
            int y2 = (int)e1.getY();
            msg =  "cmP"+(x-x2)+"P"+(y-y2)+" ";
            lastX1 = e2.getX();
            lastY1 = e2.getY();
        }else {
            msg = "cmP"+(int)(e2.getX()-lastX1)+"P"+(int)(e2.getY()-lastY1)+" ";
        }
        int length = msg.length();
        if (length<15){
            for (int i=0;i<15-length;i++){
                msg= msg+",";
            }
        }
        Log.i(TAG,"msg="+msg);
        if (!twoTouch){
            sendMsg(msg);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("MainActivity", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        twoTouchTimes =0 ;
        lastTwoY = -1;
        if (twoTouch) {
            msg = "cuP" + (lastUpX + (int) (lastXa - lastXb)) * 2 + "P" + (lastUpY + (int) (lastYa - lastYb)) * 2 + " ";
            int length = msg.length();
            if (length < 15) {
                for (int i = 0; i < 15 - length; i++) {
                    msg = msg + ",";
                }
            }
            sendMsg(msg);
        }
        Log.i(TAG, "e2.getX()="+e2.getX()+"e1.getX()="+e1.getX()+"e2.getY()="+e2.getY()+"e1.getY()="+e1.getY()+"velocityX="+velocityX+"velocityY="+velocityY);

        twoTouch = false;
        return false;
    }
}
