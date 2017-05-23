package com.example.joseph.slimdiary.DIYView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.example.joseph.slimdiary.R;
import com.example.joseph.slimdiary.anim.AnimHelper;
import com.example.joseph.slimdiary.anim.PulseAnimator;

/**
 * Created by Joseph on 2017/3/23.
 */

public class CollectView extends FrameLayout implements Checkable {

    protected CollectView.OnPraisCheckedListener praiseCheckedListener;
    protected CheckedImageView imageView;
    protected int padding;

    public CollectView(Context context) {
        super(context);
        initalize();
    }

    public CollectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize();
    }

    protected void initalize() {
        setClickable(true);
        imageView = new CheckedImageView(getContext());
        imageView.setImageResource(R.drawable.blog_collect_selector);
        FrameLayout.LayoutParams flp = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(imageView, flp);

    }

    @Override
    public boolean performClick() {
        checkChange();
        return super.performClick();
    }

    @Override
    public void toggle() {
        checkChange();
    }

    public void setChecked(boolean isCheacked) {
        imageView.setChecked(isCheacked);
    }

    public void checkChange() {
        if (imageView.isChecked) {
            imageView.setChecked(false);
            AnimHelper.with(new PulseAnimator()).duration(1000).playOn(imageView);
        } else {
            imageView.setChecked(true);
            AnimHelper.with(new PulseAnimator()).duration(1000).playOn(imageView);
        }
        if (praiseCheckedListener != null) {
            praiseCheckedListener.onPraisChecked(imageView.isChecked);
        }
    }

    public boolean isChecked() {
        return imageView.isChecked;
    }

    public void setOnPraisCheckedListener(CollectView.OnPraisCheckedListener praiseCheckedListener) {
        this.praiseCheckedListener = praiseCheckedListener;
    }

    public interface OnPraisCheckedListener{
        void onPraisChecked(boolean isChecked);
    }
}
