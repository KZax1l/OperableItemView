package com.kzax1l.oiv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by KZax1l on 2017/5/21.
 * <p>
 * 后续可继承{@link android.view.View}而不是{@link android.widget.TextView}，然后解决点击效果的问题
 *
 * @author KZax1l
 */
@SuppressWarnings("unused")
public class OperableItemView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private Drawable mEndDrawable;
    private Drawable mStartDrawable;
    private Drawable mDividerDrawable;

    private int mSpace;// 左图标和文字间的间距
    private int mTextSize;
    private int mTextColor;
    private float mDividerHeight;
    private String mText;
    private boolean mDividerEnable = false;
    private boolean mShowEndDrawable = false;
    private boolean mShowStartDrawable = false;

    public OperableItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        mText = ta.getString(R.styleable.OperableItemView_oiv_text);
        mSpace = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_space, 0);
        mTextSize = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_textSize, 28);
        mTextColor = ta.getColor(R.styleable.OperableItemView_oiv_textColor, Color.BLACK);
        mDividerEnable = ta.getBoolean(R.styleable.OperableItemView_oiv_dividerEnable, false);
        mShowEndDrawable = ta.getBoolean(R.styleable.OperableItemView_oiv_showEndDrawable, false);
        mShowStartDrawable = ta.getBoolean(R.styleable.OperableItemView_oiv_showStartDrawable, false);
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f);
        mEndDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable);
        mStartDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable);
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable);
        ta.recycle();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void foreachAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.OperableItemView_oiv_dividerEnable) {
                mDividerEnable = ta.getBoolean(attr, false);
            } else if (attr == R.styleable.OperableItemView_oiv_dividerHeight) {
                mDividerHeight = ta.getDimension(attr, 1f);
            } else if (attr == R.styleable.OperableItemView_oiv_showEndDrawable) {
                mShowEndDrawable = ta.getBoolean(attr, false);
            } else if (attr == R.styleable.OperableItemView_oiv_showStartDrawable) {
                mShowStartDrawable = ta.getBoolean(attr, false);
            } else if (attr == R.styleable.OperableItemView_oiv_space) {
                mSpace = ta.getDimensionPixelOffset(attr, 0);
            } else if (attr == R.styleable.OperableItemView_oiv_text) {
                mText = ta.getString(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_textSize) {
                mTextSize = ta.getDimensionPixelOffset(attr, 28);
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

        if (mShowStartDrawable && mStartDrawable != null) {
            int startTop = centerY - mStartDrawable.getIntrinsicHeight() / 2;
            mStartDrawable.setBounds(paddingLeft, startTop,
                    paddingLeft + mStartDrawable.getIntrinsicWidth(),
                    startTop + mStartDrawable.getIntrinsicHeight());
            mStartDrawable.draw(canvas);
        }

        float metricTop = centerY - mPaint.getFontMetricsInt().top;
        float metricBottom = centerY + mPaint.getFontMetricsInt().bottom;
        float height = Math.abs(metricBottom - metricTop);

        drawText(canvas, paddingLeft, centerY + height / 2);

        if (mShowEndDrawable && mEndDrawable != null && mEndDrawable.isVisible()) {
            int endTop = centerY - mEndDrawable.getIntrinsicHeight() / 2;
            mEndDrawable.setBounds(getWidth() - paddingRight - mEndDrawable.getIntrinsicWidth(),
                    endTop, getWidth() - paddingRight, endTop + mEndDrawable.getIntrinsicHeight());
            mEndDrawable.draw(canvas);
        }

        if (mDividerEnable && mDividerDrawable != null) {
            if (!mShowStartDrawable) {
                mDividerDrawable.setBounds(paddingLeft, (int) (getBottom() - getTop() - mDividerHeight),
                        getWidth() - paddingRight, getBottom() - getTop());
            } else if (mStartDrawable != null) {
                mDividerDrawable.setBounds(paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth(),
                        (int) (getBottom() - getTop() - mDividerHeight), getWidth() - paddingRight, getBottom() - getTop());
            } else {
                mDividerDrawable.setBounds(paddingLeft + mSpace, (int) (getBottom() - getTop() - mDividerHeight),
                        getWidth() - paddingRight, getBottom() - getTop());
            }
            mDividerDrawable.draw(canvas);
        }
    }

    /**
     * @param baseLineY 基线纵坐标
     */
    private void drawText(Canvas canvas, int paddingLeft, float baseLineY) {
        if (!mShowStartDrawable) {
            canvas.drawText(mText == null ? "" : mText, paddingLeft, baseLineY, mPaint);
        } else if (mStartDrawable == null) {
            canvas.drawText(mText == null ? "" : mText, paddingLeft + mSpace, baseLineY, mPaint);
        } else {
            canvas.drawText(mText == null ? "" : mText, paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth(), baseLineY, mPaint);
        }
    }

    public void setEndDrawableVisible(boolean visible) {
        if (mEndDrawable == null) return;
        mEndDrawable.setVisible(visible, false);
    }

    public boolean isEndDrawableVisible() {
        return mEndDrawable != null && mEndDrawable.isVisible();
    }
}
