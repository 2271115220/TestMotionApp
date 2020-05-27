package rjw.net.testmotion;

import rjw.net.testmotion.client.TcpSocketClient;

/**
 * @author wfx: 一行代码,亿万生活.
 * @date 2020/3/23 13:52
 * @desc
 */
public class SendMsgRunable implements Runnable {

    private String msg;


    public SendMsgRunable(TcpSocketClient mTcpSocketClient, String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

    }
}
