package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.support.v4.util.Pair;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ConfirmationHeaderViewBinding;
import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ConfirmationHeaderViewModel.class)
public class ConfirmationHeaderView extends DataBindingViewModelView<ConfirmationHeaderViewModel, ConfirmationHeaderViewBinding> {

    public ConfirmationHeaderView(Context context) {
        this(context, null, 0);
    }

    public ConfirmationHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmationHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_confirmation_header, null));
            return;
        }

        createViewBinding(R.layout.v_confirmation_header);
        initViews();
    }

    private void initViews() {
        String text = getViewBinding().youAreAllSetTextView.getText().toString();

        int position = text.indexOf('\n');

        if (position != -1) {
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary));
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            builder.setSpan(span, position, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            getViewBinding().youAreAllSetTextView.setText(builder);
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().confirmationNumberText.text(), getViewBinding().confirmationNumberTextView));
        addReaction("CAR_CLASS_IMAGE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                List<EHIImage> images = getViewModel().getImagesList();
                if (images != null
                        && EHIImageUtils.getCarClassImageUrl(images, EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE, getViewBinding().carImageView.getWidth()) != null) {
                    final Pair<Integer, Integer> defaultCarImageMeasureSpec = BaseAppUtils.getDefaultCarImageMeasureSpec(getContext());
                    getViewBinding().carImageView.measure(defaultCarImageMeasureSpec.first, defaultCarImageMeasureSpec.second);
                    EHIImageLoader.with(getContext())
                            .load(EHIImageUtils.getCarClassImageUrl(images,
                                    EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE,
                                    getViewBinding().carImageView.getMeasuredWidth()))
                            .into(getViewBinding().carImageView);
                    getViewBinding().carImageView.setTranslationX(getViewBinding().carImageView.getMeasuredWidth() / 3);
                }
            }
        });
    }

    public void setConfirmationNumber(String number) {
        getViewModel().setConfirmationNumber(number);
    }

    public void setCarClassDetails(EHICarClassDetails details) {
        getViewModel().setCarClassDetails(details);
    }

}