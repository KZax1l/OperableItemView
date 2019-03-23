package com.andova.widget

import android.graphics.drawable.Drawable

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class StartDrawable(width: Int, height: Int, drawable: Drawable?, private val text: Text, private val param: Param) {
    private var mStartDrawableWidth: Int = 0
    private var mStartDrawableHeight: Int = 0
    private var mStartDrawable: Drawable? = null

    init {
        mStartDrawable = drawable
        mStartDrawableWidth = width
        mStartDrawableHeight = height
    }

    private inline fun intrinsicWidth(): Int = mStartDrawable?.intrinsicWidth ?: 0
    private inline fun intrinsicHeight(): Int = mStartDrawable?.intrinsicHeight ?: 0
    private inline fun intrinsicWidthF(): Float = mStartDrawable?.intrinsicWidth?.toFloat() ?: 0f
    private inline fun intrinsicHeightF(): Float = mStartDrawable?.intrinsicHeight?.toFloat() ?: 0f

    fun width(): Int {
        if (mStartDrawableWidth > 0) return mStartDrawableWidth
        text.briefStcLayout() ?: return intrinsicWidth()
        text.bodyStcLayout() ?: return intrinsicWidth()
        mStartDrawable ?: return 0
        val w = intrinsicWidthF()
        val h = intrinsicHeightF()
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> (h / w * text.briefStcLayoutH()).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> (h / w * text.bodyStcLayoutH()).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> w.toInt()
            else -> w.toInt()
        }
    }

    fun height(): Int {
        if (mStartDrawableHeight > 0) return mStartDrawableHeight
        text.briefStcLayout() ?: return intrinsicHeight()
        text.bodyStcLayout() ?: return intrinsicHeight()
        return when (startDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START -> text.briefStcLayoutH()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START -> text.bodyStcLayoutH()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> intrinsicHeight()
            else -> intrinsicHeight()
        }
    }

    @DrawableAlignStyle
    private fun startDrawableAlignStyle(): Int {
        if (param.align() and OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START != 0) return OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START
        return if (param.align() and OIV_DRAWABLE_ALIGN_STYLE_BODY_START != 0) {
            OIV_DRAWABLE_ALIGN_STYLE_BODY_START
        } else OIV_DRAWABLE_ALIGN_STYLE_NORMAL
    }
}