package com.ehi.enterprise.android.ui.onboarding.animation;

import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.widget.ImageView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BitmapUtils;
import com.ehi.enterprise.android.utils.DisplayUtils;

public abstract class BaseCarAnimationFragment<M extends ManagersAccessViewModel, T extends ViewDataBinding> extends DataBindingViewModelFragment<M, T> {
    public static float BASE_OFFSET = 120f; //temporary should be defined in DP
    public static float MEDIUM_OFFSET = 600f; //temporary should be defined in DP
    public static float MAX_OFFSET = 600f; //temporary should be defined in DP

    protected abstract int getPageNumber();

    private int mSceneWidth;
    private int mScreenWidth;
    private AnimationDrawable mCarAnimationDrawable;
    private ImageView mCarImageView;

    protected double mAvailableSpaceToMoveCar;

    protected Pair<ImageView, Integer> getViewToAnimateWithAnimation() {
        return null;
    }

    protected void initViews() {
        Pair<ImageView, Integer> viewAndAnimation = getViewToAnimateWithAnimation();
        if (viewAndAnimation != null) {
            mCarImageView = viewAndAnimation.first;
            mCarImageView.setImageResource(viewAndAnimation.second);
            mCarAnimationDrawable = (AnimationDrawable) viewAndAnimation.first.getDrawable();
        }
    }

    protected int positionCarAtSceneStart(int pageIndex) {
        final int carStartPosition = (mSceneWidth/ 4) * pageIndex;
        final int sceneStartPosition = mScreenWidth / 2 - mSceneWidth / 2;
        return sceneStartPosition + carStartPosition;
    }

    protected void stopAnimatingCar() {
        if (mCarAnimationDrawable != null && mCarAnimationDrawable.isRunning()) {
            mCarAnimationDrawable.stop();
        }
    }

    protected void animateCar() {
        if (mCarAnimationDrawable != null) {
            mCarAnimationDrawable.start();
        }
    }

    protected void prepareVariables() {
        BASE_OFFSET = getResources().getDimension(R.dimen.base_onboarding_parallax_offset);
        MEDIUM_OFFSET = BASE_OFFSET * 5;
        MAX_OFFSET = BASE_OFFSET * 15;

        mScreenWidth = DisplayUtils.getScreenWidth(getActivity());
        mSceneWidth = BitmapUtils.getBitmapWidth((BitmapDrawable) getResources().getDrawable(R.drawable.anim1_building0));
        mAvailableSpaceToMoveCar = 0.25 * mSceneWidth;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCar();
    }

    private void setupCar() {
        int initialCarPosition = positionCarAtSceneStart(getPageNumber());
        mCarImageView.setPadding(initialCarPosition, 0, 0, 0);
        mCarImageView.setImageResource(R.drawable.car_animation);
        mCarAnimationDrawable = (AnimationDrawable) mCarImageView.getDrawable();
        animateCar();
    }
}
