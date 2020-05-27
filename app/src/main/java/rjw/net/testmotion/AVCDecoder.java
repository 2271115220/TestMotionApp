package rjw.net.testmotion;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

public class AVCDecoder {
    private String TAG = "MC_AVCDecoder";
    private MediaCodec mCodec;
    private MediaFormat mCodecFormat;
    private final static String MIME_TYPE = "video/avc";
    private final static int VIDEO_WIDTH = 720;
    private final static int VIDEO_HEIGHT = 1280;
    private SurfaceView mSurfaceView;
//    private LinkedList<VideoFrame> mFrameList = new LinkedList<>();

    public final static int DECODE_ASYNC = 0;
    public final static int DECODE_SYNC = 1;
    public final static int DECODE_SYNC_DEPRECATED = 2;
    private int mDecodeType = 0;
    private LinkedList<Integer> mInputIndexList = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AVCDecoder(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        initDecoder();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initDecoder() {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, 0, 0);
            mCodecFormat = format;

            if (mDecodeType == DECODE_ASYNC) {
                mCodec.setCallback(new MediaCodec.Callback() {
                    @Override
                    public void onInputBufferAvailable(MediaCodec codec, int index) {
                        //Log.i(TAG, "onInputBufferAvailable " + Thread.currentThread().getName());
                        mInputIndexList.add(index);
                    }

                    @Override
                    public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
                        //Log.i(TAG, "onOutputBufferAvailable");
                        mCodec.releaseOutputBuffer(index, true);
                    }

                    @Override
                    public void onError(MediaCodec codec, MediaCodec.CodecException e) {
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
                        Log.i(TAG, "onOutputFormatChanged");
                    }
                });
                queueInputBuffer();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private LinkedList<VideoFrame> mFrameList = new LinkedList<>();

    private void queueInputBuffer() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                while (true) {
//                    if (mFrameList.isEmpty() || mInputIndexList.isEmpty()) {
//                        continue;
//                    }
//                    VideoFrame frame = mFrameList.poll();
//                    Integer index = mInputIndexList.poll();
//                    ByteBuffer inputBuffer = mCodec.getInputBuffer(index);
//                    inputBuffer.clear();
//                    inputBuffer.put(frame.buf, frame.offset, frame.length);
//                    // Log.i(TAG, "queueInputBuffer " + frame.offset + "/" + frame.length);
//                    mCodec.queueInputBuffer(index, 0, frame.length - frame.offset, 0, 0);
                }
            }
        }).start();
    }

    public void onFrame(byte[] buf, int offset, int length) {
        // 首帧是SPS PPS，需要设置给解码器，才能工作
        Log.i(TAG, "queueInputBuffer " + Arrays.toString(buf) + "/" + Arrays.toString(buf));
        mCodecFormat.setByteBuffer("csd-0", ByteBuffer.wrap(buf));
        mCodecFormat.setByteBuffer("csd-1", ByteBuffer.wrap(buf));
        mCodec.configure(mCodecFormat, mSurfaceView.getHolder().getSurface(),
                null, 0);
        mCodec.start();
        switch (mDecodeType) {
            case DECODE_ASYNC:
                decodeAsync(buf, offset, length);
                break;
        }
    }

    public void decodeAsync(byte[] buf, int offset, int length) {
//        VideoFrame frame = new VideoFrame();
//        frame.buf = buf;
//        frame.offset = offset;
//        frame.length = length;
//        mFrameList.add(frame);
        // Log.i(TAG, "decodeAsync " + mFrameList.size());
    }

}