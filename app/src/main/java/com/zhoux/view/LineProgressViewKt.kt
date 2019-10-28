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
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

/**
 * @fileName CircularProgressViewKt
 * @date or  2019-10-25 11:04
 * @author   zhouxiang
 * @describe TODO
 */
class LineProgressViewKt(context: Context?, attrs: AttributeSet?) : View(context, attrs) {


    private val mPaint: Paint
    private var mWidth: Int = 0// 矩形的宽
    private val mHeight: Int//矩形的最低
    private val mRadius: Float//圆角半径
    private var mOffset: Float = 0.toFloat()// 偏移量
    var viewWide: Int = 0
    var mIndex: Int = 4//当前的位置
    var mRectCount: Int//总共的数量


    init {
        @SuppressLint("Recycle")
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.LineProgressView)

        mPaint = Paint()
        mPaint.style = Paint.Style.FILL//充满
        mPaint.color = ContextCompat.getColor(context, R.color.color_ffffff)
        mPaint.isAntiAlias = true// 设置画笔的锯齿效果

        mWidth = Utils.dip2px(context, typedArray.getDimension(R.styleable.LineProgressView_widh, 20f))
        mHeight = Utils.dip2px(context, typedArray.getDimension(R.styleable.LineProgressView_height, 1f))
        mRadius = Utils.dip2px(context, typedArray.getDimension(R.styleable.LineProgressView_radius, 4f)).toFloat()
        mOffset = Utils.dip2px(context, typedArray.getDimension(R.styleable.LineProgressView_offset, 4f)).toFloat()
        mRectCount = typedArray.getInt(R.styleable.LineProgressView_count, 5)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWide = measuredWidth - paddingLeft - paddingRight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = ((bottom - top) / 2).toFloat()
        //计算X轴偏移量
        mWidth = (viewWide - mOffset.toInt() * mRectCount) / mRectCount
        for (i in 0 until mRectCount) {
            //第二个参数是x半径，第三个参数是y半径
            when {
                i + 1 < mIndex -> {
                    mPaint.color = ContextCompat.getColor(context, R.color.color_663dacb7)
                }
                i + 1 == mIndex -> {
                    mPaint.color = ContextCompat.getColor(context, R.color.color_ff3dacb7)
                }
                i + 1 > mIndex -> {
                    mPaint.color = ContextCompat.getColor(context, R.color.color_ffedeeee)
                }
            }
            val randomHight = mHeight
            if (i == mRectCount - 1) {
                val rectF = RectF((mWidth + mOffset) * i,
                        centerY - randomHight / 2,
                        (mWidth + mOffset) * i + mWidth.toFloat() + mOffset,
                        centerY + randomHight / 2)
                canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)//第二个参数是x半径，第三个参数是y半径
            } else {
                val rectF = RectF((mWidth + mOffset) * i,
                        centerY - randomHight / 2,
                        (mWidth + mOffset) * i + mWidth.toFloat(),
                        centerY + randomHight / 2)
                canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)
            }
        }
    }

    /**
     * 设置当前进度
     * @param mIndex 当前进度
     * @param mRectCount 总进度
     */
    fun setProgress(mIndex: Int,mRectCount :Int) {
        this.mIndex = mIndex
        this.mRectCount = mRectCount
        invalidate()
    }

    /**
     * 设置当前进度
     * @param mIndex 当前进度
     */
    fun setProgress(mIndex: Int) {
        this.mIndex = mIndex
        this.mRectCount = mRectCount
        invalidate()
    }

}