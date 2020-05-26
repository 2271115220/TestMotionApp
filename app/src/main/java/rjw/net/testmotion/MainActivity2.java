package rjw.net.testmotion;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity2 extends AppCompatActivity {
    private MyFrameLayout mMyFrameLayout;
    TcpSocketClient mTcpSocketClient;
    private String IP = "192.168.1.3";
    private int PORT = 9999;

    private int ACATION_DOUBLE_CLICK = 3;//双击

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
        setContentView(R.layout.activity_main);
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(10);
        mAction = new action();
        mExecutorService = Executors.newCachedThreadPool();
        mMyFrameLayout = findViewById(R.id.frameLayout);
        mMyFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                width = mMyFrameLayout.getWidth();
                height = mMyFrameLayout.getHeight();
                mAction.setAppWidth(width);
                mAction.setAppHeight(height);
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
                        mAction.setX(mPreX);
                        mAction.setY(mPreY);
//                        mAction.setX(new BigDecimal(mPreX).divide(new BigDecimal(width)).toString());
//                        mAction.setY(new BigDecimal(mPreY).divide(new BigDecimal(width)).toString());
                        final String jsonString = JSON.toJSONString(mAction);

                        mExecutorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mTcpSocketClient.sendMessageByTcpSocket(jsonString);
                            }
                        });
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动鼠标
                        mAction.setAction(MotionEvent.ACTION_MOVE);
                        float endW = event.getX() - mPreX;
                        float endH = event.getY() - mPreY;
                        if (endW != 0 || endH != 0) {
                            mAction.setX(endW);
                            mAction.setY(endH);
                            final String jsonString2 = JSON.toJSONString(mAction);
                            Log.d("zhd", "jsonString: " + jsonString2);
                            mExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mTcpSocketClient.sendMessageByTcpSocket(jsonString2);
                                }
                            });
                            mPreX = event.getX();
                            mPreY = event.getY();
                        }

                        break;
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
