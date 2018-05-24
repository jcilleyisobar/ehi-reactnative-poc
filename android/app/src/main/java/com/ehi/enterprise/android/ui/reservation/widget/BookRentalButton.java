package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BookRentalButtonViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(BookRentalButtonViewModel.class)
public class BookRentalButton extends DataBindingViewModelView<BookRentalButtonViewModel, BookRentalButtonViewBinding> {

    private CharSequence mSubtitle;
    private CharSequence mPrice;
    private AnimationDrawable mGenericSpinnerAnim;
    private int mPriceVisibility = View.VISIBLE;
    private OnClickListener mOnDisabledClickListener;

    //region constructors
    public BookRentalButton(Context context) {
        this(context, null, 0);
    }

    public BookRentalButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookRentalButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_book_rental_button);
            initViews();
        } else {
            addView(inflate(context, R.layout.v_book_rental_button, null));
        }
    }
    //endregion

    private void initViews() {
        getViewBinding().progressSpinner.setBackgroundResource(R.drawable.generic_spinner_white_small);
        getViewBinding().progressSpinner.setVisibility(View.INVISIBLE);
        mGenericSpinnerAnim = (AnimationDrawable) getViewBinding().progressSpinner.getBackground();

        if (mSubtitle != null) {
            setSubtitle(mSubtitle);
        }

        if (mPrice != null) {
            setPrice(mPrice);
        }

        setPriceVisibility(mPriceVisibility);

        showSubtitle(false);
    }

    public void showSubtitle(boolean showSubtitle) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getViewBinding().bookRentalView.getLayoutParams();
        if (showSubtitle) {
            getViewBinding().subtitleView.setVisibility(VISIBLE);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
        } else {
            getViewBinding().subtitleView.setVisibility(GONE);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        }

        getViewBinding().bookRentalView.setLayoutParams(layoutParams);

    }

    public void setTitle(@StringRes int title) {
        setTitle(getContext().getResources().getString(title));
    }

    public void setTitle(@NonNull CharSequence title) {
        if (getViewBinding().bookRentalView != null) {
            getViewBinding().bookRentalView.setText(title);
        }
    }

    public void setSubtitle(@NonNull CharSequence subtitle) {
        mSubtitle = subtitle;

        if (getViewBinding().subtitleView != null) {
            getViewBinding().subtitleView.setText(mSubtitle);
            showSubtitle(true);
        }
    }

    public void setPrice(@NonNull CharSequence price) {
        mPrice = price;

        if (getViewBinding().priceView != null) {
            getViewBinding().priceView.setText(mPrice);
        }
    }

    public void setPriceVisibility(int visibility) {
        mPriceVisibility = visibility;
        if (getViewBinding().priceContainer != null) {
            getViewBinding().priceContainer.setVisibility(visibility);
        }
    }

    public void setNetRateVisibility(int visibility) {
        if (getViewBinding().netRate != null) {
            getViewBinding().netRate.setVisibility(visibility);
        }
    }

    public void setPriceSubtitle(CharSequence priceSubtitle) {
        if (getViewBinding().bookRentalTotalCostView != null) {
            getViewBinding().bookRentalTotalCostView.setText(priceSubtitle);
        }
    }

    public void showProgress(boolean showProgress) {
        if (showProgress) {
            getViewBinding().bookRentalTotalCostView.setVisibility(GONE);
            getViewBinding().priceView.setVisibility(GONE);
            getViewBinding().progressSpinner.setVisibility(VISIBLE);
            mGenericSpinnerAnim.start();
        } else {
            getViewBinding().progressSpinner.setVisibility(GONE);
            mGenericSpinnerAnim.stop();
            getViewBinding().priceView.setVisibility(VISIBLE);
            getViewBinding().bookRentalTotalCostView.setVisibility(VISIBLE);
        }
    }

    public static ReactorComputationFunction title(final ReactorVar<? extends CharSequence> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setTitle(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction price(final ReactorVar<? extends CharSequence> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPrice(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction progress(final ReactorVar<Boolean> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.showProgress(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction enabled(final ReactorVar<Boolean> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setEnabled(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction subtitle(final ReactorVar<? extends CharSequence> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setSubtitle(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction priceSubtitle(final ReactorVar<? extends CharSequence> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPriceSubtitle(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction priceVisibility(final ReactorVar<? extends Integer> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setPriceVisibility(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction netRateVisibility(final ReactorVar<? extends Integer> source, final BookRentalButton target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target)) {
                    target.setNetRateVisibility(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction[] bind(final ReactorBookRentalButtonState source, final BookRentalButton target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[7];

        functions[0] = title(source.title(), target);
        functions[1] = subtitle(source.subtitle(), target);
        functions[2] = price(source.price(), target);
        functions[3] = priceSubtitle(source.priceSubtitle(), target);
        functions[4] = progress(source.progress(), target);
        functions[5] = enabled(source.enabled(), target);
        functions[6] = priceVisibility(source.priceVisibility(), target);
        functions[7] = netRateVisibility(source.netRateVisibility(), target);

        return functions;
    }

    public void setOnDisabledClickListener(OnClickListener disabledClickListener) {
        this.mOnDisabledClickListener = disabledClickListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() && mOnDisabledClickListener != null) {
            final int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                mOnDisabledClickListener.onClick(this);
                return true;
            }
        } else {
            return super.onTouchEvent(event);
        }
        return false;
    }
}
