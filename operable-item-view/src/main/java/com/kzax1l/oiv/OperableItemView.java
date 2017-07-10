package com.kzax1l.oiv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KZax1l on 2017/5/21.
 * <p>
 * 当给该控件设置state_press状态时，若没给该控件设置{@link android.view.View.OnClickListener}进行监听，
 * 则会没有点击效果产生；相反的，如果继承自{@link android.widget.Button}的话则没有这种顾虑
 *
 * @author KZax1l
 */
@SuppressWarnings("unused")
public class OperableItemView extends View {
    private Paint mBriefPaint;
    private Drawable mEndDrawable;
    private Drawable mStartDrawable;
    private Drawable mDividerDrawable;

    private int mSpace;// 左图标和文字间的间距
    private int mBriefTextSize;
    private int mBriefTextColor;
    private float mDividerHeight;
    private String mBriefText;

    private short mTextState;
    /**
     * 左右两边的图标都没绘制
     */
    private final short TEXT_STATE_NONE = 0x01;
    /**
     * 只绘制了左边图标
     */
    private final short TEXT_STATE_START = 0x02;
    /**
     * 只绘制了右边图标
     */
    private final short TEXT_STATE_END = 0x03;
    /**
     * 绘制了左右两边的图标
     */
    private final short TEXT_STATE_ALL = 0x04;

    public OperableItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initBriefPaint();
        mTextState = state();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        mBriefText = ta.getString(R.styleable.OperableItemView_oiv_briefText);
        mSpace = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_space, 0);
        mBriefTextSize = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_briefTextSize, 28);
        mBriefTextColor = ta.getColor(R.styleable.OperableItemView_oiv_briefTextColor, Color.BLACK);
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f);
        mEndDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable);
        mStartDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable);
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable);
        ta.recycle();
    }

    private void initBriefPaint() {
        mBriefPaint = new Paint();
        mBriefPaint.setColor(mBriefTextColor);
        mBriefPaint.setTextSize(mBriefTextSize);
        mBriefPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void foreachAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.OperableItemView_oiv_dividerHeight) {
                mDividerHeight = ta.getDimension(attr, 1f);
            } else if (attr == R.styleable.OperableItemView_oiv_space) {
                mSpace = ta.getDimensionPixelOffset(attr, 0);
            } else if (attr == R.styleable.OperableItemView_oiv_briefText) {
                mBriefText = ta.getString(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_briefTextSize) {
                mBriefTextSize = ta.getDimensionPixelOffset(attr, 28);
            } else if (attr == R.styleable.OperableItemView_oiv_briefTextColor) {
                mBriefTextColor = ta.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.OperableItemView_oiv_endDrawable) {
                mEndDrawable = ta.getDrawable(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_startDrawable) {
                mStartDrawable = ta.getDrawable(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_dividerDrawable) {
                mDividerDrawable = ta.getDrawable(attr);
            }
        }
        ta.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int centerY = (getBottom() - getTop()) / 2;

        drawStartDrawable(canvas, centerY, paddingLeft);

        float metricTop = centerY - mBriefPaint.getFontMetricsInt().top;
        float metricBottom = centerY + mBriefPaint.getFontMetricsInt().bottom;
        float height = Math.abs(metricBottom - metricTop);

        drawText(canvas, paddingLeft, centerY + height / 2);

        drawEndDrawable(canvas, centerY, paddingRight);

        drawDivider(canvas, paddingLeft, paddingRight);
    }

    /**
     * @param baseLineY 基线纵坐标
     */
    private void drawText(Canvas canvas, int paddingLeft, float baseLineY) {
        if (mStartDrawable == null) {
            canvas.drawText(mBriefText == null ? "" : mBriefText, paddingLeft, baseLineY, mBriefPaint);
        } else {
            canvas.drawText(mBriefText == null ? "" : mBriefText, paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth(), baseLineY, mBriefPaint);
        }
    }

    /**
     * 绘制底部的分割线
     */
    private void drawDivider(Canvas canvas, int paddingLeft, int paddingRight) {
        if (mDividerDrawable == null) return;
        if (mStartDrawable != null) {
            mDividerDrawable.setBounds(paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth(),
                    (int) (getBottom() - getTop() - mDividerHeight), getWidth() - paddingRight, getBottom() - getTop());
        } else {
            mDividerDrawable.setBounds(paddingLeft, (int) (getBottom() - getTop() - mDividerHeight),
                    getWidth() - paddingRight, getBottom() - getTop());
        }
        mDividerDrawable.draw(canvas);
    }

    /**
     * 绘制左边的图标
     *
     * @param centerY 中间线的纵坐标
     */
    private void drawStartDrawable(Canvas canvas, int centerY, int paddingLeft) {
        if (mStartDrawable == null) return;
        int startTop = centerY - mStartDrawable.getIntrinsicHeight() / 2;
        mStartDrawable.setBounds(paddingLeft, startTop,
                paddingLeft + mStartDrawable.getIntrinsicWidth(),
                startTop + mStartDrawable.getIntrinsicHeight());
        mStartDrawable.draw(canvas);
    }

    /**
     * 绘制右边的图标
     *
     * @param centerY 中间线的纵坐标
     */
    private void drawEndDrawable(Canvas canvas, int centerY, int paddingRight) {
        if (mEndDrawable == null || !mEndDrawable.isVisible()) return;
        int endTop = centerY - mEndDrawable.getIntrinsicHeight() / 2;
        mEndDrawable.setBounds(getWidth() - paddingRight - mEndDrawable.getIntrinsicWidth(),
                endTop, getWidth() - paddingRight, endTop + mEndDrawable.getIntrinsicHeight());
        mEndDrawable.draw(canvas);
    }

    private short state() {
        if (mStartDrawable == null && mEndDrawable == null) return TEXT_STATE_NONE;
        if (mStartDrawable != null && mEndDrawable == null) return TEXT_STATE_START;
        if (mStartDrawable == null) return TEXT_STATE_END;
        return TEXT_STATE_ALL;
    }

    public void setEndDrawableVisible(boolean visible) {
        if (mEndDrawable == null) return;
        mEndDrawable.setVisible(visible, false);
    }

    public boolean isEndDrawableVisible() {
        return mEndDrawable != null && mEndDrawable.isVisible();
    }
}
