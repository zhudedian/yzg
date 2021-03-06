package com.ider.yzg.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ider.yzg.MainActivity;
import com.ider.yzg.db.MyData;
import com.ider.yzg.popu.ConnectPopup;
import com.ider.yzg.popu.PopupUtil;
import com.ider.yzg.util.RequestUtil;
import com.ider.yzg.view.ConfirmPopu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;


/**
 * Created by Eric on 2017/9/6.
 */

public class Connect {
    private static InetAddress address;
    private static MulticastSocket multicastSocket;
    private static Handler mHandler;
    private static int connectCount ;
    private static List<String> ipList = new ArrayList<>();
    private static boolean isConnecting = false;
    private static boolean isScanComplete;
    private static int readCount = 0;
    private static CompleteListener listener;
    private static String lastIp = "";
    public static void onBrodacastSend(Handler handler,CompleteListener listener) {
        if (isConnecting){
            return;
        }
        Connect.listener = listener;
        ipList.clear();
        isScanComplete = false;
        isConnecting = true;
        mHandler = handler;
        connectCount = 0;
        readCount = 0;
        MyData.isConnect = false;
        try {
            // 侦听的端口
            multicastSocket = new MulticastSocket(8082);
            // 使用D类地址，该地址为发起组播的那个ip段，即侦听10001的套接字
            address = InetAddress.getByName("239.0.0.1");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.postDelayed(sendInfo,1000);
                    while (!MyData.isConnect&&!isScanComplete) {
                        // 获取当前时间
                        //String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        // 当前时间+标识后缀
                        //time = time + " >>> form server onBrodacastSend()";
                        String msg = "connect";
                        // 获取当前时间+标识后缀的字节数组
                        byte[] buf = msg.getBytes();
                        // 组报
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        // 向组播ID，即接收group /239.0.0.1  端口 10001
                        datagramPacket.setAddress(address);
                        // 发送的端口号
                        datagramPacket.setPort(10001);
                        try {
                            // 开始发送
                            multicastSocket.send(datagramPacket);
                            onBrodacastReceiver();
                            // 每执行一次，线程休眠2s，然后继续下一次任务
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void onBrodacastReceiver() {
        //Log.i("receiver","onBrodacastReceiver");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 字节数组的格式，即最大大小
                    byte[] buf = new byte[1024];
                        // 组报格式
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        // 接收来自group组播10001端口的二次确认，阻塞
                        multicastSocket.receive(datagramPacket);
                        // 从buf中截取收到的数据
                        byte[] message = new byte[datagramPacket.getLength()];
                        // 数组拷贝
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
                        // 这里打印ip字段
                    String ip = datagramPacket.getAddress().toString();
                    String ms = new String(message);
                    if (ms.equals("snoop")){
                        mHandler.removeCallbacks(sendInfo);
                        if (ip.equals(lastIp)){
                            isScanComplete = true;
                            setData(ip);
                            return;
                        }
                        if (!ipList.contains(ip)){
                            ipList.add(ip);
                        }else {
                            readCount++;
                            if (readCount>=5) {
                                isConnecting = false;
                                isScanComplete = true;
                                if (ipList.size() == 1) {
                                    setData(ip);
                                } else {

//                            Log.i("Connect","ip="+ip);
                                    handler.sendEmptyMessage(0);

                                }
                            }
                        }

                    }
                        System.out.println(ip);
                        // 打印组播端口10001发送过来的消息
                        System.out.println(new String(message));
                        // 这里可以根据结接收到的内容进行分发处理，假如收到 10001的 "snoop"字段为关闭命令，即可在此处关闭套接字从而释放资源
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void setData(String ip){
        lastIp = ip;
        isConnecting = false;
        MyData.isConnect = true;
        String oldIP = MyData.boxIP;
        MyData.boxIP = ip.replace("/","");
        if (!MyData.boxIP.equals(oldIP)){
            MyData.client=null;
        }
        MyData.infoUrl="http:/"+ip+":8083/info";
        MyData.downUrl="http:/"+ip+":8083/down";
        MyData.uploadUrl="http:/"+ip+":8083/upload";
        MyData.installUrl="http:/"+ip+":8083/install";
        MyData.appIconUrl="http:/"+ip+":8083/appicon";
        mHandler.sendEmptyMessage(0);
    }
    private static Runnable sendInfo = new Runnable() {
        @Override
        public void run() {
            isConnecting = false;
            isScanComplete = true;
            mHandler.sendEmptyMessage(0);
        }
    };
    private static Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    if (listener!= null){
                        listener.complete(ipList);
                    }
                    break;
                default:
                    break;
            }

        }
    };
    public interface CompleteListener{
        void complete(List<String> list);
    }
}
