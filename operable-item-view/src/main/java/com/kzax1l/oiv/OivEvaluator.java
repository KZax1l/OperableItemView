package com.kzax1l.oiv;

import android.animation.TypeEvaluator;

/**
 * Created by Zsago on 2017/11/9.
 *
 * @author Zsago
 */
class OivEvaluator implements TypeEvaluator<OivAnimatorElement> {
    private int argb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }

    private float number(float fraction, float startValue, float endValue) {
        return startValue + fraction * (endValue - startValue);
    }

    @Override
    public OivAnimatorElement evaluate(float fraction, OivAnimatorElement startElement, OivAnimatorElement endElement) {
        OivAnimatorElement element = new OivAnimatorElement();
        if (startElement.isSetArgbValue() && endElement.isSetArgbValue()) {
            element.argbValue = argb(fraction, startElement.argbValue, endElement.argbValue);
        }
        if (startElement.isSetBodyBaseLineY() && endElement.isSetBodyBaseLineY()) {
            element.bodyBaseLineY = number(fraction, startElement.bodyBaseLineY, endElement.bodyBaseLineY);
        }
        if (startElement.isSetBriefBaseLineY() && endElement.isSetBriefBaseLineY()) {
            element.briefBaseLineY = number(fraction, startElement.briefBaseLineY, endElement.briefBaseLineY);
        }
        return element;
    }
}
