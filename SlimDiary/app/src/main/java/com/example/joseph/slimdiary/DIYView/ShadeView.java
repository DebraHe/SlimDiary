package com.example.joseph.slimdiary.DIYView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.joseph.slimdiary.R;

public class ShadeView extends View {

    /**
     * 图标
     */
    private Bitmap iconBitmap;
    /**
     * 图标背景色
     */
    private int iconBackgroundColor;
    /**
     * 图标默认背景色
     */
    private final int DEFAULT_ICON_BACKGROUND_COLOR = 0x484848;


    /**
     * 图标绘制范围
     */
    private Rect iconRect;

    /**
     * 透明度（0.0-1.0）
     */
    private float mAlpha;

    private Bitmap mBitmap;

    public ShadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadeView);
        BitmapDrawable drawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.ShadeView_icon);
        if (drawable != null) {
            iconBitmap = drawable.getBitmap();
        }
        iconBackgroundColor = typedArray.getColor(R.styleable.ShadeView_color, DEFAULT_ICON_BACKGROUND_COLOR);
        //资源回收
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为图标是正方形且需要居中显示的，所以View的大小去掉padding和文字所占空间后，
        //剩余的空间的宽和高的最小值才是图标的边长
        int bitmapSide = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom());
        int left = getMeasuredWidth() / 2 - bitmapSide / 2;
        int top = (getMeasuredHeight()) / 2 - bitmapSide / 2;
        //获取图标的绘制范围
        iconRect = new Rect(left, top, left + bitmapSide, top + bitmapSide);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //进一取整
        int alpha = (int) Math.ceil((255 * mAlpha));
        //绘制原图标
        canvas.drawBitmap(iconBitmap, null, iconRect, null);
        setupTargetBitmap(alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * 在mBitmap上绘制以iconBackgroundColor颜色为Dst，DST_IN模式下的图标
     *
     * @param alpha Src颜色的透明度
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        Paint paint = new Paint();
        paint.setColor(iconBackgroundColor);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(alpha);
        //在图标背后先绘制一层iconBackgroundColor颜色的背景
        canvas.drawRect(iconRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setAlpha(255);
        //在mBitmap上绘制以iconBackgroundColor颜色为Dst，DST_IN模式下的图标
        canvas.drawBitmap(iconBitmap, null, iconRect, paint);
    }


    /**
     * 设置图标透明度并重绘
     *
     * @param alpha 透明度
     */
    public void setIconAlpha(float alpha) {
        if (mAlpha != alpha) {
            this.mAlpha = alpha;
            invalidateView();
        }
    }

    public void setIconBitmap(Context context, int resourceID) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, resourceID);
        if (!bitmapDrawable.getBitmap().equals(iconBitmap)) {
            iconBitmap = bitmapDrawable.getBitmap();
            invalidateView();
        }
    }

    /**
     * 判断当前是否为UI线程，是则直接重绘，否则调用postInvalidate()利用Handler来重绘
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    private static final String STATE_INSTANCE = "STATE_INSTANCE";

    private static final String STATE_ALPHA = "STATE_ALPHA";

    /**
     * 保存状态
     *
     * @return Parcelable
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    /**
     * 恢复状态
     *
     * @param parcelable Parcelable
     */
    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
        } else {
            super.onRestoreInstanceState(parcelable);
        }
    }

}
