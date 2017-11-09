package com.kzax1l.oiv;

/**
 * Created by Zsago on 2017/11/9.
 *
 * @author Zsago
 */
class OivAnimatorElement {
    int argbValue = -1;
    float bodyBaseLineY = -1f;
    float briefBaseLineY = -1f;

    /**
     * 是否设置了颜色值
     */
    boolean isSetArgbValue() {
        return argbValue != -1;
    }

    boolean isSetBodyBaseLineY() {
        return bodyBaseLineY != -1f;
    }

    boolean isSetBriefBaseLineY() {
        return briefBaseLineY != -1f;
    }

    void reset() {
        argbValue = -1;
        bodyBaseLineY = -1f;
        briefBaseLineY = -1f;
    }
}
