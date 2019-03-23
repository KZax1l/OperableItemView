package com.andova.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
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

    private var mPaddingTop: Int = 0
    private var mPaddingBottom: Int = 0
    private var mDividerHeight: Float = 0f
    private var mDrawablePadding: Int = 0

    private var mDividerDrawable: Drawable? = null

    private lateinit var text: Text
    private lateinit var param: Param
    private lateinit var start: StartDrawable
    private lateinit var end: EndDrawable

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.OperableItemView)
        mDrawablePadding = ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_drawablePadding, 0)
        mDividerHeight = ta.getDimension(R.styleable.OperableItemView_oiv_dividerHeight, 1f)
        mDividerDrawable = ta.getDrawable(R.styleable.OperableItemView_oiv_dividerDrawable)
        mPaddingTop = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_paddingTop, 0)
        mPaddingBottom = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_paddingBottom, 0)
        if (mPaddingTop == 0) mPaddingTop = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_padding, 0)
        if (mPaddingBottom == 0) mPaddingBottom = ta.getDimensionPixelOffset(R.styleable.OperableItemView_android_padding, 0)
        param = Param(ta.getInt(R.styleable.OperableItemView_oiv_gravity, 0),
                ta.getInt(R.styleable.OperableItemView_oiv_drawableAlignStyle, OIV_DRAWABLE_ALIGN_STYLE_NORMAL),
                ta.getInt(R.styleable.OperableItemView_oiv_drawableChainStyle, OIV_DRAWABLE_CHAIN_STYLE_SPREAD_INSIDE))
        text = Text(ta.getString(R.styleable.OperableItemView_oiv_bodyText)
                ?: ta.getString(R.styleable.OperableItemView_oiv_bodyDefaultText),
                ta.getString(R.styleable.OperableItemView_oiv_briefText)
                        ?: ta.getString(R.styleable.OperableItemView_oiv_briefDefaultText),
                ta.getColor(R.styleable.OperableItemView_oiv_bodyTextColor, Color.BLACK),
                ta.getColor(R.styleable.OperableItemView_oiv_briefTextColor, Color.BLACK),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_textInterval, 0), param, this)
        text.initBodyPaint(context, ta.getString(R.styleable.OperableItemView_oiv_bodyTextTypeface), ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_bodyTextSize, 28))
        text.initBriefPaint(context, ta.getString(R.styleable.OperableItemView_oiv_briefTextTypeface), ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_briefTextSize, 28))
        start = StartDrawable(ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_startDrawableWidth, -1),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_startDrawableHeight, -1),
                ta.getDrawable(R.styleable.OperableItemView_oiv_startDrawable), text, param)
        end = EndDrawable(ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_endDrawableWidth, -1),
                ta.getDimensionPixelOffset(R.styleable.OperableItemView_oiv_endDrawableHeight, -1),
                ta.getDrawable(R.styleable.OperableItemView_oiv_endDrawable), text, param)
        ta.recycle()
    }

    fun occupiedWidthExceptText(): Int = paddingLeft + paddingRight + (if (mStartDrawable == null || mStartDrawable?.isVisible == false) 0 else mDrawablePadding + getStartDrawableWidth()) + if (mEndDrawable == null || mEndDrawable?.isVisible == false) 0 else mDrawablePadding + getEndDrawableWidth()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (View.MeasureSpec.getSize(widthMeasureSpec) > 0) text.initStaticLayout(View.MeasureSpec.getSize(widthMeasureSpec))
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
                if (start.height() > height) height = start.height().toFloat()
                if (end.height() > height) height = end.height().toFloat()
                val lineHeight = text.lineHeight()
                if (lineHeight > height) height = lineHeight
                val linesHeight = text.linesHeight().toFloat()
                if (linesHeight > height) height = linesHeight
            }
            View.MeasureSpec.EXACTLY -> height = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height.toInt() + mPaddingTop + mPaddingBottom)
    }
}