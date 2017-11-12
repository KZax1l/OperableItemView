package com.kzax1l.oiv;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.kzax1l.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_CENTER;
import static com.kzax1l.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_LEFT;
import static com.kzax1l.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_RIGHT;

/**
 * Created by KZax1l on 2017/5/21.
 * <p>
 * 当给该控件设置state_press状态时，若没给该控件设置{@link android.view.View.OnClickListener}进行监听，
 * 则会没有点击效果产生；相反的，如果继承自{@link android.widget.Button}的话则没有这种顾虑
 * <p>目前文字绘制是垂直居中的，下一步可以考虑增加设置文字绘制在顶部中间还是底部</p>
 * <p>后续需补充对{@link android.graphics.drawable.StateListDrawable}的宽高测量</p>
 *
 * @author KZax1l
 */
public class OperableItemView extends View implements ValueAnimator.AnimatorUpdateListener {
    private TextPaint mBodyPaint;
    private TextPaint mBriefPaint;
    private Drawable mEndDrawable;
    private Drawable mStartDrawable;
    private Drawable mDividerDrawable;
    private StaticLayout mBodyStcLayout;
    private StaticLayout mBriefStcLayout;

    private int mSpace;// 左图标和文字间的间距
    private int mTextInterval;// 摘要文字和正文文字之间的间距
    private int mBodyTextSize;
    private int mBriefTextSize;
    private int mBodyTextColor;
    private int mBriefTextColor;
    private float mDividerHeight;
    private String mBriefText;
    private String mBodyText;
    private boolean mBriefTextEnable = true;
    private boolean mBodyTextEnable = true;
    @Deprecated
    private int mTextMinHeight;

    private boolean mRefresh = true;
    private boolean mAnimate = false;
    private int measureHeightMode;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mMaxTextWidth;

    private int mBriefHorizontalGravity;
    private int mBodyHorizontalGravity;

