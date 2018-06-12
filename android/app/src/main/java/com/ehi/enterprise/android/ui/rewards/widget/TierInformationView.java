package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.TierInformationViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class TierInformationView extends DataBindingViewModelView<ManagersAccessViewModel, TierInformationViewBinding> {

    private static final float TRIANGLE_WIDTH = 16f;
    private static final float TRIANGLE_HEIGHT = 8f;

    private static float MEASURED_TRIANGLE_WIDTH;
    private static float MEASURED_TRIANGLE_HEIGHT;

    private Paint mPaint;
    private Path mPath;

    public TierInformationView(Context context) {
        this(context, null, 0);
    }

    public TierInformationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TierInformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_tier_information);
        init();
    }

    private void init() {
        MEASURED_TRIANGLE_WIDTH = DisplayUtils.dipToPixels(getContext(), TRIANGLE_WIDTH);
        MEASURED_TRIANGLE_HEIGHT = DisplayUtils.dipToPixels(getContext(), TRIANGLE_HEIGHT);

        mPaint = new Paint();
        mPath = new Path();
        mPaint.setStrokeWidth(1f);
        mPath.setFillType(Path.FillType.EVEN_ODD);
        invalidate();
        setWillNotDraw(false);
    }

    public void setupView(String leftText, String rightText, @ColorRes int leftColor) {

        getViewBinding().leftText.setText(leftText);
        getViewBinding().rightText.setText(rightText);
        getViewBinding().leftText.setBackgroundColor(getResources().getColor(leftColor));

        mPaint.setColor(getResources().getColor(leftColor));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isInEditMode()) {
            return;
        }
        if (getViewBinding().leftText == null) {
            return;
        }

        float positionY = (canvas.getHeight() / 2) - (MEASURED_TRIANGLE_WIDTH / 2);
        float positionX = getViewBinding().leftText.getWidth() + MEASURED_TRIANGLE_HEIGHT;
        mPath.rewind();

        mPath.moveTo(getViewBinding().leftText.getWidth(), positionY);
        mPath.lineTo(positionX, canvas.getHeight() / 2);
        mPath.lineTo(getViewBinding().leftText.getWidth(), positionY + MEASURED_TRIANGLE_WIDTH);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
        canvas.save();
    }

}
