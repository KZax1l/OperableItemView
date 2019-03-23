package com.andova.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.andova.oiv.R

/**
 * Created by Administrator on 2019-03-23.
 *
 * @author kzaxil
 * @since 1.0.0
 */
class OperableItemView : View {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs)
    }

    @Suppress("unused")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(context, attrs)
    }

    companion object {
        private const val TAG = "OperableItemView"
    }

    private var mPaddingTop: Int = 0
    private var mPaddingBottom: Int = 0
    private var mMaxTextWidth: Int = 0
    private var mBriefText: String? = null
    private var mBodyText: String? = null
    private var mTextInterval: Int = 0
    private var mBodyTextColor: Int = 0
    private var mBriefTextColor: Int = 0
    private var mDividerHeight: Float = 0f
    private var mDrawablePadding: Int = 0
    private var mEndDrawableWidth: Int = 0
    private var mEndDrawableHeight: Int = 0

    private var mRefresh = true
    private var mGravity: Int = 0
    private var mDrawableAlignStyle: Int = 0
    private var mDrawableChainStyle: Int = 0

    private val mBodyPaint: TextPaint by lazy { TextPaint() }
    private val mBriefPaint: TextPaint by lazy { TextPaint() }
    private var mEndDrawable: Drawable? = null
    private var mDividerDrawable: Drawable? = null
    private var mBodyStcLayout: StaticLayout? = null
    private var mBriefStcLayout: StaticLayout? = null

    private lateinit var startDrawable: StartDrawable

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView)
        mBodyText = ta.getString(R.styleable.OperableItemView_oiv_bodyText)
                ?: ta.getString(R.styleable.OperableItemView_oiv_bodyDefaultText)
        mBriefText = ta.getString(R.styleable.OperableItemView_oiv_briefText)
                ?: ta.getString(R.styleable.OperableItemView_oiv_briefDefaultText)
        mDrawablePadding = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_drawablePadding, 0)
        mTextInterval = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_textInterval, 0)
        mBriefTextColor = ta.getColor(R.styleable.OperableItemView_oiv_briefTextColor, Color.BLACK)
        mBodyTextColor = ta.getColor(R.styleable.OperableItemView_oiv_bodyTextColor, Color.BLACK)
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f)
        mEndDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable)
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable)
        mGravity = ta.getInt(R.styleable.OperableItemView_oiv_gravity, 0)
        mDrawableChainStyle = ta.getInt(R.styleable.OperableItemView_oiv_drawableChainStyle, OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE)
        mDrawableAlignStyle = ta.getInt(R.styleable.OperableItemView_oiv_drawableAlignStyle, OIV_DRAWABLE_ALIGN_STYLE_NORMAL)
        mEndDrawableWidth = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_endDrawableWidth, -1)
        mEndDrawableHeight = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_endDrawableHeight, -1)
        initBriefPaint(ta.getString(R.styleable.OperableItemView_oiv_briefTextTypeface), ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_briefTextSize, 28))
        initBodyPaint(ta.getString(R.styleable.OperableItemView_oiv_bodyTextTypeface), ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_bodyTextSize, 28))
        mPaddingTop = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_paddingTop, 0)
        mPaddingBottom = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_paddingBottom, 0)
        if (mPaddingTop == 0) mPaddingTop = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_padding, 0)
        if (mPaddingBottom == 0) mPaddingBottom = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_padding, 0)
        startDrawable = StartDrawable(ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_startDrawableWidth, -1),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_startDrawableHeight, -1),
                ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable))
        ta.recycle()
    }

    private fun initBriefPaint(typefacePath: String?, textSize: Int) {
        mBriefPaint.color = mBriefTextColor
        try {
            mBriefPaint.typeface = Typeface.createFromAsset(context.assets, typefacePath)
        } catch (e: Exception) {
            Log.i(TAG, "No set special brief text typeface!")
        }
        mBriefPaint.textSize = textSize.toFloat()
        when (horizontalGravity()) {
            OIV_GRAVITY_FLAG_CENTER_HORIZONTAL -> mBriefPaint.textAlign = Paint.Align.CENTER
            OIV_GRAVITY_FLAG_RIGHT -> mBriefPaint.textAlign = Paint.Align.RIGHT
            OIV_GRAVITY_FLAG_LEFT -> mBriefPaint.textAlign = Paint.Align.LEFT
            else -> mBriefPaint.textAlign = Paint.Align.LEFT
        }
        mBriefPaint.isAntiAlias = true
    }

    private fun initBodyPaint(typefacePath: String?, textSize: Int) {
        mBodyPaint.color = mBodyTextColor
        try {
            mBodyPaint.typeface = Typeface.createFromAsset(context.assets, typefacePath)
        } catch (e: Exception) {
            Log.i(TAG, "No set special body text typeface!")
        }
        mBodyPaint.textSize = textSize.toFloat()
        when (horizontalGravity()) {
            OIV_GRAVITY_FLAG_CENTER_HORIZONTAL -> mBodyPaint.textAlign = Paint.Align.CENTER
            OIV_GRAVITY_FLAG_RIGHT -> mBodyPaint.textAlign = Paint.Align.RIGHT
            OIV_GRAVITY_FLAG_LEFT -> mBodyPaint.textAlign = Paint.Align.LEFT
            else -> mBodyPaint.textAlign = Paint.Align.LEFT
        }
        mBodyPaint.isAntiAlias = true
    }

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

    private fun getEndDrawableWidth(): Int {
        if (mEndDrawableWidth > 0) return mEndDrawableWidth
        mBriefStcLayout ?: return mEndDrawable?.intrinsicWidth ?: 0
        mBodyStcLayout ?: return mEndDrawable?.intrinsicWidth ?: 0
        mEndDrawable ?: return 0
        val w = mEndDrawable?.intrinsicWidth?.toFloat() ?: 0f
        val h = mEndDrawable?.intrinsicHeight?.toFloat() ?: 0f
        return when (endDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END -> (h / w * mBriefStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END -> (h / w * mBodyStcLayout?.height!!).toInt()
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> w.toInt()
            else -> w.toInt()
        }
    }

    private fun getEndDrawableHeight(): Int {
        if (mEndDrawableHeight > 0) return mEndDrawableHeight
        mBriefStcLayout ?: return mEndDrawable?.intrinsicHeight ?: 0
        mBodyStcLayout ?: return mEndDrawable?.intrinsicHeight ?: 0
        return when (endDrawableAlignStyle()) {
            OIV_DRAWABLE_ALIGN_STYLE_BRIEF_END -> return mBriefStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_BODY_END -> return mBodyStcLayout?.height ?: 0
            OIV_DRAWABLE_ALIGN_STYLE_NORMAL -> return mEndDrawable?.intrinsicHeight ?: 0
            else -> mEndDrawable?.intrinsicHeight ?: 0
        }
    }

    private fun briefTextWidth(widthPx: Int): Int {
        mBriefText ?: return 0
        return when (mDrawableChainStyle) {
            OIV_DRAWABLE_CHAIN_STYLE_PACKED -> {
                val width = mBriefPaint.measureText(mBriefText).toInt()
                if (width > usableMaxTextWidth(widthPx)) usableMaxTextWidth(widthPx) else width
            }
            OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE -> usableMaxTextWidth(widthPx)
            else -> usableMaxTextWidth(widthPx)
        }
    }

    private fun bodyTextWidth(widthPx: Int): Int {
        if (TextUtils.isEmpty(mBodyText)) return 0
        return when (mDrawableChainStyle) {
            OIV_DRAWABLE_CHAIN_STYLE_PACKED -> {
                val width = mBodyPaint.measureText(mBodyText).toInt()
                if (width > usableMaxTextWidth(widthPx)) usableMaxTextWidth(widthPx) else width
            }
            OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE -> usableMaxTextWidth(widthPx)
            else -> usableMaxTextWidth(widthPx)
        }
    }

    private fun occupiedWidthExceptText(): Int = paddingLeft + paddingRight + (if (mStartDrawable == null || mStartDrawable?.isVisible == false) 0 else mDrawablePadding + getStartDrawableWidth()) + if (mEndDrawable == null || mEndDrawable?.isVisible == false) 0 else mDrawablePadding + getEndDrawableWidth()

    private fun usableMaxTextWidth(widthPx: Int): Int {
        if (widthPx <= 0) return 0
        if (!mRefresh && mMaxTextWidth > 0) return mMaxTextWidth
        mMaxTextWidth = widthPx - occupiedWidthExceptText()
        return mMaxTextWidth
    }

    private fun initStaticLayout(widthPx: Int) {
        if (widthPx <= 0) return
        if (mBriefStcLayout == null || mRefresh) {
            mBriefStcLayout = StaticLayout(if (mBriefText == null) "" else mBriefText, mBriefPaint, briefTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }
        if (mBodyStcLayout == null || mRefresh) {
            mBodyStcLayout = StaticLayout(if (mBodyText == null) "" else mBodyText, mBodyPaint, bodyTextWidth(widthPx), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
        }
    }

    private fun getTextHeight(paint: Paint): Float = paint.descent() - paint.ascent()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (View.MeasureSpec.getSize(widthMeasureSpec) > 0) initStaticLayout(View.MeasureSpec.getSize(widthMeasureSpec))
        when (background) {
            is BitmapDrawable -> setMeasuredDimension(background.intrinsicWidth, background.intrinsicHeight)
            is GradientDrawable -> {
                if (background.intrinsicWidth <= 0 || background.intrinsicHeight <= 0) return
                setMeasuredDimension(background.intrinsicWidth, background.intrinsicHeight)
            }
        }
        var height = 0f
        when (measureHeightMode) {
            View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED -> {
                if (mStartDrawable != null && getStartDrawableHeight() > height) height = getStartDrawableHeight().toFloat()
                if (mEndDrawable != null && getEndDrawableHeight() > height) height = getEndDrawableHeight().toFloat()
                val lineHeight = if (TextUtils.isEmpty(mBriefText)) 0f else getTextHeight(mBriefPaint) + mTextInterval.toFloat() + if (TextUtils.isEmpty(mBodyText)) 0f else getTextHeight(mBodyPaint)
                if (lineHeight > height) height = lineHeight
                val briefH = if (mBriefStcLayout == null || TextUtils.isEmpty(mBriefText)) 0 else mBriefStcLayout?.height
                        ?: 0
                val bodyH = if (mBodyStcLayout == null || TextUtils.isEmpty(mBodyText)) 0 else mBodyStcLayout?.height
                        ?: 0
                val linesHeight = (briefH + bodyH + mTextInterval).toFloat()
                if (linesHeight > height) height = linesHeight
            }
            View.MeasureSpec.EXACTLY -> height = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height.toInt() + mPaddingTop + mPaddingBottom)
    }
}