    private OivEvaluator mEvaluator = new OivEvaluator();
    private OivAnimatorElement mCurrentAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mEndUpdateAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mEndPercentAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mStartUpdateAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mStartPercentAnimElem = new OivAnimatorElement();

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

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        OivAnimatorElement element = (OivAnimatorElement) valueAnimator.getAnimatedValue();
        updateAnimation(element);
    }

    @IntDef({OIV_GRAVITY_FLAG_LEFT, OIV_GRAVITY_FLAG_CENTER, OIV_GRAVITY_FLAG_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Gravity {
        int OIV_GRAVITY_FLAG_LEFT = 1;
        int OIV_GRAVITY_FLAG_CENTER = 2;
        int OIV_GRAVITY_FLAG_RIGHT = 3;
    }

    public OperableItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initBriefPaint();
        initBodyPaint();
        mTextState = state();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        mBodyText = ta.getString(R.styleable.OperableItemView_oiv_bodyText);
        mBriefText = ta.getString(R.styleable.OperableItemView_oiv_briefText);
        mSpace = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_space, 0);
        mTextInterval = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_textInterval, 0);
        mBriefTextSize = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_briefTextSize, 28);
        mBodyTextSize = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_bodyTextSize, 28);
        mBriefTextColor = ta.getColor(R.styleable.OperableItemView_oiv_briefTextColor, Color.BLACK);
        mBodyTextColor = ta.getColor(R.styleable.OperableItemView_oiv_bodyTextColor, Color.BLACK);
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f);
        mEndDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable);
        mStartDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable);
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable);
        mBriefHorizontalGravity = ta.getInt(R.styleable.OperableItemView_oiv_briefHorizontalGravity, OIV_GRAVITY_FLAG_LEFT);
        mBodyHorizontalGravity = ta.getInt(R.styleable.OperableItemView_oiv_bodyHorizontalGravity, OIV_GRAVITY_FLAG_LEFT);
        ta.recycle();
    }

    private void initBriefPaint() {
        mBriefPaint = new TextPaint();
        mBriefPaint.setColor(mBriefTextColor);
        mCurrentAnimElem.briefTextColor = mBriefTextColor;
        mBriefPaint.setTextSize(mBriefTextSize);
        switch (mBriefHorizontalGravity) {
            case OIV_GRAVITY_FLAG_LEFT:
                mBriefPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case OIV_GRAVITY_FLAG_CENTER:
                mBriefPaint.setTextAlign(Paint.Align.CENTER);
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                mBriefPaint.setTextAlign(Paint.Align.RIGHT);
                break;
        }
        mBriefPaint.setAntiAlias(true);
    }

    private void initBodyPaint() {
        mBodyPaint = new TextPaint();
        mBodyPaint.setColor(mBodyTextColor);
        mCurrentAnimElem.bodyTextColor = mBodyTextColor;
        mBodyPaint.setTextSize(mBodyTextSize);
        switch (mBodyHorizontalGravity) {
            case OIV_GRAVITY_FLAG_LEFT:
                mBodyPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case OIV_GRAVITY_FLAG_CENTER:
                mBodyPaint.setTextAlign(Paint.Align.CENTER);
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                mBodyPaint.setTextAlign(Paint.Align.RIGHT);
                break;
        }
        mBodyPaint.setAntiAlias(true);
    }

    @SuppressWarnings("unused")
    private void foreachAttrs(Context context, AttributeSet attrs) {
        mTextMinHeight = getResources().getDimensionPixelOffset(R.dimen.dimen_oiv_min_height);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.OperableItemView_oiv_dividerHeight) {
                mDividerHeight = ta.getDimension(attr, 1f);
            } else if (attr == R.styleable.OperableItemView_oiv_space) {
                mSpace = ta.getDimensionPixelOffset(attr, 0);
            } else if (attr == R.styleable.OperableItemView_oiv_textInterval) {
                mTextInterval = ta.getDimensionPixelOffset(attr, 0);
            } else if (attr == R.styleable.OperableItemView_oiv_briefText) {
                mBriefText = ta.getString(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_bodyText) {
                mBodyText = ta.getString(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_briefTextSize) {
                mBriefTextSize = ta.getDimensionPixelOffset(attr, 28);
            } else if (attr == R.styleable.OperableItemView_oiv_bodyTextSize) {
                mBodyTextSize = ta.getDimensionPixelOffset(attr, 28);
            } else if (attr == R.styleable.OperableItemView_oiv_briefTextColor) {
                mBriefTextColor = ta.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.OperableItemView_oiv_bodyTextColor) {
                mBodyTextColor = ta.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.OperableItemView_oiv_endDrawable) {
                mEndDrawable = ta.getDrawable(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_startDrawable) {
                mStartDrawable = ta.getDrawable(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_dividerDrawable) {
                mDividerDrawable = ta.getDrawable(attr);
            } else if (attr == R.styleable.OperableItemView_oiv_briefHorizontalGravity) {
                mBriefHorizontalGravity = ta.getInt(attr, OIV_GRAVITY_FLAG_LEFT);
            } else if (attr == R.styleable.OperableItemView_oiv_bodyHorizontalGravity) {
                mBodyHorizontalGravity = ta.getInt(attr, OIV_GRAVITY_FLAG_LEFT);
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
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        initStaticLayout();

        Drawable background = getBackground();
        if (background instanceof BitmapDrawable
                || background instanceof GradientDrawable
                && background.getIntrinsicWidth() > 0
                && background.getIntrinsicHeight() > 0) {
            setMeasuredDimension(background.getIntrinsicWidth(), background.getIntrinsicHeight());
            return;
        }

        float height = mTextMinHeight;
        switch (measureHeightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:// 父视图不对子视图施加任何限制，子视图可以得到任意想要的大小
                if (mStartDrawable != null && mStartDrawable.getIntrinsicHeight() > height) {
                    height = mStartDrawable.getIntrinsicHeight();
                }
                if (mEndDrawable != null && mEndDrawable.getIntrinsicHeight() > height) {
                    height = mEndDrawable.getIntrinsicHeight();
                }
                float lineHeight = (TextUtils.isEmpty(mBriefText) ? 0 : getTextHeight(mBriefPaint))
                        + mTextInterval + (TextUtils.isEmpty(mBodyText) ? 0 : getTextHeight(mBodyPaint));
                if (lineHeight > height) {
                    height = lineHeight;
                }
                float linesHeight = (mBriefStcLayout == null || TextUtils.isEmpty(mBriefText) ? 0 : mBriefStcLayout.getHeight())
                        + (mBodyStcLayout == null || TextUtils.isEmpty(mBodyText) ? 0 : mBodyStcLayout.getHeight());
                if (linesHeight > lineHeight) {
                    height = linesHeight;
                }
                break;
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) height + mPaddingTop + mPaddingBottom);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int centerY = (getBottom() - getTop()) / 2;
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        if (!mCurrentAnimElem.isSetBodyBaseLineY()) {
            mCurrentAnimElem.bodyBaseLineY = bodyBaseLineY();
        }
        if (!mCurrentAnimElem.isSetBriefBaseLineY()) {
            mCurrentAnimElem.briefBaseLineY = briefBaseLineY();
        }

        drawBodyText(canvas, paddingLeft, mAnimate);
        drawBriefText(canvas, paddingLeft, mAnimate);

        drawStartDrawable(canvas, centerY, paddingLeft);
        drawEndDrawable(canvas, centerY, paddingRight);
        drawDivider(canvas, paddingLeft, paddingRight);
    }

    private void initStaticLayout() {
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = new StaticLayout(mBriefText == null ? "" : mBriefText,
                    mBriefPaint, maxTextWidth(), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = new StaticLayout(mBodyText == null ? "" : mBodyText,
                    mBodyPaint, maxTextWidth(), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        }
    }

    /**
     * @deprecated use {@link #initStaticLayout()} instead
     */
    private boolean shouldRequestLayout(Canvas canvas) {
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = new StaticLayout(mBriefText == null ? "" : mBriefText,
                    mBriefPaint, maxTextWidth(canvas), Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true);
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = new StaticLayout(mBodyText == null ? "" : mBodyText,
                    mBodyPaint, maxTextWidth(canvas), Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true);
        }
        if (mRefresh) {
            mRefresh = false;
            if (measureHeightMode == MeasureSpec.EXACTLY) return false;
            requestLayout();
            return true;
        }
        return false;
    }

    private void drawBriefText(Canvas canvas, int paddingLeft, boolean animate) {
        if (TextUtils.isEmpty(mBriefText)) return;
        int baseLineX = 0;
        switch (mBriefHorizontalGravity) {
            case OIV_GRAVITY_FLAG_LEFT:
                baseLineX = mStartDrawable == null ? paddingLeft : paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth();
                break;
            case OIV_GRAVITY_FLAG_CENTER:
                baseLineX = canvas.getWidth() / 2;
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                break;
        }
        drawBriefText(canvas, baseLineX);
    }

    /**
     * 绘制摘要文字
     * <p>用{@link StaticLayout}绘制文字时，绘制的文字是在基线下方（当然，这个说法不是非常精确），
     * 而用{@link Canvas}绘制文字时则是绘制在基线上方</p>
     */
    private void drawBriefText(Canvas canvas, int baseLineX) {
        canvas.save();
        canvas.translate(baseLineX, mCurrentAnimElem.briefBaseLineY);
        mBriefStcLayout.draw(canvas);
        canvas.restore();
    }

    private void drawBodyText(Canvas canvas, int paddingLeft, boolean animate) {
        if (TextUtils.isEmpty(mBodyText)) return;
        int baseLineX = 0;
        switch (mBodyHorizontalGravity) {
            case OIV_GRAVITY_FLAG_LEFT:
                baseLineX = mStartDrawable == null ? paddingLeft : paddingLeft + mSpace + mStartDrawable.getIntrinsicWidth();
                break;
            case OIV_GRAVITY_FLAG_CENTER:
                baseLineX = canvas.getWidth() / 2;
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                break;
        }
        drawBodyText(canvas, baseLineX);
    }

    /**
     * 绘制正文文字
     * <p>用{@link StaticLayout}绘制文字时，绘制的文字是在基线下方（当然，这个说法不是非常精确），
     * 而用{@link Canvas}绘制文字时则是绘制在基线上方</p>
     */
    private void drawBodyText(Canvas canvas, int baseLineX) {
        canvas.save();
        canvas.translate(baseLineX, mCurrentAnimElem.bodyBaseLineY);
        mBodyStcLayout.draw(canvas);
        canvas.restore();
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

    /**
     * 获取文字高度
     */
    private float getTextHeight(Paint paint) {
        return paint.descent() - paint.ascent();
    }

    private short state() {
        if (mStartDrawable == null && mEndDrawable == null) return TEXT_STATE_NONE;
        if (mStartDrawable != null && mEndDrawable == null) return TEXT_STATE_START;
        if (mStartDrawable == null) return TEXT_STATE_END;
        return TEXT_STATE_ALL;
    }

    private int maxTextWidth() {
        if (!mRefresh && mMaxTextWidth > 0) return mMaxTextWidth;
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        mMaxTextWidth = getResources().getDisplayMetrics().widthPixels
                - getPaddingLeft() - getPaddingRight() - mSpace
                - marginLayoutParams.leftMargin - marginLayoutParams.rightMargin
                - (mStartDrawable == null ? 0 : mStartDrawable.getIntrinsicWidth())
                - (mEndDrawable == null ? 0 : mEndDrawable.getIntrinsicWidth());
        return mMaxTextWidth;
    }

    /**
     * 获取能绘制文本的最大宽度
     * <p>用{@link Canvas#getWidth()}获取到的宽度值是去除了间距的值</p>
     *
     * @see TextPaint#measureText(char[], int, int)
     * @see StaticLayout#getDesiredWidth(CharSequence, TextPaint)
     * @deprecated use {@link #maxTextWidth()} instead
     */
    private int maxTextWidth(Canvas canvas) {
        return canvas.getWidth() - getPaddingLeft() - getPaddingRight() - mSpace
                - (mStartDrawable == null ? 0 : mStartDrawable.getIntrinsicWidth())
                - (mEndDrawable == null ? 0 : mEndDrawable.getIntrinsicWidth());
    }

    /**
     * 绘制正文文本时，画布需要平移到的纵坐标值
     */
    private int briefBaseLineY() {
        if (TextUtils.isEmpty(mBodyText) || !mBodyTextEnable) {
            return getHeight() / 2 - mBriefStcLayout.getHeight() / 2;
        } else {
            return (getHeight() - mTextInterval
                    - mBriefStcLayout.getHeight()
                    - mBodyStcLayout.getHeight()) / 2;
        }
    }

    /**
     * 绘制摘要文本时，画布需要平移到的纵坐标值
     */
    private int bodyBaseLineY() {
        if (TextUtils.isEmpty(mBriefText) || !mBriefTextEnable) {
            return getHeight() / 2 - mBodyStcLayout.getHeight() / 2;
        } else {
            return getHeight()
                    - (getHeight() - mTextInterval
                    - mBriefStcLayout.getHeight()
                    - mBodyStcLayout.getHeight()) / 2
                    - mBodyStcLayout.getHeight();
        }
    }

    public void setEndDrawableVisible(boolean visible) {
        if (mEndDrawable == null) return;
        mAnimate = false;
        mEndDrawable.setVisible(visible, false);
    }

    public boolean isEndDrawableVisible() {
        return mEndDrawable != null && mEndDrawable.isVisible();
    }

    public void setBodyText(String bodyText) {
        if (TextUtils.isEmpty(bodyText)) return;
        mBodyText = bodyText;
        mAnimate = false;
        mRefresh = true;
        requestLayout();
    }

    public void setBodyTextColor(int bodyTextColor) {
        mBodyTextColor = bodyTextColor;
        mBodyPaint.setColor(bodyTextColor);
        mCurrentAnimElem.bodyTextColor = bodyTextColor;
        mAnimate = false;
        invalidate();
    }

    public void setBodyTextSize(@DimenRes int bodyTextSize) {
        mBodyTextSize = bodyTextSize;
        mBodyPaint.setTextSize(bodyTextSize);
        mAnimate = false;
        mRefresh = true;
        requestLayout();
    }

    public void setBriefText(String briefText) {
        if (TextUtils.isEmpty(briefText)) return;
        mBriefText = briefText;
        mAnimate = false;
        mRefresh = true;
        requestLayout();
    }

    public void setBriefTextColor(int briefTextColor) {
        mBriefTextColor = briefTextColor;
        mBriefPaint.setColor(briefTextColor);
        mCurrentAnimElem.briefTextColor = briefTextColor;
        mAnimate = false;
        invalidate();
    }

    public void setBriefTextSize(@DimenRes int briefTextSize) {
        mBriefTextSize = briefTextSize;
        mBriefPaint.setTextSize(briefTextSize);
        mAnimate = false;
        mRefresh = true;
        requestLayout();
    }

    public void enableBriefText(boolean enable, boolean animate) {
        if (mBriefTextEnable == enable) return;
        mBriefTextEnable = enable;
        mAnimate = animate;
        if (animate) {
            startAnimation();
        } else {
            invalidate();
        }
    }

    /**
     * @param percent [0,1]
     */
    public void enableBriefText(boolean enable, float percent) {
        if (percent < 0) percent = 0f;
        if (percent > 1) percent = 1f;
        boolean reset = mBriefTextEnable != enable;
        mBriefTextEnable = enable;
        mAnimate = true;
        if (reset) readyAnimation(mStartPercentAnimElem, mEndPercentAnimElem);
        updateAnimation(mEvaluator.evaluate(percent, mStartPercentAnimElem, mEndPercentAnimElem));
    }

    public void enableBodyText(boolean enable, boolean animate) {
        if (mBodyTextEnable == enable) return;
        mBodyTextEnable = enable;
        mAnimate = animate;
        if (animate) {
            startAnimation();
        } else {
            invalidate();
        }
    }

    /**
     * @param percent [0,1]
     */
    public void enableBodyText(boolean enable, float percent) {
        if (percent < 0) percent = 0f;
        if (percent > 1) percent = 1f;
        boolean reset = mBodyTextEnable != enable;
        mBodyTextEnable = enable;
        mAnimate = true;
        if (reset) readyAnimation(mStartPercentAnimElem, mEndPercentAnimElem);
        updateAnimation(mEvaluator.evaluate(percent, mStartPercentAnimElem, mEndPercentAnimElem));
    }

    private int convertToTrans(int colorValue) {
        int colorR = (colorValue >> 16) & 0xff;
        int colorG = (colorValue >> 8) & 0xff;
        int colorB = colorValue & 0xff;
        return (colorR << 16) | (colorG << 8) | colorB;
    }

    private void readyAnimation(OivAnimatorElement startElem, OivAnimatorElement endElem) {
        endElem.reset();
        startElem.reset();
        startElem.bodyTextColor = mCurrentAnimElem.bodyTextColor;
        startElem.bodyBaseLineY = mCurrentAnimElem.bodyBaseLineY;
        startElem.briefTextColor = mCurrentAnimElem.briefTextColor;
        startElem.briefBaseLineY = mCurrentAnimElem.briefBaseLineY;
        endElem.bodyTextColor = mBodyTextEnable ? mBodyTextColor : convertToTrans(mBodyTextColor);
        endElem.briefTextColor = mBriefTextEnable ? mBriefTextColor : convertToTrans(mBriefTextColor);
        endElem.bodyBaseLineY = bodyBaseLineY();
        endElem.briefBaseLineY = briefBaseLineY();
    }

    private void updateAnimation(OivAnimatorElement element) {
        if (element.isSetBodyBaseLineY()) {
            mCurrentAnimElem.bodyBaseLineY = element.bodyBaseLineY;
        }
        if (element.isSetBriefBaseLineY()) {
            mCurrentAnimElem.briefBaseLineY = element.briefBaseLineY;
        }
        if (element.isSetBodyTextColor()) {
            mCurrentAnimElem.bodyTextColor = element.bodyTextColor;
            mBodyPaint.setColor(mCurrentAnimElem.bodyTextColor);
        }
        if (element.isSetBriefTextColor()) {
            mCurrentAnimElem.briefTextColor = element.briefTextColor;
            mBriefPaint.setColor(mCurrentAnimElem.briefTextColor);
        }
        invalidate();
    }

    private void startAnimation() {
        readyAnimation(mStartUpdateAnimElem, mEndUpdateAnimElem);
        ValueAnimator animator = ValueAnimator.ofObject(mEvaluator, mStartUpdateAnimElem, mEndUpdateAnimElem);
        animator.addUpdateListener(this);
        animator.setDuration(300);
        animator.start();
    }
}
