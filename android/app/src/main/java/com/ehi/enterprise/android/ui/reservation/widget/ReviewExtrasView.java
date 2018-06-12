package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewExtraItemViewBinding;
import com.ehi.enterprise.android.databinding.ReviewExtrasViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(ReviewExtrasViewModel.class)
public class ReviewExtrasView extends DataBindingViewModelView<ReviewExtrasViewModel, ReviewExtrasViewBinding> {

    private static final String TAG = "ReviewExtrasView";

    private OnExtraActionListener mListener;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().noExtrasAddedItem.getRoot()) {
                if (mListener != null) {
                    mListener.onClick(null);
                }
            }
        }
    };

    //region constructors
    public ReviewExtrasView(Context context) {
        this(context, null, 0);
    }

    public ReviewExtrasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewExtrasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_extras, null));
            return;
        }

        createViewBinding(R.layout.v_review_extras);
        initViews();
    }
    //endregion


    private void initViews() {
        getViewBinding().noExtrasAddedItem.getRoot().setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().includedArea.visibility(), getViewBinding().includedArea));
        bind(ReactorView.visibility(getViewModel().includedExtrasContainer.visibility(), getViewBinding().includedExtrasContainer));
        bind(ReactorView.visibility(getViewModel().includedExtrasTitle.visibility(), getViewBinding().includedTitle));
        bind(ReactorView.visibility(getViewModel().mandatoryArea.visibility(), getViewBinding().mandatoryArea));
        bind(ReactorView.visibility(getViewModel().mandatoryExtrasContainer.visibility(), getViewBinding().mandatoryExtrasContainer));
        bind(ReactorView.visibility(getViewModel().mandatoryExtrasTitle.visibility(), getViewBinding().mandatoryTitle));
        bind(ReactorView.visibility(getViewModel().addedArea.visibility(), getViewBinding().addedArea));
        bind(ReactorView.visibility(getViewModel().addedExtrasContainer.visibility(), getViewBinding().addedExtrasContainer));
        bind(ReactorView.visibility(getViewModel().addedExtrasTitle.visibility(), getViewBinding().addedTitle));
        bind(ReactorView.visibility(getViewModel().noExtrasAddedView.visibility(), getViewBinding().noExtrasAddedItem.getRoot()));

        bind(ReactorView.visibility(getViewModel().noExtrasAddedViewExtraArrow.visibility(), getViewBinding().noExtrasAddedItem.extraArrow));

        addReaction("INCLUDED_EXTRAS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewBinding().includedExtrasContainer.removeAllViews();
                final List<EHIExtraItem> included = getViewModel().getIncludedExtras();
                if (included != null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    for (int i = 0, size = included.size(); i < size; i++) {
                        ReviewExtraItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_review_extra_item, getViewBinding().includedExtrasContainer, false);
                        binding.extraArrow.setVisibility(View.GONE);
                        final EHIExtraItem extraItem = included.get(i);
                        SpannableStringBuilder bld = new SpannableStringBuilder();
                        if (extraItem.getName() != null) {
                            bld.append(extraItem.getName());
                        }
                        if (extraItem.getSelectedQuantity() != null
                                && extraItem.getSelectedQuantity() > 1) {
                            SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                            count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            bld.append(" ");
                            bld.append(count);
                        }
                        binding.setExtrasText(bld);
                        getViewBinding().includedExtrasContainer.addView(binding.getRoot());
                    }
                }
            }
        });

        addReaction("MANDATORY_EXTRAS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewBinding().mandatoryExtrasContainer.removeAllViews();
                final List<EHIExtraItem> mandatory = getViewModel().getMandatoryExtras();
                if (mandatory != null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    for (int i = 0, size = mandatory.size(); i < size; i++) {
                        ReviewExtraItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_review_extra_item, getViewBinding().mandatoryExtrasContainer, false);
                        binding.extraArrow.setVisibility(GONE);
                        final EHIExtraItem extraItem = mandatory.get(i);
                        SpannableStringBuilder bld = new SpannableStringBuilder();
                        if (extraItem.getName() != null) {
                            bld.append(extraItem.getName());
                        }
                        if (extraItem.getSelectedQuantity() != null
                                && extraItem.getSelectedQuantity() > 1) {
                            SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                            count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            bld.append(" ");
                            bld.append(count);
                        }
                        binding.setExtrasText(bld);
                        getViewBinding().mandatoryExtrasContainer.addView(binding.getRoot());
                    }
                }
            }
        });

        addReaction("ADDED_EXTRAS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewBinding().addedExtrasContainer.removeAllViews();
                final List<EHIExtraItem> added = getViewModel().getAddedExtras();
                if (added != null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    for (int i = 0, size = added.size(); i < size; i++) {
                        final int currentItem = i;
                        ReviewExtraItemViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_review_extra_item, getViewBinding().addedExtrasContainer, false);
                        if (getViewModel().getHideGreenArrow()) {
                            binding.extraArrow.setVisibility(GONE);
                        }
                        EHIExtraItem extraItem = added.get(i);
                        SpannableStringBuilder bld = new SpannableStringBuilder();
                        if (extraItem.getName() != null) {
                            bld.append(extraItem.getName());
                        }
                        if (extraItem.getSelectedQuantity() != null
                                && extraItem.getSelectedQuantity() > 1) {
                            SpannableString count = new SpannableString(" (x" + extraItem.getSelectedQuantity() + ")");
                            count.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.extra_subtitle)), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            count.setSpan(new AbsoluteSizeSpan(14, true), 0, count.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            bld.append(" ");
                            bld.append(count);
                        }

                        binding.setExtrasText(bld);
                        getViewBinding().addedExtrasContainer.addView(binding.getRoot());
                        binding.getRoot().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mListener != null) {
                                    mListener.onClick(added.get(currentItem));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void setOnExtraActionListener(OnExtraActionListener listener) {
        mListener = listener;
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }

    public void setExtras(EHIExtras extras, boolean isReadOnly) {
        getViewModel().setExtras(extras, isReadOnly);
    }
}
