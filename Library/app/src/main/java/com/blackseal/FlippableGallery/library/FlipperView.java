package com.blackseal.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

import timber.log.Timber;

/**
 * Created by steven.xu on 17/01/2017.
 * <p>
 * A widget that can be turned over, like a card.
 *
 * @attr ref R.styleable#FlipperView_frontCardLayout
 * @attr ref R.styleable#FlipperView_backCardLayout
 */
public class FlipperView extends ViewFlipper {
    Context context;
    View frontView, backView;
    private boolean isBackShowing;  //indicator of which side is showing
    OnClickListener onClickListener;
    LayoutInflater inflater;
    Animation.AnimationListener animationListener;
    @AnimRes int forntViewFilpInAnimationResource = -1;
    @AnimRes int backViewFilpInAnimationResource = -1;
    @AnimRes int backViewFilpOutAnimationResource = -1;
    @AnimRes int forntViewFilpOutAnimationResource = -1;

    public FlipperView(Context context) {
        this(context, null);
    }

    public FlipperView(Context context, View frontView, View backView) {
        this(context, null, 0, frontView, backView);
    }

    public FlipperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipperView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, null);
    }

    public FlipperView(Context context, AttributeSet attrs, int defStyleAttr, View frontView, View backView) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.FlipperView, defStyleAttr, 0);
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (frontView != null && backView != null) {
            this.frontView = frontView;
            this.backView = backView;
        } else {
            this.frontView = createViewFromSytleable(inflater, typedArray, frontView, R.styleable.FlipperView_frontCardLayout);
            this.backView = createViewFromSytleable(inflater, typedArray, backView, R.styleable.FlipperView_backCardLayout);
            typedArray.recycle();
        }
        initializeView();
    }

    public FlipperView setAnimationListener(Animation.AnimationListener animationListener) {
        this.animationListener = animationListener;
        return this;
    }

    public FlipperView(Context context, AttributeSet attrs, @LayoutRes int frontLayoutId, @LayoutRes int backLayoutId) {
        super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.frontView = inflater.inflate(frontLayoutId, null);
        this.backView = inflater.inflate(backLayoutId, null);
        initializeView();
    }

    private void initializeView() {
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.onClickListener = v -> flip();
        if (frontView != null && backView != null) {
            updateFrontAndBack();
        }
    }

    @Nullable
    private View createViewFromSytleable(LayoutInflater inflater, final TypedArray a, @Nullable View view, @StyleableRes int styleable) {
        if (view == null) {
            int viewResId = a.getResourceId(styleable, -1);
            if (viewResId == -1) {
                Timber.d("Front or Back view not set yet (via constructor or XML attribute - will be ignored for now)");
                view = null;
            } else {
                view = inflater.inflate(viewResId, null);
            }
        }
        return view;
    }

    public void updateFrontAndBack() {
        removeAllViews();
        View[] views = new View[]{frontView, backView};
        for (int i = 0; i < 2; i++) {
            addView(views[i]);
        }
        isBackShowing = false;
    }

    public void flip() {
        if (frontView == null || backView == null) {
            throw new NullPointerException("You must specify a front and back view for the " +
                    "FlippableView, through either a constructor, XML attribute, or method");
        }
        if (!isBackShowing) {
            if (getDisplayedChild() == 1) {
                return;  // If there is a child (to the left), stop
            }
            //animation
            setInAnimation(context, getBackViewFilpInAnimationResource());
            setOutAnimation(context, getFrontViewFilpOutAnimationResource());
            if (animationListener != null) {
                getInAnimation().setAnimationListener(animationListener);
            }
            // Display previous screen
            showPrevious();
        } else {
            if (getDisplayedChild() == 0) {
                return;  // If there aren't any other children, stop
            }
            //animation
            setInAnimation(context, getFrontViewFilpInAnimationResource());
            setOutAnimation(context, getBackViewFilpOutAnimationResource());
            if (animationListener != null) {
                getInAnimation().setAnimationListener(animationListener);
            }
            // Display next screen
            showNext();
        }
        isBackShowing = !isBackShowing;
    }

    public int getFrontViewFilpInAnimationResource() {
        if (forntViewFilpInAnimationResource == -1) {
            return R.anim.grow_from_middle;
        } else {
            return forntViewFilpInAnimationResource;
        }
    }

    public int getBackViewFilpInAnimationResource() {
        if (backViewFilpInAnimationResource == -1) {
            return R.anim.grow_from_middle;
        } else {
            return backViewFilpInAnimationResource;
        }
    }

    public int getFrontViewFilpOutAnimationResource() {
        if (forntViewFilpOutAnimationResource == -1) {
            return R.anim.shrink_to_middle;
        } else {
            return forntViewFilpOutAnimationResource;
        }
    }

    public int getBackViewFilpOutAnimationResource() {
        if (backViewFilpOutAnimationResource == -1) {
            return R.anim.shrink_to_middle;
        } else {
            return backViewFilpOutAnimationResource;
        }
    }

    public void setForntViewFilpInAnimationResource(int forntViewFilpInAnimationResource) {
        this.forntViewFilpInAnimationResource = forntViewFilpInAnimationResource;
    }

    public void setForntViewFilpOutAnimationResource(int forntViewFilpOutAnimationResource) {
        this.forntViewFilpOutAnimationResource = forntViewFilpOutAnimationResource;
    }

    public void setBackViewFilpInAnimationResource(int backViewFilpInAnimationResource) {
        this.backViewFilpInAnimationResource = backViewFilpInAnimationResource;
    }

    public void setBackViewFilpOutAnimationResource(int backViewFilpOutAnimationResource) {
        this.backViewFilpOutAnimationResource = backViewFilpOutAnimationResource;
    }

    public View getFrontView() {
        return frontView;
    }

    public void setFrontView(View frontView) {
        this.frontView = frontView;
    }

    public View getBackView() {
        return backView;
    }

    public void setBackView(View backView) {
        this.backView = backView;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
