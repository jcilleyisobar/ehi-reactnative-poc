package com.ehi.enterprise.android.ui.reservation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ConfirmationAnimationFragmentViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.widgets.MultiDrawableAnimationView;
import com.ehi.enterprise.android.ui.reservation.interfaces.BackButtonBlockListener;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class ConfirmationAnimationFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, ConfirmationAnimationFragmentViewBinding> {

    private IFinishedCallback mFinishCallback;
    private boolean mAttached = true;
    private Handler mHandler;

    private float mGrowthRate = 0f;
    private boolean mStartEndAnimation = false;
    private final static int ANIMATION_TIME = 33;
    private boolean mIncrementCircle = false;

    private MultiDrawableAnimationView.RenderCallback mRenderCallback = new MultiDrawableAnimationView.RenderCallback() {
        @Override
        public void beforeDrawableRender(final Canvas canvas) {
            try {
                getViewBinding().growingCircleView.draw(canvas);
            } catch (AndroidRuntimeException e) {
                //ignoring exception
            }
            if (mIncrementCircle) {
                if (getActivity() == null) {
                    return;
                } else {
                    getViewBinding().growingCircleView.setRadius(getViewBinding().growingCircleView.getRadius() + (mGrowthRate * catchupMultiple()));
                }
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mAttached = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttached = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_confirmation_animation, container);


        //Calculating speed at which we need to grow in order to cover screen
        float height = (float) Math.sqrt(Math.pow(DisplayUtils.getScreenHeight(getActivity()), 2)
                + Math.pow(DisplayUtils.getScreenWidth(getActivity()), 2)) + 300; // Pyth theorem + buffer
        float time = (((AnimationDrawable) getResources().getDrawable(R.drawable.check_rental_animation)).getNumberOfFrames()
                * ((AnimationDrawable) getResources().getDrawable(R.drawable.check_rental_animation)).getDuration(0))
                + (((AnimationDrawable) getResources().getDrawable(R.drawable.book_rental_spinner_finish)).getDuration(0)
                * ((AnimationDrawable) getResources().getDrawable(R.drawable.book_rental_spinner_finish)).getNumberOfFrames());

        time /= 4f;
        mGrowthRate = height / time;

        getViewBinding().animationView.setCanvasColor(getResources().getColor(R.color.ehi_primary));
        getViewBinding().animationView.setRenderCallback(mRenderCallback);

        return getViewBinding().getRoot();
    }


    public void forceStopAnimation(IFinishedCallback finishCallback) {
        getViewBinding().animationView.stop(finishCallback);
    }

    public void endAnimation(IFinishedCallback finishedCallback) {
        mStartEndAnimation = true;
        mFinishCallback = finishedCallback;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getViewBinding().animationView.getAnimationCount() == 0) {
            getViewBinding().animationView.addDrawable(R.array.start_confirmation_cycle, null, ANIMATION_TIME, true);
            getViewBinding().animationView.addDrawable(R.array.repeat_confirmation_cycle, new MultiDrawableAnimationView.IFinishedAnimation() {
                @Override
                public void onAnimationFinish() {

                    if (mAttached && mStartEndAnimation) {
                        getViewBinding().animationView.nextDrawable();
                    }
                }
            }, ANIMATION_TIME, false);

            getViewBinding().animationView.addDrawable(R.array.end_confirmation_cycle, new MultiDrawableAnimationView.IFinishedAnimation() {
                @Override
                public void onAnimationFinish() {
                    float middleX = getViewBinding().growingCircleView.getWidth() / 2;
                    float middleY = getViewBinding().growingCircleView.getHeight() / 2;
                    getViewBinding().growingCircleView.setXY(new Pair<>(
                            middleX,
                            middleY
                    ));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            int height = (getResources().getDrawable(R.drawable.check_mark_icon_00001).getIntrinsicHeight());
                            getViewBinding().successMessage.setVisibility(View.VISIBLE);
                            getViewBinding().successMessage.setY((getViewBinding().parentContainer.getHeight() / 2)
                                    - (height));

                        }
                    });
                    mIncrementCircle = true;
                }
            }, ANIMATION_TIME, true);
            getViewBinding().animationView.addDrawable(R.array.checkmark_confirmation_cycle, new MultiDrawableAnimationView.IFinishedAnimation() {
                @Override
                public void onAnimationFinish() {
                    if (mFinishCallback != null) {
                        mFinishCallback.onFinish();
                    }
                }
            }, ANIMATION_TIME, true);

            getViewBinding().animationView.post(new Runnable() {
                @Override
                public void run() {
                    getViewBinding().animationView.run();
                }
            });
        }
    }

    @Override
    public void onResume() {
        ((BackButtonBlockListener) getActivity()).blockBackPressed(true);
        super.onResume();
        DLog.d("confirmation", "isRunning = " + getViewBinding().animationView.isRunning() +
                " isSuspended = " + getViewBinding().animationView.isSuspended());
        if (getViewBinding().animationView.isRunning() && getViewBinding().animationView.isSuspended()) {
            getViewBinding().animationView.suspend(false);
        }
    }

    @Override
    public void onPause() {
        ((BackButtonBlockListener) getActivity()).blockBackPressed(false);
        super.onPause();
        getViewBinding().animationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                getViewBinding().animationView.suspend(true);
            }
        }, 200);

    }

    public interface IFinishedCallback {
        void onFinish();
    }

}
