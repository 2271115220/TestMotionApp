package rjw.net.testmotion.udp;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author zhd: 好好写
 * @date 2020/5/26 13:25
 * @desc
 */
public class SendUdpBroadcast {
    public String TAG = "zhdUDP";
    private ExecutorService mExecutorService = null;

    public SendUdpBroadcast() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void sendMessage(final String msg) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                    //2.打包
                    final String msgs = URLEncoder.encode(msg, "utf-8");
                    byte[] arr = msgs.getBytes();
                    //四个参数: 包的数据  包的长度  主机对象  端口号
                    DatagramPacket packet = new DatagramPacket
                            (arr, arr.length, InetAddress.getByName("192.168.1.106"), 4000);
                    //3.发送
                    socket.send(packet);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //4.关闭资源
                if (socket != null) {
                    socket.close();
                }
            }
        });
    }

    public void reviceMessage() {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //1
                DatagramSocket serverSocket = null;
                try {
                    serverSocket = new DatagramSocket(4000);
                    //2
                    byte[] arr = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(arr, arr.length);
                    while (true) {
                        //3 当程序运行起来之后,receive方法会一直处于监听状态
                        serverSocket.receive(packet);
                        //从包中将数据取出
                        byte[] arr1 = packet.getData();
                        String json = new String(arr1);

                        String newJsonStr = URLDecoder.decode(json, "utf-8").trim();
                        if (mOnRevice != null) {
                            mOnRevice.aaa(newJsonStr);
                        }
//                        UdpBean udpBean = GsonUtils.fromJson(newJsonStr, UdpBean.class);
//                        if (udpBean != null && udpBean.getType().equals("server")) {
//                            InetAddress inetAddress = packet.getAddress();
//                            String serverIP = inetAddress.getHostAddress();
//
//                            Log.d(TAG, "run:已匹配到服务器，服务器信息" + GsonUtils.getJson(udpBean));
//                            Log.d(TAG, "run:已匹配到服务器，服务器IP" + serverIP);
//                        }
                    }
                    //4
//        serverSocket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {

                }
            }
        });
    }

    private OnRevice mOnRevice;

    public void set(OnRevice mOnRevice) {
        this.mOnRevice = mOnRevice;
    }

    public interface OnRevice {
        void aaa(String aaa);
    }
}
