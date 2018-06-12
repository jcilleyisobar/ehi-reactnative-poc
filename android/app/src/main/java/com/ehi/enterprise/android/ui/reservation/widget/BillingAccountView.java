package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BillingAccountViewBinding;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.request_params.reservation.CommitRequestParams;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.widget.EHISpinnerView;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(BillingAccountViewModel.class)
public class BillingAccountView extends DataBindingViewModelView<BillingAccountViewModel, BillingAccountViewBinding> {

    //region constructors
    public BillingAccountView(Context context) {
        this(context, null);
    }

    public BillingAccountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BillingAccountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_account_billing, null));
            return;
        }

        setSaveEnabled(true);
        createViewBinding(R.layout.v_account_billing);
    }
    //endregion


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        SavedState savedState = new SavedState(state);
        savedState.lastBillingOptionSelected = getViewModel().getBillingSelectedOption();
        savedState.lastPaymentOptionSelected = getViewModel().getPaymentSelectedOption();
        savedState.lastViewState = getViewModel().getCheckboxState();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        getViewModel().setLastBillingSelected(ss.lastBillingOptionSelected);
        getViewModel().setLastPaymentSelected(ss.lastPaymentOptionSelected);
        getViewModel().setLastCheckboxState(ss.lastViewState);
    }

    //region onclick listeners
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewBinding().newBillingCode.setText("");
        }
    };
    private OnClickListener mCheckClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().setBillingCheckRowChecked(getViewBinding().billingCodeCheckRow == view);
        }
    };
    //endregion

    public void populateView(ProfileCollection profileCollection, EHIReservation reservationObject, IBillingCallBack callBack) {
        getViewBinding().payAtCounterCheckRow.setOnClickListener(mCheckClickListener);
        getViewBinding().billingCodeCheckRow.setOnClickListener(mCheckClickListener);
        getViewBinding().clearText.setOnClickListener(mOnClickListener);
        getViewBinding().payAtCounterCheckRow.setDisabledOn(true, false);
        getViewBinding().billingCodeCheckRow.setDisabledOn(true, false);

        getViewModel().setup(profileCollection, reservationObject, callBack);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        getViewModel().lockBillingCallback(true);

        bind(ReactorTextView.bindText(getViewModel().newBillingCode,getViewBinding().newBillingCode));
        bind(ReactorView.visibility(getViewModel().billingCodeEditTextVisibility, getViewBinding().billingCodeTextContainer));
        bind(ReactorView.visibility(getViewModel().payAtCounterSpinnerVisible, getViewBinding().payAtCounterSpinner));
        bind(ReactorView.visibility(getViewModel().billingSpinnerVisibility, getViewBinding().billingCodeSpinner));
        bind(ReactorView.visibility(getViewModel().rootViewVisible, this));
        bind(ReactorTextView.text(getViewModel().maskedBillingAccountText.text(), getViewBinding().billingMaskedDefaultTextView));
        bind(ReactorView.visibility(getViewModel().maskedBillingAccountText.visibility(), getViewBinding().billingMaskedDefaultTextView));
        bind(EHISpinnerView.spinnerSelection(getViewModel().billingSpinnerSelection, getViewBinding().billingCodeSpinner));
        bind(EHISpinnerView.spinnerSelection(getViewModel().paymentSpinnerSelection, getViewBinding().payAtCounterSpinner));
        bind(ReactorTextView.text(getViewModel().billingAccountDescription.textCharSequence(), getViewBinding().billingDescription));
        bind(ReactorView.visibility(getViewModel().billingAccountDescription.visibility(), getViewBinding().billingDescription));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().billingSpinnerVisibility.getValue() == VISIBLE) {
                    getViewBinding().billingCodeSpinner.populateView(getViewModel().getBillingOptionsText(), getViewModel().getBillingSelectedOption(), getResources().getString(R.string.review_payment_options_billing_title));
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().payAtCounterSpinnerVisible.getValue() == View.VISIBLE) {
                    getViewBinding().payAtCounterSpinner.setVisibility(VISIBLE);
                    getViewBinding().payAtCounterSpinner.populateView(getViewModel().getPaymentOptionsText(), getViewModel().getPaymentSelectedOption(), getResources().getString(R.string.review_payment_options_payment_title));
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                setupCheckboxes(!getViewModel().isBillingAccountViewChecked());
            }
        });

        getViewModel().lockBillingCallback(false);
    }

    private void setupCheckboxes(boolean payAtCounterIsChecked) {
        getViewBinding().payAtCounterCheckRow.setChecked(payAtCounterIsChecked);
        getViewBinding().billingCodeCheckRow.setChecked(!payAtCounterIsChecked);
    }

    /***
    * Used as a wrapper to save instance of view.
    ***/
    private static class SavedState extends BaseSavedState {
        int lastBillingOptionSelected;
        int lastPaymentOptionSelected;
        @BillingAccountViewModel.CheckboxState int lastViewState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            lastBillingOptionSelected = in.readInt();
            lastPaymentOptionSelected = in.readInt();
            lastViewState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(lastBillingOptionSelected);
            out.writeInt(lastPaymentOptionSelected);
            out.writeInt(lastViewState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Will NOT show the view if the populate view logic shows that it cannot be revealed. If business logic may have changed, re-populate the view.
     *
     * @param visibility
     */
    @Override
    public void setVisibility(int visibility) {
        if (getViewModel().isHideForever() && visibility == VISIBLE) {
            return;
        }
        getViewModel().setRootViewVisibility(visibility == VISIBLE);

        super.setVisibility(visibility);
    }

    public interface IBillingCallBack {
        void onBillingMethodChanged(String billingNumber, @CommitRequestParams.BillingTypes String billingType, List<String> paymentIds, @CommitRequestParams.BillingMethods String method);
    }
}