package rjw.net.testmotion;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private MyFrameLayout mMyFrameLayout;
    TcpSocketClient mTcpSocketClient;
    private String IP = "192.168.1.3";
    private int PORT = 9877;

    private double lastX = 0;//上一次手指的位置x
    private double lastY = 0;//上一次手指的位置y
    private double nowX = 0;//这次手指的位置X
    private double nowY = 0;//这次手指的位置Y
    private double codeX = 0;//两次动作之间的偏移量X
    private double codeY = 0;//两次动作之间的偏移量Y

    private float width = 0;
    private float height = 0;
    private action mAction;
    // 线程池
    private ExecutorService mExecutorService = null;
    private float mPreX, mPreY;

    private NumberFormat numberFormat;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(10);
        mAction = new action();
        mExecutorService = Executors.newCachedThreadPool();
        setContentView(R.layout.activity_main);
        mMyFrameLayout = findViewById(R.id.frameLayout);
        mMyFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                width = mMyFrameLayout.getWidth();
                height = mMyFrameLayout.getHeight();
            }
        });
        mMyFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //重新指定当前鼠标的位置
                        mPreX = event.getX();
                        mPreY = event.getY();
                        mAction.setAction(MotionEvent.ACTION_DOWN);
                        mAction.setX(numberFormat.format(mPreX / width));
                        mAction.setY(numberFormat.format(mPreY / height));
//                        mAction.setX(new BigDecimal(mPreX).divide(new BigDecimal(width)).toString());
//                        mAction.setY(new BigDecimal(mPreY).divide(new BigDecimal(width)).toString());
                        final String jsonString = JSON.toJSONString(mAction);
                        Log.d("zhd", "jsonString: " + jsonString);
                        mExecutorService.execute(new Thread() {
                            @Override
                            public void run() {
                                mTcpSocketClient.sendMessageByTcpSocket(jsonString);
                            }
                        });
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动鼠标

                        float aaa = event.getX();
                        float bbb = event.getY();
                        mAction.setAction(MotionEvent.ACTION_MOVE);
                        mAction.setX(numberFormat.format(aaa / width));
                        mAction.setY(numberFormat.format(bbb / height));
                        final String jsonString2 = JSON.toJSONString(mAction);
                        Log.d("zhd", "jsonString2: " + jsonString2);
                        mExecutorService.execute(new Thread() {
                            @Override
                            public void run() {
                                mTcpSocketClient.sendMessageByTcpSocket(jsonString2);
                            }
                        });

                }
                return true;
            }
        });

        mMyFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTcpSocketClient = new TcpSocketClient(IP, PORT, new TcpSocketClient.TcpSocketListener() {
            @Override
            public void callBackContent(String content) {

            }

            @Override
            public void clearInputContent() {

            }
        });
        mTcpSocketClient.startTcpSocketConnect();
    }
}
