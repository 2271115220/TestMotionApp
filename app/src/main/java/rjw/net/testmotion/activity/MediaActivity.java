package rjw.net.testmotion.activity;

import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

import rjw.net.testmotion.R;

public class MediaActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1000;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay mVirtualDisplay;
    MediaCodec.BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private int width, height;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private String TAG = "zhd";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        mSurfaceView = findViewById(R.id.bbbbbbbb);
        requestScreenCapture();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestScreenCapture() {
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_CODE)
            return;
        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        initEncoder();
        initDecCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initEncoder() {
        Log.d(TAG, "initEncoder: ");
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        // 设置视频输入颜色格式，这里选择使用Surface作为输入，可以忽略颜色格式的问题，并且不需要直接操作输入缓冲区。
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        // 码率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
        // 帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 60);
        // I帧间隔
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0);
        try {
            mEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mEncoder.createInputSurface();
            mVirtualDisplay = mediaProjection.createVirtualDisplay("-display", width, height, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);

            mEncoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    Log.d(TAG, "onInputBufferAvailable: ");
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (mBufferInfo == null) {
                        mBufferInfo = info;
                    }
                    ByteBuffer buffer = mEncoder.getOutputBuffer(index);
                    encodeFrame(buffer, info.size);
                    mEncoder.releaseOutputBuffer(index, false);
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    Log.d(TAG, "onError: ");
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    Log.d(TAG, "onOutputFormatChanged: ");
                }
            });
            mEncoder.start();

        } catch (Exception e) {
            Log.w(TAG, e);
            mEncoder = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void encodeFrame(ByteBuffer buffer, int bufferSize) {
        byte[] imageByte = decodeValue(buffer);
//        Log.d(TAG, "encodeFrame: " + imageByte.length);
        onFrame(imageByte);

//        aaaaa.setImageBitmap(bitmap);
    }

    public byte[] decodeValue(ByteBuffer bytes) {
        int len = bytes.limit() - bytes.position();
        byte[] bytes1 = new byte[len];
        bytes.get(bytes1);
        return bytes1;
    }

    //根据视频编码创建解码器，这里是解码AVC编码的视频
    MediaCodec mediaCodec = null;

    public void initDecCode() {
        Log.d(TAG, "initDecCode: ");
        //首先我们需要一个显示视频的SurfaceView
        try {
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建视频格式信息
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        //！！！注意，这行代码需要界面绘制完成之后才可以调用！！！
        mediaCodec.configure(mediaFormat, mSurfaceView.getHolder().getSurface(), null, 0);
        mediaCodec.start();
    }

    //为减少卡顿，此方法应在子线程下被执行
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFrame(byte[] buf) {
        try {
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
            //填充数据到输入流
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer;
                inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                inputBuffer.put(buf, 0, buf.length);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, buf.length, System.nanoTime(), 0);
            }
            //解码数据到surface，实际项目中最好将以下代码放入另一个线程，不断循环解码以降低延迟
            if (mBufferInfo == null) {
                mBufferInfo = new MediaCodec.BufferInfo();
            }
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
            if (outputBufferIndex >= 0) {
                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //此处可以或得到视频的实际分辨率，用以修正宽高比
                //fixHW();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
