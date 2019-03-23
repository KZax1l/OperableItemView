package com.andova.widget

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class Text(bodyText: String?, briefText: String?, bodyColor: Int, briefColor: Int, divider: Int) {
    private var mRefresh = true
    private var mMaxTextWidth: Int = 0
    private var mTextInterval: Int = 0
    private var mBodyTextColor: Int = 0
    private var mBriefTextColor: Int = 0

    private var mBodyText: String? = null
    private var mBriefText: String? = null
    private val mBodyPaint: TextPaint by lazy { TextPaint() }
    private val mBriefPaint: TextPaint by lazy { TextPaint() }
    private var mBodyStcLayout: StaticLayout? = null
    private var mBriefStcLayout: StaticLayout? = null

    companion object {
        private const val TAG = "OperableItemView_Text"
    }

    init {
        mBodyText = bodyText
        mBriefText = briefText
        mTextInterval = divider
        mBodyTextColor = bodyColor
        mBriefTextColor = briefColor
    }

    fun initBriefPaint(context: Context, typefacePath: String?, textSize: Int) {
        mBriefPaint.color = mBriefTextColor
        try {
            mBriefPaint.typeface = Typeface.createFromAsset(context.assets, typefacePath)
        } catch (e: Exception) {
            Log.i(TAG, "No set special brief text typeface!")
        }
        mBriefPaint.textSize = textSize.toFloat()
        when (horizontalGravity()) {
            OIV_GRAVITY_FLAG_CENTER_HORIZONTAL -> mBriefPaint.textAlign = Paint.Align.CENTER
            OIV_GRAVITY_FLAG_RIGHT -> mBriefPaint.textAlign = Paint.Align.RIGHT
            OIV_GRAVITY_FLAG_LEFT -> mBriefPaint.textAlign = Paint.Align.LEFT
            else -> mBriefPaint.textAlign = Paint.Align.LEFT
        }
        mBriefPaint.isAntiAlias = true
    }

    fun initBodyPaint(context: Context, typefacePath: String?, textSize: Int) {
        mBodyPaint.color = mBodyTextColor
        try {
            mBodyPaint.typeface = Typeface.createFromAsset(context.assets, typefacePath)
        } catch (e: Exception) {
            Log.i(TAG, "No set special body text typeface!")
        }
        mBodyPaint.textSize = textSize.toFloat()
        when (horizontalGravity()) {
            OIV_GRAVITY_FLAG_CENTER_HORIZONTAL -> mBodyPaint.textAlign = Paint.Align.CENTER
            OIV_GRAVITY_FLAG_RIGHT -> mBodyPaint.textAlign = Paint.Align.RIGHT
            OIV_GRAVITY_FLAG_LEFT -> mBodyPaint.textAlign = Paint.Align.LEFT
            else -> mBodyPaint.textAlign = Paint.Align.LEFT
        }
        mBodyPaint.isAntiAlias = true
    }

    fun usableMaxTextWidth(widthPx: Int): Int {
        if (widthPx <= 0) return 0
        if (!mRefresh && mMaxTextWidth > 0) return mMaxTextWidth
        mMaxTextWidth = widthPx - occupiedWidthExceptText()
        return mMaxTextWidth
    }

    private fun occupiedWidthExceptText(): Int = paddingLeft + paddingRight + (if (mStartDrawable == null || mStartDrawable?.isVisible == false) 0 else mDrawablePadding + getStartDrawableWidth()) + if (mEndDrawable == null || mEndDrawable?.isVisible == false) 0 else mDrawablePadding + getEndDrawableWidth()

    fun initStaticLayout(widthPx: Int) {
        if (widthPx <= 0) return
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = StaticLayout(if (mBriefText == null) "" else mBriefText, mBriefPaint, briefTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = StaticLayout(if (mBodyText == null) "" else mBodyText, mBodyPaint, bodyTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }
    }

    private fun briefTextWidth(widthPx: Int): Int {
        mBriefText ?: return 0
        return when (mDrawableChainStyle) {
            OIV_DRAWABLE_CHAIN_STYLE_PACKED -> {
                val width = mBriefPaint.measureText(mBriefText).toInt()
                if (width > usableMaxTextWidth(widthPx)) usableMaxTextWidth(widthPx) else width
            }
            OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE -> usableMaxTextWidth(widthPx)
            else -> usableMaxTextWidth(widthPx)
        }
    }

    private fun bodyTextWidth(widthPx: Int): Int {
        if (TextUtils.isEmpty(mBodyText)) return 0
        return when (mDrawableChainStyle) {
            OIV_DRAWABLE_CHAIN_STYLE_PACKED -> {
                val width = mBodyPaint.measureText(mBodyText).toInt()
                if (width > usableMaxTextWidth(widthPx)) usableMaxTextWidth(widthPx) else width
            }
            OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE -> usableMaxTextWidth(widthPx)
            else -> usableMaxTextWidth(widthPx)
        }
    }
}