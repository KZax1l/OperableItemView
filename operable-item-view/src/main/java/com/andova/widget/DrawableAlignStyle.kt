package com.andova.widget

import androidx.annotation.IntDef

const val OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START = 0x01
const val OIV_DRAWABLE_ALIGN_STYLE_BODY_START = 0x02
const val OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END = 0x04
const val OIV_DRAWABLE_ALIGN_STYLE_BODY_END = 0x08
const val OIV_DRAWABLE_ALIGN_STYLE_NORMAL = 0x10

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
@IntDef(value = [OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START, OIV_DRAWABLE_ALIGN_STYLE_BODY_START,
    OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END, OIV_DRAWABLE_ALIGN_STYLE_BODY_END, OIV_DRAWABLE_ALIGN_STYLE_NORMAL])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DrawableAlignStyle