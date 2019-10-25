package com.zhoux.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.Random;

/**
 * @author zhouxiang
 * @fileName CircularProgressView
 * @date or  2019-10-24 17:44
 * @describe TODO
 */
public class CircularProgressView extends View {

    private Paint mBackPaint, mProgPaint;   // 绘制画笔
    private RectF mRectF;       // 绘制区域
    private int[] mColorArray;  // 圆环渐变色
    private int   mProgress;    // 圆环进度(0-100)

    private Context context;
    private Paint   mPaint;
    private int     mRectCount;
    private int     mWidth;// 矩形的宽
    private int     mMaxHeight;//矩形的最高
    private int     mMinHeight;//矩形的最低
    private float   mRadius;//圆角半径
    private float   mOffset;// 偏移量
    private int     mSpeed;//速度
    private boolean isStartDraw = false;//是否播放波形

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView);

        // 初始化背景圆环画笔
        mBackPaint = new Paint();
        mBackPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mBackPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mBackPaint.setAntiAlias(true);              // 设置抗锯齿
        mBackPaint.setDither(true);                 // 设置抖动
        mBackPaint.setStrokeWidth(typedArray.getDimension(R.styleable.CircularProgressView_backWidth, 5));
        mBackPaint.setColor(typedArray.getColor(R.styleable.CircularProgressView_backColor, Color.LTGRAY));

        // 初始化进度圆环画笔
        mProgPaint = new Paint();
        mProgPaint.setStyle(Paint.Style.STROKE);    // 只描边，不填充
        mProgPaint.setStrokeCap(Paint.Cap.ROUND);   // 设置圆角
        mProgPaint.setAntiAlias(true);              // 设置抗锯齿
        mProgPaint.setDither(true);                 // 设置抖动
        mProgPaint.setStrokeWidth(typedArray.getDimension(R.styleable.CircularProgressView_progWidth, 10));
        mProgPaint.setColor(typedArray.getColor(R.styleable.CircularProgressView_progColor, Color.BLUE));

        // 初始化进度圆环渐变色
        int startColor = typedArray.getColor(R.styleable.CircularProgressView_progStartColor, -1);
        int firstColor = typedArray.getColor(R.styleable.CircularProgressView_progFirstColor, -1);
        if (startColor != -1 && firstColor != -1) mColorArray = new int[]{startColor, firstColor};
        else mColorArray = null;

        // 初始化进度
        mProgress = typedArray.getInteger(R.styleable.CircularProgressView_progress, 0);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);//充满
        mPaint.setColor(ContextCompat.getColor(context, R.color.color_ffffff));
        mPaint.setAntiAlias(true);// 设置画笔的锯齿效果

        mWidth = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveWidh, 4));
        mMaxHeight = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveMaxHeight, 27));
        mMinHeight = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveMinHeight, 2));
        mRadius = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveRadius, 4));
        mOffset = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveOffset, 6));
        mSpeed = typedArray.getInt(R.styleable.CircularProgressView_waveSpeed, 150);
        mRectCount = typedArray.getInt(R.styleable.CircularProgressView_waveRectCount, 5);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((viewWide > viewHigh ? viewHigh : viewWide) - (mBackPaint.getStrokeWidth() > mProgPaint.getStrokeWidth() ? mBackPaint.getStrokeWidth() : mProgPaint.getStrokeWidth()));
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        mRectF = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray.length > 1)
            mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, 0, 360, false, mBackPaint);
        canvas.drawArc(mRectF, 275, 360 * mProgress / 100, false, mProgPaint);

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray.length > 1)
            mPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));

        float centerY = (getBottom() - getTop()) / 2;
        //计算X轴偏移量
        float centerX = (getRight() - getLeft()) / 2 -mOffset*2-mWidth*2-mWidth/2;
        Random random = new Random();
        for (int i = 0; i < mRectCount; i++) {
            int randomHight = mMinHeight + random.nextInt(mMaxHeight - mMinHeight);
            RectF rectF = new RectF(centerX + (mWidth + mOffset) * i,
                    centerY - randomHight / 2,
                    centerX + (mWidth + mOffset) * i + mWidth,
                    centerY + randomHight / 2);// 设置个新的长方形
            canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);//第二个参数是x半径，第三个参数是y半径
        }
        // 使得view延迟重绘
        if (isStartDraw)
            postInvalidateDelayed(mSpeed);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 获取当前进度
     *
     * @return 当前进度（0-100）
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置当前进度
     *
     * @param progress 当前进度（0-100）
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    public void setProgress(int progress, long animTime) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofInt(mProgress, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    /**
     * 设置背景圆环宽度
     *
     * @param width 背景圆环宽度
     */
    public void setBackWidth(int width) {
        mBackPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置背景圆环颜色
     *
     * @param color 背景圆环颜色
     */
    public void setBackColor(@ColorRes int color) {
        mBackPaint.setColor(ContextCompat.getColor(getContext(), color));
        invalidate();
    }

    /**
     * 设置进度圆环宽度
     *
     * @param width 进度圆环宽度
     */
    public void setProgWidth(int width) {
        mProgPaint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置进度圆环颜色
     *
     * @param color 景圆环颜色
     */
    public void setProgColor(@ColorRes int color) {
        mProgPaint.setColor(ContextCompat.getColor(getContext(), color));
        mProgPaint.setShader(null);
        invalidate();
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param startColor 进度圆环开始颜色
     * @param firstColor 进度圆环结束颜色
     */
    public void setProgColor(@ColorRes int startColor, @ColorRes int firstColor) {
        mColorArray = new int[]{ContextCompat.getColor(getContext(), startColor), ContextCompat.getColor(getContext(), firstColor)};
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param colorArray 渐变色集合
     */
    public void setProgColor(@ColorRes int[] colorArray) {
        if (colorArray == null || colorArray.length < 2) return;
        mColorArray = new int[colorArray.length];
        for (int index = 0; index < colorArray.length; index++)
            mColorArray[index] = ContextCompat.getColor(getContext(), colorArray[index]);
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }

    /**
     * 设置宽度、偏移和速度
     * @param with
     * @param offset
     * @param spead
     */
    public void setValues(int with,int offset,int spead){
        mWidth = Utils.dip2px(context, with);
        mOffset = Utils.dip2px(context, offset);
        mSpeed = spead;
    }

    public void setmPaint(int color) {
        mPaint.setColor(ContextCompat.getColor(context, color));
    }

    public void setStartDraw(boolean startDraw) {
        isStartDraw = startDraw;
    }

    public boolean isStartDraw() {
        return isStartDraw;
    }

}