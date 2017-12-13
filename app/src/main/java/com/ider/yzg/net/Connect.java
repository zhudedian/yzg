package com.ider.yzg.net;

import android.os.Handler;
import android.util.Log;

import com.ider.yzg.db.MyData;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


/**
 * Created by Eric on 2017/9/6.
 */

public class Connect {
    private static InetAddress address;
    private static MulticastSocket multicastSocket;
    private static Handler mHandler;
    private static boolean isOnBrodacastReceiver = false;
    public static void onBrodacastSend(Handler handler) {
        mHandler = handler;
        MyData.isConnect = false;
        try {
            // 侦听的端口
            multicastSocket = new MulticastSocket(8082);
            // 使用D类地址，该地址为发起组播的那个ip段，即侦听10001的套接字
            address = InetAddress.getByName("239.0.0.1");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!MyData.isConnect) {
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
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
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
                        MyData.isConnect = true;
                        String oldIP = MyData.boxIP;
                        MyData.boxIP = ip.replace("/","");
                        if (!MyData.boxIP.equals(oldIP)){
                            MyData.client=null;
                        }
                        MyData.infoUrl="http:/"+ip+":8080/info";
                        MyData.downUrl="http:/"+ip+":8080/down";
                        MyData.uploadUrl="http:/"+ip+":8080/upload";
                        MyData.installUrl="http:/"+ip+":8080/install";
                        MyData.appIconUrl="http:/"+ip+":8080/appicon";
                        mHandler.sendEmptyMessage(0);
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
    private static void onBrodacastReceiver2() {
        Log.i("receiver","onBrodacastReceiver2");
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
                        MyData.isConnect = true;
                        String oldIP = MyData.boxIP;
                        MyData.boxIP = ip.replace("/","");
                        if (!MyData.boxIP.equals(oldIP)){
                            MyData.client=null;
                        }
                        MyData.infoUrl="http:/"+ip+":8080/info";
                        MyData.downUrl="http:/"+ip+":8080/down";
                        MyData.uploadUrl="http:/"+ip+":8080/upload";
                        MyData.installUrl="http:/"+ip+":8080/install";
                        MyData.appIconUrl="http:/"+ip+":8080/appicon";
                        mHandler.sendEmptyMessage(0);
                    }
                    System.out.println(ip);
                    // 打印组播端口10001发送过来的消息
                    System.out.println(new String(message));
                    // 这里可以根据结接收到的内容进行分发处理，假如收到 10001的 "snoop"字段为关闭命令，即可在此处关闭套接字从而释放资源
                } catch (Exception e) {
                    e.printStackTrace();
                }

    }

}
