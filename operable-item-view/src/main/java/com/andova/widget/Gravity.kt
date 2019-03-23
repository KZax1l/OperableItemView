package com.andova.widget

import androidx.annotation.IntDef

const val OIV_GRAVITY_FLAG_LEFT = 0x01
const val OIV_GRAVITY_FLAG_TOP = 0x02
const val OIV_GRAVITY_FLAG_RIGHT = 0x04
const val OIV_GRAVITY_FLAG_BOTTOM = 0x08
const val OIV_GRAVITY_FLAG_CENTER = 0x30
const val OIV_GRAVITY_FLAG_CENTER_VERTICAL = 0x10
const val OIV_GRAVITY_FLAG_CENTER_HORIZONTAL = 0x20

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
@IntDef(value = [OIV_GRAVITY_FLAG_LEFT, OIV_GRAVITY_FLAG_TOP, OIV_GRAVITY_FLAG_RIGHT,
    OIV_GRAVITY_FLAG_BOTTOM, OIV_GRAVITY_FLAG_CENTER, OIV_GRAVITY_FLAG_CENTER_VERTICAL,
    OIV_GRAVITY_FLAG_CENTER_HORIZONTAL])
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Gravity