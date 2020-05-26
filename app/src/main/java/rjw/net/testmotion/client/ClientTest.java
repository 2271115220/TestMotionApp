package rjw.net.testmotion.client;

/**
 * @description:
 * @author: yaKun.shi
 * @create: 2020-03-25 11:45
 **/
public class ClientTest {


    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();

        nettyClient.sendMsg("ssss");

    }

}
