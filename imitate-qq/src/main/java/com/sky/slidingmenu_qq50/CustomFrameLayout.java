package com.sky.slidingmenu_qq50;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 作者：SKY
 * 创建时间：2016-9-9 9:56
 * 描述：自定义 view
 */
public class CustomFrameLayout extends FrameLayout {

    private ViewDragHelper helper;

    private View main;
    private View menu;


    public CustomFrameLayout(Context context) {
        this(context, null);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 定义一个触摸事件的帮助方法
        helper = ViewDragHelper.create(this, callBack);
    }

    private float maxDragRange;
    private ImageView menu_icon;
    private DragState preState;
    // 默认状态为关闭
    private DragState currentState = DragState.CLOSE;


    // 回调方法
    private ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {
        @Override
        /**
         * @params child: 捕获的视图
         * @params pointerId 手指触摸时的 id
         */
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menu || child == main;
        }

        /** 创建时间：2016-9-9 10:46  描述：水平方向是否可以被拖拽
         * 参数1 child： 被拖拽的子 view
         * 参数2 left： 被拖拽的视图距离屏幕左边的距离加上 dx
         * 参数3 dx： x距离的变化值，由系统监测
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == main) {
                if (left > maxDragRange) {
                    left = (int) maxDragRange;
                } else if (left < 0) {
                    left = 0;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            // 禁止 menu 的滑动，让menu 反方向滑动 偏移量，两个相抵消，即实现了不滑动的效果
            if (changedView == menu) {
                menu.offsetLeftAndRight(-dx);

                // 实现拖动 menu 使 main 移动
                // 先获取 main 距离left 的距离
                int oldLeft = main.getLeft();
                // 新的距离 加上 menu 的偏移量
                int newLeft = oldLeft + dx;
                if (newLeft > maxDragRange) {
                    newLeft = (int) maxDragRange;
                } else if (newLeft < 0) {
                    newLeft = 0;
                }
                int newDx = newLeft - oldLeft;
                main.offsetLeftAndRight(newDx);
            }
            // 获取移动的 百分比
            float percent = main.getLeft() * 1.0f / maxDragRange;
            executeAnimation(percent);

            // 执行监听
            changeCurrentDragState(percent);
        }

        /**
         * 描述：手指释放的操作
         * xvel yvel x与y轴的释放速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel == 0 && releasedChild.getLeft() > maxDragRange * 0.5f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }

        private void changeCurrentDragState(float percent) {
            preState = currentState;
            if (percent == 0) {
                currentState = DragState.CLOSE; // close state
            } else if (percent == 1) {
                currentState = DragState.OPEN; // open state
            } else {
                currentState = DragState.DRAGING; // 拖拽中
            }
            // 调用接口的方法，通知使用方
            if (dragStateChangedListener != null) {

                if (currentState == DragState.OPEN) {
                    // 判断滑动中状态的改变，如果状态一样，则不执行方法
                    if (currentState != preState) {
                        dragStateChangedListener.onOpen();
                    }
                } else if (currentState == DragState.CLOSE) {
                    if (currentState != preState) {
                        dragStateChangedListener.onClose();
                    }
                } else {
                    dragStateChangedListener.onDraging(percent);
                }
            }
        }

        /** 创建时间：2016-9-9 20:23  描述：重写此方法，默认返回0
         * 返回任意比0大的数，即可拦截子view 的水平滑动事件
         */
        @Override
        public int getViewHorizontalDragRange (View child) {
            return 1;
        }
    };

    private void executeAnimation(float percent) {
        // 处理main 的缩放
        float evaluateResult = evaluate(percent, 1.0f, 0.8f);
        ViewCompat.setScaleX(main, evaluateResult);
        ViewCompat.setScaleY(main, evaluateResult);

        // 处理 menu 的缩放
        evaluateResult = evaluate(percent, 0.7f, 1.0f);
        ViewCompat.setScaleX(menu, evaluateResult);
        ViewCompat.setScaleY(menu, evaluateResult);

        float range = -maxDragRange * 0.8f;
        evaluateResult = evaluate(percent, range, 0);
        ViewCompat.setTranslationX(menu, evaluateResult);

        // 设置背景色的渐变
        int color = (int) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT);
        // 注意：必须设置背景，否则 会报 空指针
        getBackground().setColorFilter(color, PorterDuff.Mode.SRC_OVER);

    }

    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }

    // 估值   差值
    // 此方法原在 TypeEvaluator 类型估值器中，由于动画执行过程需要一直 new 对象，
    // 且此方法所用的参数没有成员变量，所以直接copy 过来，并自定义使用
    public float evaluate(float fraction, float startValue, float endValue) {
        return startValue + fraction * (endValue - startValue);
    }

    /**
     * 创建时间：2016-9-9 15:28  描述：当子控件移动过程中会调用此方法
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 如果存在下一帧，就移动过去
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    // 关闭 menu
    private void close() {
        if (helper.smoothSlideViewTo(main, 0, 0)) {
            invalidate();
        }
    }

    // 打开 menu
    private void open() {
        if (helper.smoothSlideViewTo(main, (int) maxDragRange, 0)) {
            invalidate();
        }
    }

    /** 创建时间：2016-9-9 21:24  描述：将menu的状态记录下来，供 main界面处理事件传递 */
    public boolean isClose(){
        return currentState == DragState.CLOSE;
    }

    /**
     * 创建时间：2016-9-9 11:11  描述：这个方法在测量之后调用，可以获取测量的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 限制滑动的最大距离
        maxDragRange = main.getMeasuredWidth() * 0.5f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将触摸事件交给 helper 处理
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 由 helper 决定是否拦截事件
        boolean b = helper.shouldInterceptTouchEvent(ev);
        return b;
    }

    /**
     * 创建时间：2016-9-9 10:26  描述：在所有子视图加载完成后调用这个方法，因此子视图的初始化在这里完成
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new RuntimeException("默认要求需要两个子布局！");
        }
        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new RuntimeException("子控件必须是 ViewGroup 或者其子类的类型！");
        }
        menu = getChildAt(0);
        main = getChildAt(1);
    }

    private OnDragStateChangedListener dragStateChangedListener;

    /**
     * 创建时间：2016-9-9 19:11  描述：定义滑动状态改变的回调接口
     */
    public interface OnDragStateChangedListener {
        void onOpen();

        void onClose();

        void onDraging(float percent);
    }

    // 为接口设置set方法
    public void setOnDragStateChangedListener(OnDragStateChangedListener listener) {
        this.dragStateChangedListener = listener;
    }

    // 使用枚举定义静态变量
    public enum DragState {
        OPEN, CLOSE, DRAGING
    }
}
