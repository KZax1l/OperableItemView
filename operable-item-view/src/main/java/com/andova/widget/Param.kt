package com.andova.widget

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class Param(gravity: @Gravity Int, align: @DrawableAlignStyle Int, chain: @DrawableChainStyle Int) {
    private var mGravity: Int = 0
    private var mDrawableAlignStyle: Int = 0
    private var mDrawableChainStyle: Int = 0

    init {
        mGravity = gravity
        mDrawableAlignStyle = align
        mDrawableChainStyle = chain
    }

    @DrawableAlignStyle
    fun align(): Int = mDrawableAlignStyle

    @DrawableChainStyle
    fun chain(): Int = mDrawableChainStyle

    @Gravity
    private fun horizontalGravity(): Int {
        if (mGravity and OIV_GRAVITY_FLAG_CENTER_HORIZONTAL != 0) return OIV_GRAVITY_FLAG_CENTER_HORIZONTAL
        return if (mGravity and OIV_GRAVITY_FLAG_RIGHT != 0) {
            OIV_GRAVITY_FLAG_RIGHT
        } else OIV_GRAVITY_FLAG_LEFT
    }

    @Gravity
    private fun verticalGravity(): Int {
        if (mGravity and OIV_GRAVITY_FLAG_CENTER_VERTICAL != 0) return OIV_GRAVITY_FLAG_CENTER_VERTICAL
        return if (mGravity and OIV_GRAVITY_FLAG_BOTTOM != 0) {
            OIV_GRAVITY_FLAG_BOTTOM
        } else OIV_GRAVITY_FLAG_TOP
    }

    @DrawableAlignStyle
    private fun endDrawableAlignStyle(): Int {
        if (mDrawableAlignStyle and OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END != 0) return OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END
        return if (mDrawableAlignStyle and OIV_DRAWABLE_ALIGN_STYLE_BODY_END != 0) {
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END
        } else OIV_DRAWABLE_ALIGN_STYLE_NORMAL
    }
}