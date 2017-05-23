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

public class AddFriendView extends FrameLayout implements Checkable {
    protected AddFriendView.OnPraisCheckedListener praiseCheckedListener;
    protected CheckedImageView imageView;
    protected int padding;

    public AddFriendView(Context context) {
        super(context);
        initalize();
    }

    public AddFriendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize();
    }

    protected void initalize() {
        setClickable(true);
        imageView = new CheckedImageView(getContext());
        imageView.setImageResource(R.drawable.blog_add_friend_selector);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
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

    public void setOnPraisCheckedListener(AddFriendView.OnPraisCheckedListener praiseCheckedListener) {
        this.praiseCheckedListener = praiseCheckedListener;
    }

    public interface OnPraisCheckedListener{
        void onPraisChecked(boolean isChecked);
    }
}
