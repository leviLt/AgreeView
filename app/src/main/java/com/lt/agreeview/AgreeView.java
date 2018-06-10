package com.lt.agreeview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * @author Scorpio
 * @date 2018/6/7
 */

public class AgreeView extends View implements View.OnClickListener {
    public static final String TAG = "AgreeView";
    //默认动画移动距离
    private static final int DEFAULT_DISTANCE = 60;
    //默认动画起始点
    private static final int DEFAULT_FROM_Y = 0;
    //动画起始点距离
    private static final int DEFAULT_TO_Y = DEFAULT_DISTANCE;
    //动画起始透明度
    private static final float DEFAULT_FROM_ALPHA = 1.0f;
    //动画默认透明度
    private static final float DEFAULT_TO_ALPHA = 0.0f;
    //动画时长
    private static final int DEFAULT_DURATION = 700;
    //默认文字大小
    private static final int DEFAULT_TEXT_SIZE = 16;
    //默认文字颜色
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    //默认 文字
    private static final String DEFAULT_TEXT = "+1";
    //默认点赞图片
    private static final int DEFAULT_IMG = R.drawable.ic_favorite_black_24dp;
    //动画 文字
    private static final int ANIMATION_MODE_TEXT = 0;
    //动画图片
    private static final int ANIMATION_MODE_IMG = 1;

    //默认文字模式动画
    private static final int DEFAULT_ANIMATION_MODE = ANIMATION_MODE_TEXT;


    //移动距离
    private int distance;
    //Y轴起始偏移量
    private int from_y;
    //Y轴移动距离
    private int to_y;
    //起始透明度
    private float from_alpha;
    //结束透明度
    private float to_alpha;
    //动画时长
    private int duration;
    //动画文本
    private String text = "";
    //文本大小
    private int text_size;
    //文本颜色
    private int text_color;
    //动画
    private AppCompatTextView tvAnimation;

    //动画图片
    private Drawable animalDrawable;

    //点赞图片
    private Drawable agreeDrawable;

    private Context mContext;

    private int animationMode;

    /**
     * PopupWindow来显示点击动画
     */
    private PopupWindow mPopupWindow;
    /**
     * 动画组合
     */
    private AnimationSet mAnimationSet;
    /**
     * 是否改变了属性
     */
    private boolean isChanged = true;

    private Animation animation;

    public AgreeView(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public AgreeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public AgreeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AgreeView);
            distance = typedArray.getInt(R.styleable.AgreeView_distance, DEFAULT_DISTANCE);
            from_y = typedArray.getInt(R.styleable.AgreeView_from_y, DEFAULT_FROM_Y);
            to_y = distance;
            from_alpha = typedArray.getFloat(R.styleable.AgreeView_from_alpha, DEFAULT_FROM_ALPHA);
            to_alpha = typedArray.getFloat(R.styleable.AgreeView_to_alpha, DEFAULT_TO_ALPHA);
            duration = typedArray.getInt(R.styleable.AgreeView_duration, DEFAULT_DURATION);
            text = typedArray.getString(R.styleable.AgreeView_text);
            if (text == null) {
                text = DEFAULT_TEXT;
            }
            text_size = typedArray.getInt(R.styleable.AgreeView_text_size, DEFAULT_TEXT_SIZE);
            text_color = typedArray.getColor(R.styleable.AgreeView_text_color, DEFAULT_TEXT_COLOR);
            animalDrawable = typedArray.getDrawable(R.styleable.AgreeView_animation_img);
            agreeDrawable = typedArray.getDrawable(R.styleable.AgreeView_img);
            animationMode = typedArray.getInt(R.styleable.AgreeView_animation, ANIMATION_MODE_TEXT);
            if (agreeDrawable == null) {
                agreeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp);
            }
            //资源释放
            typedArray.recycle();
        }

        //我们将PopupWindow作为动画View
        //初始化我们的PopupWindow
        mPopupWindow = new PopupWindow();
        //PopupWindow创建相对布局
        RelativeLayout layout = new RelativeLayout(mContext);
        //布局参数
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tvAnimation = new AppCompatTextView(mContext);
        tvAnimation.setIncludeFontPadding(false);
        tvAnimation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, text_size);
        tvAnimation.setTextColor(text_color);
        if (animationMode == ANIMATION_MODE_TEXT) {
            tvAnimation.setText(text);
        } else {
            tvAnimation.setText("");
            tvAnimation.setBackgroundDrawable(animalDrawable);
        }
        tvAnimation.setLayoutParams(layoutParams);
        layout.addView(tvAnimation);
        mPopupWindow.setContentView(layout);

        //量测我们的动画的宽高
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvAnimation.measure(w, h);
        mPopupWindow.setWidth(tvAnimation.getMeasuredWidth());
        Log.e(TAG, "distance==== " + distance);
        mPopupWindow.setHeight(distance + tvAnimation.getMeasuredHeight());
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setFocusable(false);
        mPopupWindow.setTouchable(false);
        mPopupWindow.setOutsideTouchable(false);

        this.setOnClickListener(this);

        //设置文字、图片移动动画
        setPopAnimation();
        //点击缩放动画
        setScaleAnimation();

    }

    /**
     * 缩放动画
     */
    private void setScaleAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.8f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.8f, 1.2f, 1f);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }


    @Override
    public void onClick(View v) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            int offsetY = -getHeight() - mPopupWindow.getHeight();
            mPopupWindow.showAsDropDown(this, getWidth() / 2 - mPopupWindow.getWidth() / 2, offsetY);
            mPopupWindow.update();
            if (mAnimationSet == null) {
                setPopAnimation();
            }
            tvAnimation.startAnimation(mAnimationSet);
            setScaleAnimation();
            //外部点击事件
            if (clickListener != null) {
                clickListener.onAgreeClick(v);
            }
        }
    }

    /**
     * 动画组合
     */
    private void setPopAnimation() {
        mAnimationSet = new AnimationSet(true);
        TranslateAnimation translateAnim = new TranslateAnimation(0, 0, from_y, -to_y);
        AlphaAnimation alphaAnim = new AlphaAnimation(from_alpha, to_alpha);
        mAnimationSet.addAnimation(translateAnim);
        mAnimationSet.addAnimation(alphaAnim);
        mAnimationSet.setDuration(duration);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mPopupWindow.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //将我们的Drawable画到画布
        agreeDrawable.draw(canvas);
    }

    /**
     * 量测点击控件的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int w_mode = MeasureSpec.getMode(widthMeasureSpec);
        int w_size = MeasureSpec.getSize(widthMeasureSpec);

        int h_mode = MeasureSpec.getMode(heightMeasureSpec);
        int h_size = MeasureSpec.getSize(heightMeasureSpec);

        if (w_mode == MeasureSpec.AT_MOST || w_mode == MeasureSpec.UNSPECIFIED) {
            width = agreeDrawable.getIntrinsicWidth();
        } else {
            width = w_size;
        }

        if (h_mode == MeasureSpec.AT_MOST || h_mode == MeasureSpec.UNSPECIFIED) {
            height = agreeDrawable.getIntrinsicHeight();
        } else {
            height = h_size;
        }
        setMeasuredDimension(width, height);

        //根据量测的宽高，设置我们画的Drawable的大小
        @SuppressLint("DrawAllocation")
        Rect rect = new Rect(0, 0, width, height);
        agreeDrawable.setBounds(rect);
    }

    public void setClickListener(AgreeViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private AgreeViewClickListener clickListener;

    public interface AgreeViewClickListener {
        void onAgreeClick(View view);
    }

}

