package com.sky.slidingmenu_qq50;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView main_list;
    private ListView menu_list;
    private CustomFrameLayout cfl;
    private ImageView icon;
    private NoTouchLinearLayout noTouchLL;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView () {

        noTouchLL = (NoTouchLinearLayout) findViewById(R.id.notouch_ll);
        cfl = (CustomFrameLayout) findViewById(R.id.drag_layout);
        main_list = (ListView) findViewById(R.id.main_list);
        menu_list = (ListView) findViewById(R.id.menu_list);
        icon = (ImageView) findViewById(R.id.main_icon);

        cfl.setOnDragStateChangedListener(onDragStateChangedListener);
        noTouchLL.setOnNoTouchListener(new NoTouchLinearLayout.OnNoTouchListener() {
            @Override
            public boolean menuIsOpen () {
                return cfl.isClose();
            }
        });
    }

    private CustomFrameLayout.OnDragStateChangedListener onDragStateChangedListener = new CustomFrameLayout.OnDragStateChangedListener() {

        @Override
        public void onOpen () {
            //　打开后将menu 菜单的item 随机定位
            Random random = new Random();
            menu_list.smoothScrollToPosition(random.nextInt(Cheeses.QQ_FUCTIONS.length));
        }

        @Override
        public void onClose () {
            // 关闭时，main 的图标执行震动动画
            ObjectAnimator animator = ObjectAnimator.ofFloat(icon, "translationX", 0f, 5f);
            animator.setInterpolator(new CycleInterpolator(7));
            animator.setDuration(1000);
            animator.start();
        }

        @Override
        public void onDraging (float percent) {
            // 设置首页图标的渐变， percent默认为0， 从有到无
            ViewCompat.setAlpha(icon, 1 - percent);
        }
    };

    private void initData () {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                Cheeses.NAMES);
        main_list.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.QQ_FUCTIONS) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        menu_list.setAdapter(adapter);

    }
}
