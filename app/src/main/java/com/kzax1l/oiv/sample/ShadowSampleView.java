package com.kzax1l.oiv.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018-04-25.
 *
 * @author kzaxil
 * @since 1.0.0
 */
public class ShadowSampleView extends View {
    private Paint mShadowPaint = new Paint();

    private Bitmap mBitmap;

    public ShadowSampleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(Color.WHITE);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.bg_circle);
        mBitmap = ((BitmapDrawable) drawable).getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mShadowPaint.setShadowLayer(10, 20, 20, Color.parseColor("#59ff0000"));
        canvas.drawBitmap(mBitmap, 0, 0, mShadowPaint);
    }
}
