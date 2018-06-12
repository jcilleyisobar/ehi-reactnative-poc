package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.TextureView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.reservation.ConfirmationAnimationFragment;
import com.ehi.enterprise.android.utils.DLog;

import java.util.ArrayList;

public class MultiDrawableAnimationView extends TextureView implements TextureView.SurfaceTextureListener {

    boolean mStarted = false;
    private ArrayList<DataHolder> mDrawables = new ArrayList<>();
    private int mCurrentDrawableIndex = 0;
    private int mCurrentFrame = 0;
    private boolean mNextDrawable = false;
    private AnimationDrawable mAnimationDrawable;
    private Pair<Boolean, Drawable> mCurrentDrawable = new Pair<>(false, null);
    private RunningThread mRunningThread;
    private int mMeasureHeight = 0;
    private int mMeasureWidth = 0;
    private RenderCallback mRenderCallback = null;
    private int mCanvasColor = android.R.color.transparent;
    private Pair<Integer, Integer> mXY;
    private ConfirmationAnimationFragment.IFinishedCallback mForcedAnimationExit;

    public MultiDrawableAnimationView(Context context) {
        super(context);
    }

    public MultiDrawableAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MultiDrawableAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int resource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);
        if (resource != 0) {
            Drawable tempDrawable = getResources().getDrawable(resource);
            mMeasureWidth = tempDrawable.getIntrinsicWidth();
            mMeasureHeight = tempDrawable.getIntrinsicHeight();
        }
    }

    public void setRenderCallback(RenderCallback renderCallback) {
        mRenderCallback = renderCallback;
    }

    private Drawable getCurrentDrawable() {
        return mCurrentDrawable.second;
    }

    /**
     * @return amount of animation drawables
     */
    public int getAnimationCount() {
        return mDrawables.size();
    }

    /**
     * Add a drawable animation to the queue
     *
     * @param drawableList list of drawables for an animation
     * @param callback     callback for on completion (even if it is a repeatable)
     * @param duration     duration of each frame
     * @param oneShot      true for animation that is only one execution, false it repeats
     */
    public void addDrawable(@ArrayRes int drawableList, IFinishedAnimation callback, int duration, boolean oneShot) {
        mDrawables.add(new DataHolder(drawableList, callback, duration, oneShot));
    }

    /**
     * Used in situations where there is a repeating animation that must be stopped allowing
     * a subsequent animation to take off
     */
    public void nextDrawable() {
        mNextDrawable = true;
        if (mCurrentDrawableIndex + 1 == mDrawables.size()) {
            stop(null);
        }
    }

    public Pair<Integer, Integer> getXY() {
        return mXY;
    }

    /**
     * starts the animation. must add drawables using {@link #addDrawable(int, IFinishedAnimation, int, boolean)}
     */
    public void run() {

        if (mStarted) {
            return;
        }

        DataHolder holder = mDrawables.get(mCurrentDrawableIndex);
        mStarted = true;
        mAnimationDrawable = new AnimationDrawable(holder);
        mRunningThread = new RunningThread();
        mRunningThread.setRunning(true);
        mRunningThread.start();
    }

    /**
     * Stops thread from executing
     */
    public void stop(ConfirmationAnimationFragment.IFinishedCallback callback) {
        mForcedAnimationExit = callback;
        mStarted = false;
        if (mRunningThread != null) {
            mRunningThread.setRunning(false);
        }
        if (mRenderCallback != null) {
            mRenderCallback.onDestroy(mCurrentDrawableIndex, mCurrentFrame);
        }
    }

    /**
     * only to be used if the thread has never stoppped
     *
     * @param suspend
     */
    public void suspend(boolean suspend) {
        mRunningThread.setSuspend(suspend);
    }

    /**
     * @return true if suspended, thread continues logical operations, but nothing to render as app is in background
     */
    public boolean isSuspended() {
        return mRunningThread != null && mRunningThread.mSuspendThread;
    }

    public boolean isRunning() {
        return mRunningThread != null && mRunningThread.mThreadRunning;
    }

    public boolean canRender(Canvas canvas) {
        return (mStarted || canvas != null || mCurrentDrawable != null);
    }

    /**
     * Render the current drawable frame at the center of the provided canvas, along with callbacks
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
        lockCurrentDrawable(true);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //noinspection ResourceType
        canvas.drawColor(mCanvasColor);
        boolean willRender = false;
        if (mRenderCallback != null) {
            willRender = mRenderCallback.willRender();
            if (willRender) {
                mRenderCallback.beforeDrawableRender(canvas);
            }
        }

        int width = getCurrentDrawable().getIntrinsicWidth();
        int height = getCurrentDrawable().getIntrinsicHeight();
        int top = (getHeight() / 2) - (getCurrentDrawable().getIntrinsicHeight() / 2);
        int left = (getWidth() / 2) - (getCurrentDrawable().getIntrinsicWidth() / 2);
        mXY = new Pair(left, top);
        Rect rect = new Rect(left, top, left + width, top + height);


        getCurrentDrawable().setBounds(rect);
        getCurrentDrawable().draw(canvas);


        if (mRenderCallback != null && willRender) {
            mRenderCallback.afterDrawableRender(canvas);
        }
        lockCurrentDrawable(false);
    }

    /**
     * In case of 'src' we measure off its dimensions
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMeasureWidth == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(mMeasureWidth, mMeasureHeight);
        }

    }


    public boolean isCurrentLocked() {
        return mCurrentDrawable.first;
    }

    /**
     * mutex lock for possible implementation of handlers/additional threads
     *
     * @param lock
     */
    public void lockCurrentDrawable(boolean lock) {
        mCurrentDrawable = new Pair<>(lock, mCurrentDrawable.second);
    }

    /**
     * Attempts to stop the animation or if available will setup next drawable animation
     */
    private void stopFetching() {
        mCurrentDrawableIndex++;
        if (mCurrentDrawableIndex >= mDrawables.size()) {
            return;
        }
        mAnimationDrawable = new AnimationDrawable(mDrawables.get(mCurrentDrawableIndex));
    }

    public void setCanvasColor(int canvasColor) {
        mCanvasColor = canvasColor;
    }

    public void setCurrentDrawable(Drawable currentDrawable) {
        mCurrentDrawable = new Pair<>(mCurrentDrawable.first, currentDrawable);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stop(null);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * A utility class that contains properties relevant to the {@link AnimationDrawable}
     */
    public class DataHolder {
        public int arrayId;
        public IFinishedAnimation callBack;
        public int transitionTimeStatic;
        public ArrayList<Integer> transitionTimes;
        public boolean oneShot = true;

        public DataHolder(int arrayId, IFinishedAnimation callBack, int transitionTimeStatic, boolean oneShot) {
            this.arrayId = arrayId;
            this.callBack = callBack;
            this.transitionTimeStatic = transitionTimeStatic;
            this.oneShot = oneShot;
        }

        public DataHolder() {
        }
    }

    public class AnimationDrawable {

        private ArrayList<Integer> mDrawableIds;
        private boolean mIsOneShot = true;
        private int mStaticDelay = 33;
        private ArrayList<Integer> mTimes = null;

        public AnimationDrawable(DataHolder holder) {
            this(holder.arrayId, holder.transitionTimeStatic, holder.oneShot);
        }

        public AnimationDrawable(@ArrayRes int resId, ArrayList<Integer> pairDelays, boolean isOneShot) {
            mTimes = pairDelays;
            mIsOneShot = isOneShot;
            TypedArray imgs = getResources().obtainTypedArray(resId);
            mDrawableIds = new ArrayList<>();
            for (int a = 0; a < imgs.length(); a++) {
                mDrawableIds.add(imgs.getResourceId(a, R.drawable.logo_eplus));
            }
            imgs.recycle();
        }

        public AnimationDrawable(@ArrayRes int resId, int staticDelay, boolean isOneShot) {
            mStaticDelay = staticDelay;
            mIsOneShot = isOneShot;
            TypedArray imgs = getResources().obtainTypedArray(resId);
            mDrawableIds = new ArrayList<>();
            for (int a = 0; a < imgs.length(); a++) {
                mDrawableIds.add(imgs.getResourceId(a, R.drawable.logo_eplus));
            }
            imgs.recycle();
        }

        public int getNumberOfFrames() {
            return mDrawableIds.size();
        }

        public Drawable getFrame(int index) {
            return getResources().getDrawable(mDrawableIds.get(index));
        }

        public
        @DrawableRes
        int getFrameId(int index) {
            return mDrawableIds.get(index);
        }

        public boolean isOneShot() {
            return mIsOneShot;
        }

        public int getStaticDelay() {
            return mStaticDelay;
        }

        public int getFrameDelay(int i) {
            return (mTimes == null) ? mStaticDelay : mTimes.get(i);
        }
    }

    public class RunningThread extends Thread {

        private String TAG = RunningThread.class.getSimpleName();
        private boolean mThreadRunning = false;
        //		private final SurfaceHolder mSurfaceHolder;
        private boolean mSuspendThread = false;

        public RunningThread() {
//			mSurfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {

            int duration;
            Canvas canvas;
            boolean shouldUpdate;
            setCurrentDrawable(mAnimationDrawable.getFrame(mCurrentFrame));
            long sleepTime = System.currentTimeMillis();
            while (mThreadRunning && mCurrentDrawableIndex < mDrawables.size()) {

                duration = mAnimationDrawable.getFrameDelay(mCurrentFrame);
                shouldUpdate = System.currentTimeMillis() - sleepTime >= duration; // should frames be updated
                canvas = null;

                if (mSuspendThread) {
                    //Not rendering but still going throug logic
                    if (shouldUpdate) {
                        lockCurrentDrawable(true);
                        if (frameIteration()) {
                            setRunning(false);
                            break;
                        }
                        lockCurrentDrawable(false);
                    }
                } else {
                    try {
                        canvas = lockCanvas();
                        synchronized (MultiDrawableAnimationView.this) {
                            if (!canRender(canvas)) {
                                continue;
                            }
                            render(canvas);
                            if (shouldUpdate && frameIteration()) {
                                break;
                            }

                            canvas.save();
                        }

                    } catch (IllegalArgumentException e) {
                        DLog.e(TAG, "", e);
                    } catch (NullPointerException e) {
//						DLog.e(TAG, "", e);
                    } finally {
                        if (shouldUpdate) {
                            sleepTime = System.currentTimeMillis();
                        }
                        try {
                            unlockCanvasAndPost(canvas);
                        } catch (IllegalArgumentException | IllegalStateException e) {
                        }
                    }
                    if (!isCurrentLocked()) {
                        setCurrentDrawable(mAnimationDrawable.getFrame(mCurrentFrame));
                    }
                }
            }
            if (mForcedAnimationExit != null) {
                mForcedAnimationExit.onFinish();
            }
        }

        /**
         * @return true if the animation has reached the end of its queued animation
         */
        private boolean frameIteration() {
            mCurrentFrame++;
            if (mCurrentFrame == mAnimationDrawable.getNumberOfFrames()) {
                mCurrentFrame = 0;
                int currentDrawableIndex = (mCurrentDrawableIndex == mDrawables.size()) ?
                        mCurrentDrawableIndex - 1 : mCurrentDrawableIndex;
                if (mDrawables.get(currentDrawableIndex).callBack != null) {
                    mDrawables.get(currentDrawableIndex).callBack.onAnimationFinish();
                }
                if (mAnimationDrawable.isOneShot()) {
                    stopFetching();
                    if (mCurrentDrawableIndex == mDrawables.size()) {
                        return true;
                    }
                }
            }
            if (mNextDrawable) {
                mNextDrawable = false;
                mCurrentFrame = 0;
                stopFetching();
            }
            return false;
        }

        public void setRunning(boolean running) {
            mThreadRunning = running;
        }

        /**
         * Used to suspend the rending thread.
         * This will not stop thread execution, but will attempt to stop rendering
         */
        public void setSuspend(boolean suspend) {
            mSuspendThread = suspend;
        }
    }

    public interface IFinishedAnimation {
        void onAnimationFinish();
    }

    public static class RenderCallback {
        private long mUpdateTime = 4;
        private long mWaitDuration = 0;
        private boolean mUpdate;
        long mLastSleepTime = System.currentTimeMillis();

        /**
         * This is a callback that is called prior to drawable render
         * It is important for you to consider a scenario where the activity
         * is paused, aka suspended
         *
         * @param canvas
         */
        public void beforeDrawableRender(Canvas canvas) {

        }

        /**
         * This is a callback that is called after to drawable render
         * It is important for you to consider a scenario where the activity
         * is paused, aka suspended
         *
         * @param canvas
         */
        public void afterDrawableRender(Canvas canvas) {

        }

        /**
         * A callback used to inform the container view of the frame. Allows extensibility
         * to encompass scenarios where animation may be stopped/destroyed.
         *
         * @param currentDrawableIndex
         * @param currentFrame
         */
        public void onDestroy(int currentDrawableIndex, int currentFrame) {

        }

        public void setsUpdateTime(long sUpdateTime) {
            mUpdateTime = sUpdateTime;
        }

        /**
         * Called by the {@link MultiDrawableAnimationView} in order to determine if the {@link RenderCallback} plan to render to the canvas. A scenario being where the drawable is rendered every 33ms and the callbacks wish to render every 5 ms
         *
         * @return true if the callbacks will render, false if not
         */
        public boolean willRender() {
            mUpdate = System.currentTimeMillis() - mLastSleepTime > mUpdateTime;
            if (!mUpdate) {
                return false;
            }
            mWaitDuration = System.currentTimeMillis() - mLastSleepTime;
            mLastSleepTime = System.currentTimeMillis(); // you may override and choose to measure time elsewhere...
            return true;
        }

        /**
         * @return is the multiple of wait that you actually waited for the execution to come back, used for calculations...
         */
        public float catchupMultiple() {
            return mWaitDuration / mUpdateTime;
        }

    }


}
