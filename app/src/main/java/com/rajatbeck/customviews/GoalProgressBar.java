package com.rajatbeck.customviews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by rajatbeck on 11/23/2016.
 */

public class GoalProgressBar extends View {

    private int progress;
    private int goal;
    private float goalIndicatorHeight;
    private float goalIndicatorWidth;
    private int goalReachedColor;
    private int goalNotReachedColor;
    private int unfilledSectionColor;
    private float barHeight;
    private Paint mPaint;
    private ValueAnimator barAnimator;

    public GoalProgressBar(Context context) {
        super(context);
    }

    public GoalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GoalProgressBar, 0, 0);
        try {
            int indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.GoalProgressBar_goalIndicatorHeight, 10);
            setGoalIndicatorHeight(indicatorHeight);
            setGoalIndicatorWidth(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_goalIndicatorWidth, 4));
            setGoalReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalReachedColor, Color.BLUE));
            setGoalNotReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalNotReachedColor, Color.GRAY));
            setUnfilledSectionColor(typedArray.getColor(R.styleable.GoalProgressBar_unfilledSectionColor, Color.YELLOW));
            setBarHeight(typedArray.getDimensionPixelSize(R.styleable.GoalProgressBar_barHeight, 6));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        int height;

      /*  EXACTLY means the layout_width or layout_height value was set to a specific value. You should probably make your view this size. This can also get triggered when match_parent is used, to set the size exactly to the parent view (this is layout dependent in the framework).

        AT_MOST typically means the layout_width or layout_height value was set to match_parent or wrap_content where a maximum size is needed (this is layout dependent in the framework), and the size of the parent dimension is the value. You should not be any larger than this size.

        UNSPECIFIED typically means the layout_width or layout_height value was set to wrap_content with no restrictions. You can be whatever size you would like. Some layouts also use this callback to figure out your desired size before determine what specs to actually pass you again in a second measure request.*/
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            default:
            case MeasureSpec.UNSPECIFIED:
                height = (int) Math.max(barHeight, goalIndicatorHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                height = (int) Math.min(specHeight, Math.max(barHeight, goalIndicatorHeight));
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt("progress", progress);
        bundle.putInt("goal", goal);
        bundle.putParcelable("superState", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setProgress(bundle.getInt("progress"));
            setGoal(bundle.getInt("goal"));
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int halfHeight = getHeight() / 2;
        int progressEndX = (int) (getWidth() * progress / 100f);
        //filled portion
        int color = (progress >= goal) ? goalReachedColor : goalNotReachedColor;
        mPaint.setColor(color);
        mPaint.setStrokeWidth(barHeight);
        canvas.drawLine(0, halfHeight, progressEndX, halfHeight, mPaint);

        //unfilled portion
        mPaint.setColor(unfilledSectionColor);
        canvas.drawLine(progressEndX, halfHeight, getWidth(), halfHeight, mPaint);

        //goal indicator
        float indicatorPosition = getWidth() * goal / 100f;
        mPaint.setColor(goalReachedColor);
        mPaint.setStrokeWidth(goalIndicatorWidth);
        canvas.drawLine(indicatorPosition, halfHeight - (goalIndicatorHeight / 2), indicatorPosition, halfHeight + (goalIndicatorHeight / 2), mPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public GoalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public void setProgress(final int progress, boolean animate) {
        if (animate) {
            barAnimator = ValueAnimator.ofFloat(0, 1);

            setProgress(0, false);

            barAnimator.setInterpolator(new DecelerateInterpolator());

            barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float interpolation = (float) valueAnimator.getAnimatedValue();
                    setProgress((int) (interpolation* progress),false);
                }
            });
            if(!barAnimator.isStarted())
                barAnimator.start();
        } else {
            this.progress = progress;
            postInvalidate();
        }
    }

    public int getGoal() {
        return goal;
    }

    public int getProgress() {
        return progress;
    }

    public void setGoal(int goal) {
        this.goal = goal;
        postInvalidate();
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
        postInvalidate();
    }

    public void setGoalIndicatorHeight(float goalIndicatorHeight) {
        this.goalIndicatorHeight = goalIndicatorHeight;
        postInvalidate();
    }

    public void setGoalIndicatorWidth(float goalIndicatorWidth) {
        this.goalIndicatorWidth = goalIndicatorWidth;
        postInvalidate();
    }

    public void setGoalNotReachedColor(int goalNotReachedColor) {
        this.goalNotReachedColor = goalNotReachedColor;
        postInvalidate();
    }

    public void setGoalReachedColor(int goalReachedColor) {
        this.goalReachedColor = goalReachedColor;
        postInvalidate();
    }

    public void setUnfilledSectionColor(int unfilledSectionColor) {
        this.unfilledSectionColor = unfilledSectionColor;
        postInvalidate();
    }

    public float getGoalIndicatorHeight() {
        return goalIndicatorHeight;
    }

    public float getGoalIndicatorWidth() {
        return goalIndicatorWidth;
    }

    public float getBarHeight() {
        return barHeight;
    }

    public int getGoalNotReachedColor() {
        return goalNotReachedColor;
    }

    public int getGoalReachedColor() {
        return goalReachedColor;
    }

    public int getUnfilledSectionColor() {
        return unfilledSectionColor;
    }

}
