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

    fun intrinsicWidth(): Int = mStartDrawable?.intrinsicWidth ?: 0
    fun intrinsicHeight(): Int = mStartDrawable?.intrinsicHeight ?: 0

    private fun width(): Int {
        if (mStartDrawableWidth > 0) return mStartDrawableWidth
        mBriefStcLayout ?: return mStartDrawable?.intrinsicWidth ?: 0
        mBodyStcLayout ?: return mStartDrawable?.intrinsicWidth ?: 0
        mStartDrawable ?: return 0
        val w = mStartDrawable?.intrinsicWidth?.toFloat() ?: 0f
        val h = mStartDrawable?.intrinsicHeight?.toFloat() ?: 0f
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> (h / w * mBriefStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> (h / w * mBodyStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> w.toInt()
            else -> w.toInt()
        }
    }

    private fun height(): Int {
        if (mStartDrawableHeight > 0) return mStartDrawableHeight
        mBriefStcLayout ?: return mStartDrawable?.intrinsicHeight ?: 0
        mBodyStcLayout ?: return mStartDrawable?.intrinsicHeight ?: 0
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> mBriefStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> mBodyStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> mStartDrawable?.intrinsicHeight ?: 0
            else -> mStartDrawable?.intrinsicHeight ?: 0
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