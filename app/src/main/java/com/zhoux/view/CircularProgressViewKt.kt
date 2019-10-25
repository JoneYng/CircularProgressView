package com.zhoux.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import java.util.*

/**
 * @fileName CircularProgressViewKt
 * @date or  2019-10-25 11:04
 * @author   zhouxiang
 * @describe TODO
 */
class CircularProgressViewKt(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mBackPaint: Paint
    private var mProgPaint: Paint   // 绘制画笔
    private var mRectF: RectF? = null       // 绘制区域
    private var mColorArray: IntArray? = null  // 圆环渐变色
    private var mProgress: Int = 0    // 圆环进度(0-100)

    private val mPaint: Paint
    private val mRectCount: Int
    private var mWidth: Int = 0// 矩形的宽
    private val mMaxHeight: Int//矩形的最高
    private val mMinHeight: Int//矩形的最低
    private val mRadius: Float//圆角半径
    private var mOffset: Float = 0.toFloat()// 偏移量
    private var mSpeed: Int = 0//速度
    private var isStartDraw: Boolean = false//是否播放波形

    init {
        @SuppressLint("Recycle")
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.CircularProgressView)

        // 初始化背景圆环画笔
        mBackPaint = Paint()
        mBackPaint.style = Paint.Style.STROKE    // 只描边，不填充
        mBackPaint.strokeCap = Paint.Cap.ROUND   // 设置圆角
        mBackPaint.isAntiAlias = true              // 设置抗锯齿
        mBackPaint.isDither = true                 // 设置抖动
        mBackPaint.strokeWidth = typedArray.getDimension(R.styleable.CircularProgressView_backWidth, 5f)
        mBackPaint.color = typedArray.getColor(R.styleable.CircularProgressView_backColor, Color.LTGRAY)

        // 初始化进度圆环画笔
        mProgPaint = Paint()
        mProgPaint.style = Paint.Style.STROKE    // 只描边，不填充
        mProgPaint.strokeCap = Paint.Cap.ROUND   // 设置圆角
        mProgPaint.isAntiAlias = true              // 设置抗锯齿
        mProgPaint.isDither = true                 // 设置抖动
        mProgPaint.strokeWidth = typedArray.getDimension(R.styleable.CircularProgressView_progWidth, 10f)
        mProgPaint.color = typedArray.getColor(R.styleable.CircularProgressView_progColor, Color.BLUE)

        // 初始化进度圆环渐变色
        val startColor = typedArray.getColor(R.styleable.CircularProgressView_progStartColor, -1)
        val firstColor = typedArray.getColor(R.styleable.CircularProgressView_progFirstColor, -1)
        if (startColor != -1 && firstColor != -1)
            mColorArray = intArrayOf(startColor, firstColor)
        else
            mColorArray = null

        // 初始化进度
        mProgress = typedArray.getInteger(R.styleable.CircularProgressView_progress, 0)
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL//充满
        mPaint.color = ContextCompat.getColor(context, R.color.color_ffffff)
        mPaint.isAntiAlias = true// 设置画笔的锯齿效果

        mWidth = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveWidh, 4f))
        mMaxHeight = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveMaxHeight, 27f))
        mMinHeight = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveMinHeight, 2f))
        mRadius = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveRadius, 4f)).toFloat()
        mOffset = Utils.dip2px(context, typedArray.getDimension(R.styleable.CircularProgressView_waveOffset, 6f)).toFloat()
        mSpeed = typedArray.getInt(R.styleable.CircularProgressView_waveSpeed, 100)
        mRectCount = typedArray.getInt(R.styleable.CircularProgressView_waveRectCount, 5)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWide = measuredWidth - paddingLeft - paddingRight
        val viewHigh = measuredHeight - paddingTop - paddingBottom
        val mRectLength = ((if (viewWide > viewHigh) viewHigh else viewWide) - if (mBackPaint.strokeWidth > mProgPaint.strokeWidth) mBackPaint.strokeWidth else mProgPaint.strokeWidth).toInt()
        val mRectL = paddingLeft + (viewWide - mRectLength) / 2
        val mRectT = paddingTop + (viewHigh - mRectLength) / 2
        mRectF = RectF(mRectL.toFloat(), mRectT.toFloat(), (mRectL + mRectLength).toFloat(), (mRectT + mRectLength).toFloat())

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray!!.size > 1)
            mProgPaint.shader = LinearGradient(0f, 0f, 0f, measuredWidth.toFloat(), mColorArray, null, Shader.TileMode.MIRROR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(mRectF, 0f, 360f, false, mBackPaint)
        canvas.drawArc(mRectF, 275f, (360 * mProgress / 100).toFloat(), false, mProgPaint)

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray!!.size > 1)
            mPaint.shader = LinearGradient(0f, 0f, 0f, measuredWidth.toFloat(), mColorArray, null, Shader.TileMode.MIRROR)

        val centerY = ((bottom - top) / 2).toFloat()
        //计算X轴偏移量
        val centerX = ((right - left) / 2).toFloat() - mOffset * 2 - (mWidth * 2).toFloat() - (mWidth / 2).toFloat()
        val random = Random()
        for (i in 0 until mRectCount) {
            val randomHight = mMinHeight + random.nextInt(mMaxHeight - mMinHeight)
            val rectF = RectF(centerX + (mWidth + mOffset) * i,
                    centerY - randomHight / 2,
                    centerX + (mWidth + mOffset) * i + mWidth.toFloat(),
                    centerY + randomHight / 2)// 设置个新的长方形
            canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)//第二个参数是x半径，第三个参数是y半径
        }
        // 使得view延迟重绘
        if (isStartDraw)
            postInvalidateDelayed(mSpeed.toLong())
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * 获取当前进度
     *
     * @return 当前进度（0-100）
     */
    fun getProgress(): Int {
        return mProgress
    }

    /**
     * 设置当前进度
     *
     * @param progress 当前进度（0-100）
     */
    fun setProgress(progress: Int) {
        this.mProgress = progress
//        invalidate()
    }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    fun setProgress(progress: Int, animTime: Long) {
        if (animTime <= 0)
            setProgress(progress)
        else {
            val animator = ValueAnimator.ofInt(mProgress, progress)
            animator.addUpdateListener { animation ->
                mProgress = animation.animatedValue as Int
            }
            animator.interpolator = OvershootInterpolator()
            animator.duration = animTime
            animator.start()
        }
    }

    /**
     * 设置背景圆环宽度
     *
     * @param width 背景圆环宽度
     */
    fun setBackWidth(width: Int) {
        mBackPaint.strokeWidth = width.toFloat()
    }

    /**
     * 设置背景圆环颜色
     *
     * @param color 背景圆环颜色
     */
    fun setBackColor(@ColorRes color: Int) {
        mBackPaint.color = ContextCompat.getColor(context, color)
    }

    /**
     * 设置进度圆环宽度
     *
     * @param width 进度圆环宽度
     */
    fun setProgWidth(width: Int) {
        mProgPaint.strokeWidth = width.toFloat()
    }

    /**
     * 设置进度圆环颜色
     *
     * @param color 景圆环颜色
     */
    fun setProgColor(@ColorRes color: Int) {
        mProgPaint.color = ContextCompat.getColor(context, color)
        mProgPaint.shader = null
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param startColor 进度圆环开始颜色
     * @param firstColor 进度圆环结束颜色
     */
    fun setProgColor(@ColorRes startColor: Int, @ColorRes firstColor: Int) {
        mColorArray = intArrayOf(ContextCompat.getColor(context, startColor), ContextCompat.getColor(context, firstColor))
        mProgPaint.shader = LinearGradient(0f, 0f, 0f, measuredWidth.toFloat(), mColorArray, null, Shader.TileMode.MIRROR)
    }

    /**
     * 设置进度圆环颜色(支持渐变色)
     *
     * @param colorArray 渐变色集合
     */
    fun setProgColor(@ColorRes colorArray: IntArray?) {
        if (colorArray == null || colorArray.size < 2) return
        mColorArray = IntArray(colorArray.size)
        for (index in colorArray.indices)
            mColorArray!![index] = ContextCompat.getColor(context, colorArray[index])
        mProgPaint.shader = LinearGradient(0f, 0f, 0f, measuredWidth.toFloat(), mColorArray, null, Shader.TileMode.MIRROR)
    }

    /**
     * 设置宽度、偏移和速度
     * @param with
     * @param offset
     * @param spead
     */
    fun setValues(with: Int, offset: Int, spead: Int) {
        mWidth = Utils.dip2px(context, with.toFloat())
        mOffset = Utils.dip2px(context, offset.toFloat()).toFloat()
        mSpeed = spead
    }

    fun setmPaint(color: Int) {
        mPaint.color = ContextCompat.getColor(context, color)
    }

    fun setStartDraw(startDraw: Boolean) {
        isStartDraw = startDraw
    }

    fun isStartDraw(): Boolean {
        return isStartDraw
    }
}