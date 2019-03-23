package com.andova.widget

import androidx.annotation.IntDef

const val OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE = 10
const val OIV_DRAWABLE_CHAIN_STYLE_PACKED = 20

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
@IntDef(value = [OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE, OIV_DRAWABLE_CHAIN_STYLE_PACKED])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DrawableChainStyle