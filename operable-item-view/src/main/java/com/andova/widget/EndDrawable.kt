package com.andova.widget

import android.graphics.drawable.Drawable

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class EndDrawable(width: Int, height: Int, drawable: Drawable?, private val text: Text, private val param: Param) {
    private var mEndDrawableWidth: Int = 0
    private var mEndDrawableHeight: Int = 0
    private var mEndDrawable: Drawable? = null

    init {
        mEndDrawable = drawable
        mEndDrawableWidth = width
        mEndDrawableHeight = height
    }

    private inline fun intrinsicWidth(): Int = mEndDrawable?.intrinsicWidth ?: 0
    private inline fun intrinsicHeight(): Int = mEndDrawable?.intrinsicHeight ?: 0
    private inline fun intrinsicWidthF(): Float = mEndDrawable?.intrinsicWidth?.toFloat() ?: 0f
    private inline fun intrinsicHeightF(): Float = mEndDrawable?.intrinsicHeight?.toFloat() ?: 0f

    fun width(): Int {
        if (mEndDrawableWidth > 0) return mEndDrawableWidth
        text.briefStcLayout() ?: return intrinsicWidth()
        text.bodyStcLayout() ?: return intrinsicWidth()
        mEndDrawable ?: return 0
        val w = intrinsicWidthF()
        val h = intrinsicHeightF()
        return when (param.align()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END -> (h / w * text.briefStcLayoutH()).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END -> (h / w * text.bodyStcLayoutH()).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> w.toInt()
            else -> w.toInt()
        }
    }

    fun height(): Int {
        if (mEndDrawableHeight > 0) return mEndDrawableHeight
        text.briefStcLayout() ?: return intrinsicHeight()
        text.bodyStcLayout() ?: return intrinsicHeight()
        return when (param.align()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END -> return text.briefStcLayoutH()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END -> return text.bodyStcLayoutH()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> return mEndDrawable?.intrinsicHeight ?: 0
            else -> mEndDrawable?.intrinsicHeight ?: 0
        }
    }

    fun isVisible(): Boolean = mEndDrawable?.isVisible == false
}