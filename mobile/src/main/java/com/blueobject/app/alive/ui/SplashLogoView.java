package com.blueobject.app.alive.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MainActivity;
import com.blueobject.app.alive.MapsActivity;
import com.blueobject.app.alive.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nrgie on 2017.08.22..
 */

public class SplashLogoView extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    @BindView(R.id.ivStar)
    ImageView ivStar;
    @BindView(R.id.vDotsView)
    DotsView vDotsView;
    @BindView(R.id.vCircle)
    CircleView vCircle;

    private boolean isChecked;
    private AnimatorSet animatorSet;

    Animator.AnimatorListener animListener;

    public SplashLogoView(Context context) {
        super(context);
        init(context);
    }

    public SplashLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SplashLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SplashLogoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setAnimEndListener(Animator.AnimatorListener animListener) {
        this.animListener = animListener;
    }


    private void init(Context c) {
        LayoutInflater.from(getContext()).inflate(R.layout.splashlogoview, this, true);
        ButterKnife.bind(this);
        setOnClickListener(this);

        ivStar.animate().cancel();
        ivStar.setScaleX(0);
        ivStar.setScaleY(0);
        vCircle.setInnerCircleRadiusProgress(0);
        vCircle.setOuterCircleRadiusProgress(0);
        vDotsView.setCurrentProgress(0);

        animatorSet = new AnimatorSet();

        ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        outerCircleAnimator.setDuration(500);
        outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        innerCircleAnimator.setDuration(400);
        innerCircleAnimator.setStartDelay(400);
        innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f);
        starScaleYAnimator.setDuration(750);
        starScaleYAnimator.setStartDelay(400);
        starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f);
        starScaleXAnimator.setDuration(750);
        starScaleXAnimator.setStartDelay(400);
        starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
        dotsAnimator.setDuration(1500);
        dotsAnimator.setStartDelay(50);
        dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

        ObjectAnimator mover = ObjectAnimator.ofFloat(ivStar, "translationY", -100);
        mover.setDuration(300);
        mover.setStartDelay(1500);
        mover.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);



        animatorSet.playTogether(
                outerCircleAnimator,
                innerCircleAnimator,
                starScaleYAnimator,
                starScaleXAnimator,
                dotsAnimator

        );

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                vCircle.setInnerCircleRadiusProgress(0);
                vCircle.setOuterCircleRadiusProgress(0);
                vDotsView.setCurrentProgress(0);
                ivStar.setScaleX(1);
                ivStar.setScaleY(1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                animListener.onAnimationEnd(animation);

                /*
                Intent main = new Intent(Global.appContext, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Global.appContext.startActivity(main);

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Activity act = (Activity) getContext();
                        if(act != null)
                            act.finish();
                    }
                },2000);
                */

            }
        });

        animatorSet.start();





    }

    @Override
    public void onClick(final View v) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ivStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                ivStar.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }
}