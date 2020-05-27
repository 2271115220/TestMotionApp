package rjw.net.testmotion.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import rjw.net.testmotion.R;

public class ScreenActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {
    private static final int CAPTURE_CODE = 0x123;
    private MediaProjectionManager projectionManager;
    private int screenDensity;
    private int displayWidth = 360;
    private int displayHeight = 640;
    private boolean screenSharing;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;

    private SurfaceView surfaceView;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private String TAG = "zhd";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        initScreenS();
        start();
    }

    /**
     * 初始化什么东西
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initScreenS() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        // 获取应用界面上的SurfaceView组件
        // 控制界面上的SurfaceView组件的宽度和高度
//        mSurfaceTexture = new SurfaceTexture(R.string.surface_name);
        surfaceView = findViewById(R.id.surface);
        mSurface = surfaceView.getHolder().getSurface();
//        mSurfaceTexture.setOnFrameAvailableListener(this);
        // 获取MediaProjectionManager管理器
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start() {
        Intent intent = projectionManager.createScreenCaptureIntent(); // ②
        startActivityForResult(intent, CAPTURE_CODE);  // ③
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_CODE) {
            // 如果resultCode不等于RESULT_OK，表明用户拒绝了屏幕捕捉
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "用户取消了屏幕捕捉", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);  // ④
            mediaProjection.registerCallback(new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    super.onStop();
                }
            },new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG, "handleMessage: ");
                }
            });
            virtualDisplay = mediaProjection.createVirtualDisplay("屏幕捕捉", displayWidth, displayHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mSurface, null/*Callbacks*/, null /*Handler*/);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable: ");
    }
}
