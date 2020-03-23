package rjw.net.testmotion;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author wfx: 一行代码,亿万生活.
 * @date 2020/3/23 9:45
 * @desc
 */
public class MyFrameLayout extends View {
    private String TAG = "motionevevt";
    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean onTouchEvent(final MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "onTouch: ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "onTouch: ACTION_MOVE");
//                float xAndroid = event.getX();
//                float yAndroid = event.getY();
//                Log.d(TAG, "onTouch: X:"+xAndroid);
//                Log.d(TAG, "onTouch: Y:"+yAndroid);
//
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "onTouch: ACTION_UP");
//                break;
//
//            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG, "onTouch: ACTION_DOWN");
//                break;
//
//        }
//        return true;
//    }
}
