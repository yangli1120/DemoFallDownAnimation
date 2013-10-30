package com.example.demolayoutanim;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    private ViewGroup mLayout;
    private ViewGroup mLinearLayout;
    private ImageView mArrowImgView;
    private View mArrowPopupWindow;
    private Animation falldown;
    private Animation faderight;
    WindowManager mWindowMgr;
    WindowManager.LayoutParams wParams;

    public static int MAX_Y = 270;
    private static int iteration = 0;

    public final static int PLAY_FALL_DOWN_AMIN = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case PLAY_FALL_DOWN_AMIN: {
                    if(wParams.y >= MAX_Y) {
                        mWindowMgr.removeView(mArrowPopupWindow);
                        removeMessages(PLAY_FALL_DOWN_AMIN);

                        mArrowImgView.setVisibility(View.VISIBLE);
                        mLayout.startAnimation(faderight);

                        iteration = 0;

                        return;
                    }
                    mWindowMgr.updateViewLayout(mArrowPopupWindow, wParams);
                    //I want a acceleration effect
                    wParams.y += iteration++;

                    sendMessageDelayed(obtainMessage(PLAY_FALL_DOWN_AMIN), 20);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = (RelativeLayout)findViewById(R.id.relative_rl);
        mLinearLayout = (LinearLayout)findViewById(R.id.linear_ll);
        mArrowImgView = (ImageView)findViewById(R.id.arrow_imgview);

        falldown = AnimationUtils.loadAnimation(this, R.anim.falldown);
        faderight = AnimationUtils.loadAnimation(this, R.anim.fade_right);

        mWindowMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mArrowPopupWindow = (View)LayoutInflater.from(this).inflate(R.layout.layout_arrow_window, null);

        falldown.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mLayout.startAnimation(faderight);
            }
        });
        faderight.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mLinearLayout.clearAnimation();
                mArrowImgView.setVisibility(View.INVISIBLE);
            }
        });

        mLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //mArrowImgView.setVisibility(View.VISIBLE);
                //mArrowImgView.startAnimation(falldown);
                wParams = getDefaultWindowParams();
                mWindowMgr.addView(mArrowPopupWindow, wParams);

                mHandler.sendEmptyMessage(PLAY_FALL_DOWN_AMIN);
            }
        });

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private WindowManager.LayoutParams getDefaultWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0, 0,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.RGBA_8888);
        params.gravity = Gravity.LEFT | Gravity.TOP;

        int[] location = new int[2];
        mLayout.getLocationInWindow(location);
        int startX = location[0] + mLayout.getWidth() - mArrowImgView.getWidth();

        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusbarHeight = rect.top;
        MAX_Y = location[1] + mLayout.getHeight() - mArrowImgView.getHeight() - statusbarHeight;

        params.x = startX;
        params.y = 0;

        return params;
    }
}
