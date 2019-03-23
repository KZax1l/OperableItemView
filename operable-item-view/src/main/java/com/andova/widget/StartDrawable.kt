package com.andova.widget

import android.graphics.drawable.Drawable

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class StartDrawable(width: Int, height: Int, drawable: Drawable?) {
    private var mStartDrawableWidth: Int = 0
    private var mStartDrawableHeight: Int = 0
    private var mStartDrawable: Drawable? = null

    init {
        mStartDrawable = drawable
        mStartDrawableWidth = width
        mStartDrawableHeight = height
    }

    fun intrinsicWidth(): Int = mStartDrawable?.intrinsicWidth ?: 0 // todo 内联？
    fun intrinsicHeight(): Int = mStartDrawable?.intrinsicHeight ?: 0 // todo 内联？
    fun intrinsicWidthF(): Float = mStartDrawable?.intrinsicWidth?.toFloat() ?: 0f // todo 内联？
    fun intrinsicHeightF(): Float = mStartDrawable?.intrinsicHeight?.toFloat() ?: 0f // todo 内联？

    private fun width(): Int {
        if (mStartDrawableWidth > 0) return mStartDrawableWidth
        mBriefStcLayout ?: return intrinsicWidth()
        mBodyStcLayout ?: return intrinsicWidth()
        mStartDrawable ?: return 0
        val w = intrinsicWidthF()
        val h = intrinsicHeightF()
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> (h / w * mBriefStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> (h / w * mBodyStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> w.toInt()
            else -> w.toInt()
        }
    }

    private fun height(): Int {
        if (mStartDrawableHeight > 0) return mStartDrawableHeight
        mBriefStcLayout ?: return intrinsicHeight()
        mBodyStcLayout ?: return intrinsicHeight()
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> mBriefStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> mBodyStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> intrinsicHeight()
            else -> intrinsicHeight()
        }
    }

    @DrawableAlignStyle
    private fun startDrawableAlignStyle(): Int {
        if (mDrawableAlignStyle and OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START != 0) return OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START
        return if (mDrawableAlignStyle and OIV_DRAWABLE_ALIGN_STYLE_BODY_START != 0) {
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START
        } else OIV_DRAWABLE_ALIGN_STYLE_NORMAL
    }
}