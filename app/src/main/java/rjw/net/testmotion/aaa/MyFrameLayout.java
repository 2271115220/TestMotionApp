package rjw.net.testmotion.aaa;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author wfx: 一行代码,亿万生活.
 * @date 2020/3/23 9:45
 * @desc
 */
public class MyFrameLayout extends ImageView {

    public MyFrameLayout(Context context) {
        super(context);
    }

    public MyFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
