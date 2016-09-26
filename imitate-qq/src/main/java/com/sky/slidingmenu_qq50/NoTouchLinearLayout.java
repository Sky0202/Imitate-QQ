package com.sky.slidingmenu_qq50;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 作者：SKY
 * 创建时间：2016-9-9 20:04
 * 描述：自定义线性布局，限制其触摸事件的传递
 */
public class NoTouchLinearLayout extends LinearLayout {

    public NoTouchLinearLayout (Context context) {
        this(context, null);
    }

    public NoTouchLinearLayout (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoTouchLinearLayout (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
//        if (noTouchListener != null) {
//            if (noTouchListener.menuIsOpen()) {
//                return true;
//            }
//        }
//        return super.onInterceptTouchEvent(ev);
        // 判断是否拦截事件，如果 menu 打开就拦截
        return noTouchListener != null && !noTouchListener.menuIsOpen();
    }

    private OnNoTouchListener noTouchListener;
    // 定义接口为了让使用者(界面)处理打开或关闭后的逻辑
    public interface OnNoTouchListener {
        boolean menuIsOpen ();
    }

    public void setOnNoTouchListener (OnNoTouchListener noTouchListener) {
        this.noTouchListener = noTouchListener;
    }
}
