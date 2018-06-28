package com.andova.oiv;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.andova.oiv.OperableItemView.DrawableAlignStyle.OIV_DRAWABLE_ALIGN_STYLE_BODY_END;
import static com.andova.oiv.OperableItemView.DrawableAlignStyle.OIV_DRAWABLE_ALIGN_STYLE_BODY_START;
import static com.andova.oiv.OperableItemView.DrawableAlignStyle.OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END;
import static com.andova.oiv.OperableItemView.DrawableAlignStyle.OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START;
import static com.andova.oiv.OperableItemView.DrawableAlignStyle.OIV_DRAWABLE_ALIGN_STYLE_NORMAL;
import static com.andova.oiv.OperableItemView.DrawableChainStyle.OIV_DRAWABLE_CHAIN_STYLE_PACKED;
import static com.andova.oiv.OperableItemView.DrawableChainStyle.OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_BOTTOM;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_CENTER;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_CENTER_HORIZONTAL;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_CENTER_VERTICAL;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_LEFT;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_RIGHT;
import static com.andova.oiv.OperableItemView.Gravity.OIV_GRAVITY_FLAG_TOP;

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

    private int mDrawablePadding;// 图标和文字间的间距
    private int mTextInterval;// 摘要文字和正文文字之间的间距
    private int mBodyTextColor;
    private int mBriefTextColor;
    private float mDividerHeight;
    private String mBriefText;
    private String mBodyText;
    private boolean mBriefTextEnable = true;
    private boolean mBodyTextEnable = true;

    private boolean mRefresh = true;
    private boolean mAnimate = false;
    private int measureHeightMode;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mMaxTextWidth;

    private int mGravity;
    private int mDrawableAlignStyle;
    private int mDrawableChainStyle;

    private OivEvaluator mEvaluator = new OivEvaluator();
    private OivAnimatorElement mCurrentAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mEndUpdateAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mEndPercentAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mStartUpdateAnimElem = new OivAnimatorElement();
    private OivAnimatorElement mStartPercentAnimElem = new OivAnimatorElement();

    private Paint mShadowPaint;
    private int mShadowColor;
    private int mShadowDx;
    private int mShadowDy;
    private int mShadowRadius;
    private int mShadowSide;
    private final int FLAG_SHADOW_SIDE_LEFT = 0x0001;
    private final int FLAG_SHADOW_SIDE_RIGHT = 0x0010;
    private final int FLAG_SHADOW_SIDE_TOP = 0x0100;
    private final int FLAG_SHADOW_SIDE_BOTTOM = 0x1000;
    private final int FLAG_SHADOW_SIDE_ALL = 0x1111;
    private RectF mRectF = new RectF();

    private int mX, mY;
    private OnClickDrawableListener mListener;
    private final String TAG = OperableItemView.class.getSimpleName();

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        OivAnimatorElement element = (OivAnimatorElement) valueAnimator.getAnimatedValue();
        updateAnimation(element);
    }

    @IntDef({OIV_GRAVITY_FLAG_LEFT, OIV_GRAVITY_FLAG_TOP, OIV_GRAVITY_FLAG_RIGHT, OIV_GRAVITY_FLAG_BOTTOM,
            OIV_GRAVITY_FLAG_CENTER, OIV_GRAVITY_FLAG_CENTER_VERTICAL, OIV_GRAVITY_FLAG_CENTER_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    @interface Gravity {
        int OIV_GRAVITY_FLAG_LEFT = 0x01;
        int OIV_GRAVITY_FLAG_TOP = 0x02;
        int OIV_GRAVITY_FLAG_RIGHT = 0x04;
        int OIV_GRAVITY_FLAG_BOTTOM = 0x08;
        int OIV_GRAVITY_FLAG_CENTER = 0x30;
        int OIV_GRAVITY_FLAG_CENTER_VERTICAL = 0x10;
        int OIV_GRAVITY_FLAG_CENTER_HORIZONTAL = 0x20;
    }

    @IntDef({OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE, OIV_DRAWABLE_CHAIN_STYLE_PACKED})
    @Retention(RetentionPolicy.SOURCE)
    @interface DrawableChainStyle {
        int OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE = 10;
        int OIV_DRAWABLE_CHAIN_STYLE_PACKED = 20;
    }

    @IntDef({OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START, OIV_DRAWABLE_ALIGN_STYLE_BODY_START, OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END,
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END, OIV_DRAWABLE_ALIGN_STYLE_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    @interface DrawableAlignStyle {
        int OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START = 0x01;
        int OIV_DRAWABLE_ALIGN_STYLE_BODY_START = 0x02;
        int OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END = 0x04;
        int OIV_DRAWABLE_ALIGN_STYLE_BODY_END = 0x08;
        int OIV_DRAWABLE_ALIGN_STYLE_NORMAL = 0x10;
    }

    public OperableItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initShadowPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView);
        mBodyText = ta.getString(R.styleable.OperableItemView_oiv_bodyText);
        if (TextUtils.isEmpty(mBodyText)) {
            mBodyText = ta.getString(R.styleable.OperableItemView_oiv_bodyDefaultText);
        }
        mBriefText = ta.getString(R.styleable.OperableItemView_oiv_briefText);
        if (TextUtils.isEmpty(mBriefText)) {
            mBriefText = ta.getString(R.styleable.OperableItemView_oiv_briefDefaultText);
        }
        mDrawablePadding = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_drawablePadding, 0);
        mTextInterval = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_textInterval, 0);
        mBriefTextColor = ta.getColor(R.styleable.OperableItemView_oiv_briefTextColor, Color.BLACK);
        mBodyTextColor = ta.getColor(R.styleable.OperableItemView_oiv_bodyTextColor, Color.BLACK);
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f);
        mEndDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable);
        mStartDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable);
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable);
        mGravity = ta.getInt(R.styleable.OperableItemView_oiv_gravity, 0);
        mShadowColor = ta.getColor(R.styleable.OperableItemView_oiv_shadowColor, Color.BLACK);
        mShadowRadius = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_shadowRadius, 0);
        mShadowDx = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_shadowDx, 10);
        mShadowDy = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_shadowDy, 10);
        mShadowSide = ta.getInt(R.styleable.OperableItemView_oiv_shadowSide, 0);
        mDrawableChainStyle = ta.getInt(R.styleable.OperableItemView_oiv_drawableChainStyle, OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE);
        mDrawableAlignStyle = ta.getInt(R.styleable.OperableItemView_oiv_drawableAlignStyle, OIV_DRAWABLE_ALIGN_STYLE_NORMAL);
        initBriefPaint(ta.getString(R.styleable.OperableItemView_oiv_briefTextTypeface),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_briefTextSize, 28));
        initBodyPaint(ta.getString(R.styleable.OperableItemView_oiv_bodyTextTypeface),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_bodyTextSize, 28));
        ta.recycle();
    }

    private int horizontalGravity() {
        if ((mGravity & OIV_GRAVITY_FLAG_CENTER_HORIZONTAL) != 0) {
            return OIV_GRAVITY_FLAG_CENTER_HORIZONTAL;
        }
        if ((mGravity & OIV_GRAVITY_FLAG_RIGHT) != 0) {
            return OIV_GRAVITY_FLAG_RIGHT;
        }
        return OIV_GRAVITY_FLAG_LEFT;
    }

    private int verticalGravity() {
        if ((mGravity & OIV_GRAVITY_FLAG_CENTER_VERTICAL) != 0) {
            return OIV_GRAVITY_FLAG_CENTER_VERTICAL;
        }
        if ((mGravity & OIV_GRAVITY_FLAG_BOTTOM) != 0) {
            return OIV_GRAVITY_FLAG_BOTTOM;
        }
        return OIV_GRAVITY_FLAG_TOP;
    }

    private void initBriefPaint(String typefacePath, int textSize) {
        mBriefPaint = new TextPaint();
        mBriefPaint.setColor(mBriefTextColor);
        try {
            mBriefPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), typefacePath));
        } catch (Exception e) {
            Log.i(TAG, "No set special brief text typeface!");
        }
        mCurrentAnimElem.briefTextColor = mBriefTextColor;
        mBriefPaint.setTextSize(textSize);
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                mBriefPaint.setTextAlign(Paint.Align.CENTER);
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                mBriefPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            case OIV_GRAVITY_FLAG_LEFT:
            default:
                mBriefPaint.setTextAlign(Paint.Align.LEFT);
                break;
        }
        mBriefPaint.setAntiAlias(true);
    }

    private void initBodyPaint(String typefacePath, int textSize) {
        mBodyPaint = new TextPaint();
        mBodyPaint.setColor(mBodyTextColor);
        try {
            mBodyPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), typefacePath));
        } catch (Exception e) {
            Log.i(TAG, "No set special body text typeface!");
        }
        mCurrentAnimElem.bodyTextColor = mBodyTextColor;
        mBodyPaint.setTextSize(textSize);
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                mBodyPaint.setTextAlign(Paint.Align.CENTER);
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                mBodyPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            case OIV_GRAVITY_FLAG_LEFT:
            default:
                mBodyPaint.setTextAlign(Paint.Align.LEFT);
                break;
        }
        mBodyPaint.setAntiAlias(true);
    }

    private void initShadowPaint() {
        if (mShadowSide == 0) return;
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);// 调用此方法后，才会执行 onDraw(Canvas) 方法
    }

    private void setUpShadowPaint() {
        if (mShadowPaint == null) return;
        mShadowPaint.reset();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
    }

    private void calculateShadowPadding() {
        float effect = mShadowRadius + 5;
        float rectLeft = 0;
        float rectTop = 0;
        float rectRight = this.getWidth();
        float rectBottom = this.getHeight();
        int paddingLeft = 0;
        int paddingTop = 0;
        int paddingRight = 0;
        int paddingBottom = 0;

        if (((mShadowSide & FLAG_SHADOW_SIDE_LEFT) == FLAG_SHADOW_SIDE_LEFT)) {
            rectLeft = effect;
            paddingLeft = (int) effect;
        }
        if (((mShadowSide & FLAG_SHADOW_SIDE_TOP) == FLAG_SHADOW_SIDE_TOP)) {
            rectTop = effect;
            paddingTop = (int) effect;
        }
        if (((mShadowSide & FLAG_SHADOW_SIDE_RIGHT) == FLAG_SHADOW_SIDE_RIGHT)) {
            rectRight = this.getWidth() - effect;
            paddingRight = (int) effect;
        }
        if (((mShadowSide & FLAG_SHADOW_SIDE_BOTTOM) == FLAG_SHADOW_SIDE_BOTTOM)) {
            rectBottom = this.getHeight() - effect;
            paddingBottom = (int) effect;
        }
        if (mShadowDy != 0) {
            rectBottom = rectBottom - mShadowDy;
            paddingBottom = paddingBottom + (int) mShadowDy;
        }
        if (mShadowDx != 0) {
            rectRight = rectRight - mShadowDx;
            paddingRight = paddingRight + (int) mShadowDx;
        }
        mRectF.left = rectLeft;
        mRectF.top = rectTop;
        mRectF.right = rectRight;
        mRectF.bottom = rectBottom;
        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (MeasureSpec.getSize(widthMeasureSpec) > 0) {
            initStaticLayout(MeasureSpec.getSize(widthMeasureSpec));
        }

        Drawable background = getBackground();
        if (background instanceof BitmapDrawable
                || background instanceof GradientDrawable
                && background.getIntrinsicWidth() > 0
                && background.getIntrinsicHeight() > 0) {
            setMeasuredDimension(background.getIntrinsicWidth(), background.getIntrinsicHeight());
            return;
        }

        float height = 0;
        switch (measureHeightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:// 父视图不对子视图施加任何限制，子视图可以得到任意想要的大小
                if (mStartDrawable != null && getStartDrawableHeight() > height) {
                    height = getStartDrawableHeight();
                }
                if (mEndDrawable != null && getEndDrawableHeight() > height) {
                    height = getEndDrawableHeight();
                }
                float lineHeight = (TextUtils.isEmpty(mBriefText) ? 0 : getTextHeight(mBriefPaint))
                        + mTextInterval + (TextUtils.isEmpty(mBodyText) ? 0 : getTextHeight(mBodyPaint));
                if (lineHeight > height) {
                    height = lineHeight;
                }
                float linesHeight = (mBriefStcLayout == null || TextUtils.isEmpty(mBriefText) ? 0 : mBriefStcLayout.getHeight())
                        + (mBodyStcLayout == null || TextUtils.isEmpty(mBodyText) ? 0 : mBodyStcLayout.getHeight()) + mTextInterval;
                if (linesHeight > height) {
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
        mCurrentAnimElem.bodyBaseLineY = bodyBaseLineY();
        mCurrentAnimElem.briefBaseLineY = briefBaseLineY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int centerY = (getBottom() - getTop()) / 2;
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        drawBodyText(canvas, paddingLeft, mAnimate);
        drawBriefText(canvas, paddingLeft, mAnimate);

        drawStartDrawable(canvas, centerY, paddingLeft);
        drawEndDrawable(canvas, centerY, paddingRight);
        drawDivider(canvas, paddingLeft, paddingRight);
    }

    private void initStaticLayout(int widthPx) {
        if (widthPx <= 0) return;
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = new StaticLayout(mBriefText == null ? "" : mBriefText,
                    mBriefPaint, briefTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = new StaticLayout(mBodyText == null ? "" : mBodyText,
                    mBodyPaint, bodyTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        }
    }

    /**
     * @deprecated use {@link #initStaticLayout(int)} instead
     */
    private boolean shouldRequestLayout(Canvas canvas) {
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = new StaticLayout(mBriefText == null ? "" : mBriefText,
                    mBriefPaint, usableMaxTextWidth(canvas), Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true);
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = new StaticLayout(mBodyText == null ? "" : mBodyText,
                    mBodyPaint, usableMaxTextWidth(canvas), Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true);
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
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_LEFT:
                baseLineX = mStartDrawable == null || !mStartDrawable.isVisible()
                        || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BODY_START ? paddingLeft
                        : paddingLeft + mDrawablePadding + getStartDrawableWidth();
                break;
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                baseLineX = canvas.getWidth() / 2;
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                baseLineX = usableBriefSpaceWidth(canvas) + mBriefStcLayout.getWidth() +
                        (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BODY_START
                                ? paddingLeft : paddingLeft + mDrawablePadding + getStartDrawableWidth());
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
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_LEFT:
                baseLineX = mStartDrawable == null || !mStartDrawable.isVisible()
                        || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START ? paddingLeft
                        : paddingLeft + mDrawablePadding + getStartDrawableWidth();
                break;
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                baseLineX = canvas.getWidth() / 2;
                break;
            case OIV_GRAVITY_FLAG_RIGHT:
                baseLineX = usableBodySpaceWidth(canvas) + mBodyStcLayout.getWidth() +
                        (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START
                                ? paddingLeft : paddingLeft + mDrawablePadding + getStartDrawableWidth());
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
            mDividerDrawable.setBounds(paddingLeft + mDrawablePadding + getStartDrawableWidth(),
                    (int) (getBottom() - getTop() - mDividerHeight), getWidth() - paddingRight, getBottom() - getTop());
        } else {
            mDividerDrawable.setBounds(paddingLeft, (int) (getBottom() - getTop() - mDividerHeight),
                    getWidth() - paddingRight, getBottom() - getTop());
        }
        mDividerDrawable.draw(canvas);
    }

    private int startDrawableAlignStyle() {
        if ((mDrawableAlignStyle & OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START) != 0) {
            return OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START;
        }
        if ((mDrawableAlignStyle & OIV_DRAWABLE_ALIGN_STYLE_BODY_START) != 0) {
            return OIV_DRAWABLE_ALIGN_STYLE_BODY_START;
        }
        return OIV_DRAWABLE_ALIGN_STYLE_NORMAL;
    }

    private int getStartDrawableWidth() {
        if (mBriefStcLayout == null || mBodyStcLayout == null)
            return mStartDrawable.getIntrinsicWidth();
        switch (startDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                return (int) ((float) mStartDrawable.getIntrinsicHeight() / (float) mStartDrawable.getIntrinsicWidth() * mBriefStcLayout.getHeight());
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                return (int) ((float) mStartDrawable.getIntrinsicHeight() / (float) mStartDrawable.getIntrinsicWidth() * mBodyStcLayout.getHeight());
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return mStartDrawable.getIntrinsicWidth();
        }
    }

    private int getStartDrawableHeight() {
        if (mBriefStcLayout == null || mBodyStcLayout == null)
            return mStartDrawable.getIntrinsicHeight();
        switch (startDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                return mBriefStcLayout.getHeight();
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                return mBodyStcLayout.getHeight();
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return mStartDrawable.getIntrinsicHeight();
        }
    }

    /**
     * 绘制左边的图标
     *
     * @param centerY 中间线的纵坐标
     */
    private void drawStartDrawable(Canvas canvas, int centerY, int paddingLeft) {
        if (mStartDrawable == null || !mStartDrawable.isVisible()) return;
        switch (startDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                if (TextUtils.isEmpty(mBriefText)) return;
                break;
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                if (TextUtils.isEmpty(mBodyText)) return;
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                break;
        }
        int left = startDrawableLeft(canvas, paddingLeft);
        int top = startDrawableTop(centerY);
        int right = left + getStartDrawableWidth();
        int bottom = top + getStartDrawableHeight();
        mStartDrawable.setBounds(left, top, right, bottom);
        mStartDrawable.draw(canvas);
    }

    private int startDrawableLeft(Canvas canvas, int paddingLeft) {
        switch (mDrawableChainStyle) {
            case OIV_DRAWABLE_CHAIN_STYLE_PACKED:
                break;
            case OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE:
            default:
                return paddingLeft;
        }
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                switch (startDrawableAlignStyle()) {
                    case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                        return canvas.getWidth() / 2 - mBriefStcLayout.getWidth() / 2 - mDrawablePadding - getStartDrawableWidth();
                    case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                        return canvas.getWidth() / 2 - mBodyStcLayout.getWidth() / 2 - mDrawablePadding - getStartDrawableWidth();
                    case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
                    default:
                        break;
                }
                return canvas.getWidth() / 2 - Math.max(mBriefStcLayout.getWidth(), mBodyStcLayout.getWidth()) / 2 - mDrawablePadding - getStartDrawableWidth();
            case OIV_GRAVITY_FLAG_RIGHT:
                return paddingLeft + usableSpaceWidth(canvas, startDrawableAlignStyle());
            case OIV_GRAVITY_FLAG_LEFT:
            default:
                return paddingLeft;
        }
    }

    private int startDrawableTop(int centerY) {
        switch (startDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                return briefBaseLineY();
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                return bodyBaseLineY();
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return centerY - getStartDrawableHeight() / 2;
        }
    }

    private int endDrawableAlignStyle() {
        if ((mDrawableAlignStyle & OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END) != 0) {
            return OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END;
        }
        if ((mDrawableAlignStyle & OIV_DRAWABLE_ALIGN_STYLE_BODY_END) != 0) {
            return OIV_DRAWABLE_ALIGN_STYLE_BODY_END;
        }
        return OIV_DRAWABLE_ALIGN_STYLE_NORMAL;
    }

    private int getEndDrawableWidth() {
        if (mBriefStcLayout == null || mBodyStcLayout == null)
            return mEndDrawable.getIntrinsicWidth();
        switch (endDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                return (int) ((float) mEndDrawable.getIntrinsicHeight() / (float) mEndDrawable.getIntrinsicWidth() * mBriefStcLayout.getHeight());
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                return (int) ((float) mEndDrawable.getIntrinsicHeight() / (float) mEndDrawable.getIntrinsicWidth() * mBodyStcLayout.getHeight());
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return mEndDrawable.getIntrinsicWidth();
        }
    }

    private int getEndDrawableHeight() {
        if (mBriefStcLayout == null || mBodyStcLayout == null)
            return mEndDrawable.getIntrinsicHeight();
        switch (endDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                return mBriefStcLayout.getHeight();
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                return mBodyStcLayout.getHeight();
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return mEndDrawable.getIntrinsicHeight();
        }
    }

    private int endDrawableTop(int centerY) {
        switch (endDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                return briefBaseLineY();
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                return bodyBaseLineY();
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                return centerY - getEndDrawableHeight() / 2;
        }
    }

    /**
     * 绘制右边的图标
     *
     * @param centerY 中间线的纵坐标
     */
    private void drawEndDrawable(Canvas canvas, int centerY, int paddingRight) {
        if (mEndDrawable == null || !mEndDrawable.isVisible()) return;
        switch (endDrawableAlignStyle()) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                if (TextUtils.isEmpty(mBriefText)) return;
                break;
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                if (TextUtils.isEmpty(mBodyText)) return;
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
                break;
        }
        int right = endDrawableRight(canvas, paddingRight);
        int left = right - getEndDrawableWidth();
        int top = endDrawableTop(centerY);
        int bottom = top + getEndDrawableHeight();
        mEndDrawable.setBounds(left, top, right, bottom);
        mEndDrawable.draw(canvas);
    }

    private int endDrawableRight(Canvas canvas, int paddingRight) {
        switch (mDrawableChainStyle) {
            case OIV_DRAWABLE_CHAIN_STYLE_PACKED:
                break;
            case OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE:
            default:
                return getWidth() - paddingRight;
        }
        switch (horizontalGravity()) {
            case OIV_GRAVITY_FLAG_CENTER_HORIZONTAL:
                switch (endDrawableAlignStyle()) {
                    case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                        return canvas.getWidth() / 2 + mBriefStcLayout.getWidth() / 2 + mDrawablePadding + getEndDrawableWidth();
                    case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                        return canvas.getWidth() / 2 + mBodyStcLayout.getWidth() / 2 + mDrawablePadding + getEndDrawableWidth();
                    case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
                    default:
                        break;
                }
                return canvas.getWidth() / 2 + Math.max(mBriefStcLayout.getWidth(), mBodyStcLayout.getWidth()) / 2 + mDrawablePadding + getEndDrawableWidth();
            case OIV_GRAVITY_FLAG_LEFT:
                return getWidth() - paddingRight - usableSpaceWidth(canvas, endDrawableAlignStyle());
            case OIV_GRAVITY_FLAG_RIGHT:
            default:
                return getWidth() - paddingRight;
        }
    }

    /**
     * 获取文字高度
     */
    private float getTextHeight(Paint paint) {
        return paint.descent() - paint.ascent();
    }

    private int briefTextWidth(int widthPx) {
        if (TextUtils.isEmpty(mBriefText)) return 0;
        switch (mDrawableChainStyle) {
            case OIV_DRAWABLE_CHAIN_STYLE_PACKED:
                int width = (int) mBriefPaint.measureText(mBriefText);
                return width > usableMaxTextWidth(widthPx) ? usableMaxTextWidth(widthPx) : width;
            case OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE:
            default:
                return usableMaxTextWidth(widthPx);
        }
    }

    private int bodyTextWidth(int widthPx) {
        if (TextUtils.isEmpty(mBodyText)) return 0;
        switch (mDrawableChainStyle) {
            case OIV_DRAWABLE_CHAIN_STYLE_PACKED:
                int width = (int) mBodyPaint.measureText(mBodyText);
                return width > usableMaxTextWidth(widthPx) ? usableMaxTextWidth(widthPx) : width;
            case OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE:
            default:
                return usableMaxTextWidth(widthPx);
        }
    }

    private int usableBriefSpaceWidth(Canvas canvas) {
        return canvas.getWidth() - occupiedWidthExceptText() - mBriefStcLayout.getWidth()
                + (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() != OIV_DRAWABLE_ALIGN_STYLE_BODY_START ? 0 : mDrawablePadding + getStartDrawableWidth())
                + (mEndDrawable == null || !mEndDrawable.isVisible() || endDrawableAlignStyle() != OIV_DRAWABLE_ALIGN_STYLE_BODY_END ? 0 : mDrawablePadding + getEndDrawableWidth());
    }

    private int usableBodySpaceWidth(Canvas canvas) {
        return canvas.getWidth() - occupiedWidthExceptText() - mBodyStcLayout.getWidth()
                + (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() != OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START ? 0 : mDrawablePadding + getStartDrawableWidth())
                + (mEndDrawable == null || !mEndDrawable.isVisible() || endDrawableAlignStyle() != OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END ? 0 : mDrawablePadding + getEndDrawableWidth());
    }

    /**
     * 可用空白宽度
     */
    private int usableSpaceWidth(Canvas canvas, int alignStyle) {
        switch (alignStyle) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                return canvas.getWidth() - occupiedWidthExceptText(alignStyle) - mBriefStcLayout.getWidth();
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                return canvas.getWidth() - occupiedWidthExceptText(alignStyle) - mBodyStcLayout.getWidth();
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
        }
        return canvas.getWidth() - occupiedWidthExceptText()
                - Math.max(mBriefStcLayout.getWidth(), mBodyStcLayout.getWidth());
    }

    private int occupiedWidthExceptText() {
        return getPaddingLeft() + getPaddingRight()
                + (mStartDrawable == null || !mStartDrawable.isVisible() ? 0 : mDrawablePadding + getStartDrawableWidth())
                + (mEndDrawable == null || !mEndDrawable.isVisible() ? 0 : mDrawablePadding + getEndDrawableWidth());
    }

    private int occupiedWidthExceptText(int alignStyle) {
        switch (alignStyle) {
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START:
                return getPaddingLeft() + getPaddingRight()
                        + (mStartDrawable == null || !mStartDrawable.isVisible() ? 0 : mDrawablePadding + getStartDrawableWidth())
                        + (mEndDrawable == null || !mEndDrawable.isVisible() || endDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BODY_END ? 0 : mDrawablePadding + getEndDrawableWidth());
            case OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END:
                return getPaddingLeft() + getPaddingRight()
                        + (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BODY_START ? 0 : mDrawablePadding + getStartDrawableWidth())
                        + (mEndDrawable == null || !mEndDrawable.isVisible() ? 0 : mDrawablePadding + getEndDrawableWidth());
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_START:
                return getPaddingLeft() + getPaddingRight()
                        + (mStartDrawable == null || !mStartDrawable.isVisible() ? 0 : mDrawablePadding + getStartDrawableWidth())
                        + (mEndDrawable == null || !mEndDrawable.isVisible() || endDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END ? 0 : mDrawablePadding + getEndDrawableWidth());
            case OIV_DRAWABLE_ALIGN_STYLE_BODY_END:
                return getPaddingLeft() + getPaddingRight()
                        + (mStartDrawable == null || !mStartDrawable.isVisible() || startDrawableAlignStyle() == OIV_DRAWABLE_ALIGN_STYLE_BRIEF_START ? 0 : mDrawablePadding + getStartDrawableWidth())
                        + (mEndDrawable == null || !mEndDrawable.isVisible() ? 0 : mDrawablePadding + getEndDrawableWidth());
            case OIV_DRAWABLE_ALIGN_STYLE_NORMAL:
            default:
        }
        return getPaddingLeft() + getPaddingRight()
                + (mStartDrawable == null || !mStartDrawable.isVisible() ? 0 : mDrawablePadding + getStartDrawableWidth())
                + (mEndDrawable == null || !mEndDrawable.isVisible() ? 0 : mDrawablePadding + getEndDrawableWidth());
    }

    /**
     * 可用的最大绘制宽度
     */
    private int usableMaxTextWidth(int widthPx) {
        if (widthPx <= 0) return 0;
        if (!mRefresh && mMaxTextWidth > 0) return mMaxTextWidth;
        mMaxTextWidth = widthPx - occupiedWidthExceptText();
        return mMaxTextWidth;
    }

    /**
     * 获取能绘制文本的最大宽度
     * <p>用{@link Canvas#getWidth()}获取到的宽度值是去除了间距的值</p>
     *
     * @see TextPaint#measureText(char[], int, int)
     * @see StaticLayout#getDesiredWidth(CharSequence, TextPaint)
     * @deprecated use {@link #usableMaxTextWidth(int)} instead
     */
    private int usableMaxTextWidth(Canvas canvas) {
        return canvas.getWidth() - occupiedWidthExceptText();
    }

    /**
     * 绘制正文文本时，画布需要平移到的纵坐标值
     */
    private int briefBaseLineY() {
        if (mBriefStcLayout == null || mBodyStcLayout == null) return 0;
        switch (verticalGravity()) {
            case OIV_GRAVITY_FLAG_TOP:
                return getPaddingTop();
            case OIV_GRAVITY_FLAG_BOTTOM:
                return getHeight() - getPaddingBottom()
                        - mBodyStcLayout.getHeight()
                        - mTextInterval
                        - mBriefStcLayout.getHeight();
            case OIV_GRAVITY_FLAG_CENTER_VERTICAL:
            default:
                if (TextUtils.isEmpty(mBodyText) || !mBodyTextEnable) {
                    return getHeight() / 2 - mBriefStcLayout.getHeight() / 2;
                }
                return (getHeight() - mTextInterval
                        - mBriefStcLayout.getHeight()
                        - mBodyStcLayout.getHeight()) / 2;
        }
    }

    /**
     * 绘制摘要文本时，画布需要平移到的纵坐标值
     */
    private int bodyBaseLineY() {
        if (mBodyStcLayout == null || mBriefStcLayout == null) return 0;
        switch (verticalGravity()) {
            case OIV_GRAVITY_FLAG_TOP:
                return getPaddingTop() + mBriefStcLayout.getHeight() + mTextInterval;
            case OIV_GRAVITY_FLAG_BOTTOM:
                return getHeight() - getPaddingBottom() - mBodyStcLayout.getHeight();
            case OIV_GRAVITY_FLAG_CENTER_VERTICAL:
            default:
                if (TextUtils.isEmpty(mBriefText) || !mBriefTextEnable) {
                    return getHeight() / 2 - mBodyStcLayout.getHeight() / 2;
                }
                return getHeight()
                        - (getHeight() - mTextInterval
                        - mBriefStcLayout.getHeight()
                        - mBodyStcLayout.getHeight()) / 2
                        - mBodyStcLayout.getHeight();
        }
    }

    public void setDrawableVisible(boolean visible) {
        if (mStartDrawable != null) {
            mAnimate = false;
            mStartDrawable.setVisible(visible, false);
        }
        if (mEndDrawable != null) {
            mAnimate = false;
            mEndDrawable.setVisible(visible, false);
        }
    }

    public void setStartDrawableVisible(boolean visible) {
        if (mStartDrawable == null) return;
        mAnimate = false;
        mStartDrawable.setVisible(visible, false);
    }

    public void setEndDrawableVisible(boolean visible) {
        if (mEndDrawable == null) return;
        mAnimate = false;
        mEndDrawable.setVisible(visible, false);
    }

    public boolean isStartDrawableVisible() {
        return mStartDrawable != null && mStartDrawable.isVisible();
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
        invalidate();
    }

    public void setBodyTextColor(int bodyTextColor) {
        mBodyTextColor = bodyTextColor;
        mBodyPaint.setColor(bodyTextColor);
        mCurrentAnimElem.bodyTextColor = bodyTextColor;
        mAnimate = false;
        invalidate();
    }

    public void setBodyTextSize(@DimenRes int bodyTextSize) {
        mBodyPaint.setTextSize(bodyTextSize);
        mAnimate = false;
        mRefresh = true;
        requestLayout();
        invalidate();
    }

    public void setBriefText(String briefText) {
        if (TextUtils.isEmpty(briefText)) return;
        mBriefText = briefText;
        mAnimate = false;
        mRefresh = true;
        requestLayout();
        invalidate();
    }

    public void setBriefTextColor(int briefTextColor) {
        mBriefTextColor = briefTextColor;
        mBriefPaint.setColor(briefTextColor);
        mCurrentAnimElem.briefTextColor = briefTextColor;
        mAnimate = false;
        invalidate();
    }

    public void setBriefTextSize(@DimenRes int briefTextSize) {
        mBriefPaint.setTextSize(briefTextSize);
        mAnimate = false;
        mRefresh = true;
        requestLayout();
        invalidate();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mX = (int) event.getX();
                mY = (int) event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if (mStartDrawable != null && mStartDrawable.isVisible() && mStartDrawable.getBounds().contains(mX, mY)) {
                    if (mListener != null) mListener.onStartDrawableClick(this);
                    return true;
                }
                if (mEndDrawable != null && mEndDrawable.isVisible() && mEndDrawable.getBounds().contains(mX, mY)) {
                    if (mListener != null) mListener.onEndDrawableClick(this);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnClickDrawableListener(OnClickDrawableListener listener) {
        mListener = listener;
    }

    public interface OnClickDrawableListener {
        void onStartDrawableClick(OperableItemView view);

        void onEndDrawableClick(OperableItemView view);
    }
}
