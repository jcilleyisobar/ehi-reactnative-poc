package com.ehi.enterprise.android.ui.reservation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.AddCreditCardFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.card.payment.CardIOActivity;
import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(AddCreditCardViewModel.class)
public class AddCreditCardFragment extends DataBindingViewModelFragment<AddCreditCardViewModel, AddCreditCardFragmentBinding> {

    private static final String TAG = "AddCreditCardFragment";
    private static final int MY_SCAN_REQUEST_CODE = 232;
    private static final int CAMERA_REQUEST_CODE = 88;
    public static final String EXTRA_DATA = "ehi.EXTRA_ADD_CREDIT_CARD_DATA";

    //to return the flag
    public static final String EXTRA_PAYMENT_REFERENCE = "ehi.EXTRA_PAYMENT_REFERENCE";

    @Extra(boolean.class)
    public static final String EXTRA_IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    @Extra(value = boolean.class, required = false)
    public static final String EXTRA_FROM_PROFILE = "ehi.EXTRA_FROM_PROFILE";


    private static final String CARD_NAME = "CARD_NAME";
    private static final String CARD_NUMBER = "CARD_NUMBER";
    private static final String CARD_MONTH = "CARD_MONTH";
    private static final String CARD_YEAR = "CARD_YEAR";
    private static final String CARD_CCV = "CARD_CCV";
    private static final String POLICES_CHECK = "POLICES_CHECK";
    private static final String SAVE_FOR_LATER_CHECK = "SAVE_FOR_LATER_CHECK";

    public static final String SCREEN_NAME = "AddCreditCardFragment";
    public static final int REQUEST_CODE = 821;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().creditCardAddCard) {
                getViewModel().submitCard();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ADD_CREDIT_CARD.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_CARD.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().creditCardTapToScanCard) {
                attemptScan();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ADD_CREDIT_CARD.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SCAN_CC.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().haveReadConditionsCheckBox) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_PAYMENT_METHOD.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TERMS_CHECK.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                boolean checked = getViewBinding().haveReadConditionsCheckBox.isChecked();
                getViewModel().setConditionsChecked(checked);
            } else if (view == getViewBinding().haveReadConditions) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ADD_CREDIT_CARD.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PRE_PAY_POLICY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();
                getViewModel().requestPrepaymentPolicy();
            } else if (view == getViewBinding().saveForLaterUseCheckBox) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_PAYMENT_METHOD.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SAVE_CARD.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                getViewModel().setLaterUseChecked(getViewBinding().saveForLaterUseCheckBox.isChecked());
            }
        }
    };

    PermissionRequester mPermissionRequester = new PermissionRequester() {
        @Override
        public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            final boolean permissionsGranted = PermissionUtils.areAllPermissionsGranted(grantResults);

            if (permissionsGranted) {
                doScan();
            } else {
                Snackbar.make(
                        getViewBinding().getRoot(),
                        getResources().getString(R.string.payment_camera_permission_error_prompt),
                        Snackbar.LENGTH_LONG
                )
                        .setAction(R.string.snackbar_location_disable_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IntentUtils.goToAppSettings(getActivity());
                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.ehi_primary))
                        .show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        );

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        AddCreditCardFragmentHelper.Extractor extractor = new AddCreditCardFragmentHelper.Extractor(this);

        getViewModel().setIsModify(extractor.extraIsModify());
        getViewModel().setIsFromProfile(extractor.extraFromProfile() == null ? false : extractor.extraFromProfile());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createViewBinding(inflater, R.layout.fr_add_credit_card, container);
        initViews(savedInstanceState);
        getActivity().setResult(Activity.RESULT_CANCELED);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.add_card_navigation_title);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CARD_NAME, getViewModel().cardName.text().getRawValue());
        outState.putString(CARD_NUMBER, getViewModel().cardNumber.text().getRawValue());
        outState.putString(CARD_MONTH, getViewModel().cardExpirationMonth.text().getRawValue());
        outState.putString(CARD_YEAR, getViewModel().cardExpirationYear.text().getRawValue());
        outState.putString(CARD_CCV, getViewModel().cardCCV.text().getRawValue());
        outState.putBoolean(POLICES_CHECK, getViewBinding().haveReadConditionsCheckBox.isChecked());
        outState.putBoolean(SAVE_FOR_LATER_CHECK, getViewBinding().saveForLaterUseCheckBox.isChecked());

        super.onSaveInstanceState(outState);
    }

    private void initViews(@Nullable Bundle savedInstanceState) {
        getViewBinding().creditCardAddCard.setOnClickListener(mOnClickListener);
        getViewBinding().creditCardAddCard.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().creditCardTapToScanCard.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditions.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditionsCheckBox.setOnClickListener(mOnClickListener);
        getViewBinding().saveForLaterUseCheckBox.setOnClickListener(mOnClickListener);

        SpannableString reviewPrepayPolicies = new SpannableString(getResources().getString(R.string.terms_and_conditions_prepay_title));
        reviewPrepayPolicies.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, reviewPrepayPolicies.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getViewBinding().haveReadConditions.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.review_prepay_policies_read)
                .addTokenAndValue(EHIStringToken.POLICIES, reviewPrepayPolicies)
                .format());

        if (savedInstanceState != null) {
            getViewModel().cardName.text().setValue(savedInstanceState.getString(CARD_NAME, ""));
            getViewModel().cardNumber.text().setValue(savedInstanceState.getString(CARD_NUMBER, ""));
            getViewModel().cardExpirationMonth.text().setValue(savedInstanceState.getString(CARD_MONTH, ""));
            getViewModel().cardExpirationYear.text().setValue(savedInstanceState.getString(CARD_YEAR, ""));
            getViewModel().cardCCV.text().setValue(savedInstanceState.getString(CARD_CCV, ""));
            getViewBinding().haveReadConditionsCheckBox.setChecked(!savedInstanceState.getBoolean(POLICES_CHECK, false));
            getViewBinding().haveReadConditionsCheckBox.performClick();
            getViewBinding().saveForLaterUseCheckBox.setChecked(!savedInstanceState.getBoolean(SAVE_FOR_LATER_CHECK, false));
            getViewBinding().saveForLaterUseCheckBox.performClick();
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        addReaction("CARD_ADDED_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                boolean cardAdded = getViewModel().getCardAdded();

                if (cardAdded) {
                    ToastUtils.showLongToast(getActivity(), R.string.add_card_successful_message);

                    Intent data = new Intent();
                    Bundle bundle = getViewModel().getPaymentBundle();
                    if (bundle != null) {
                        data.putExtras(bundle);
                    }

                    final Activity activity = getActivity();
                    activity.setResult(Activity.RESULT_OK, data);
                    activity.finish();
                }
            }
        });

        addReaction("PREPAY_TERMS_CONDITIONS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPrepayTermsAndConditions() != null) {
                    showModal(getActivity(), new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.terms_and_conditions_prepay_title))
                            .message(getViewModel().getPrepayTermsAndConditions())
                            .build());
                    getViewModel().setPrepayTermsAndConditions(null);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (!TextUtils.isEmpty(getViewModel().getPaymentErrorMessage())) {
                    DialogUtils.showDialogWithTitleAndText(getActivity(),
                            getViewModel().getPaymentErrorMessage(),
                            getString(R.string.alert_service_error_title));
                    getViewModel().clearPaymentErrorMessage();
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldShowDebitCardWarning()) {
                    DialogUtils.showDialogWithTitleAndText(getActivity(),
                            getString(R.string.payment_method_add_debit_card_error_message),
                            getString(R.string.payment_method_add_debit_card_error_title));
                    getViewModel().clearShowDebitCard();
                }
            }
        });

        bind(ReactorTextView.drawableRight(getViewModel().cardNumber.drawableRight(), getViewBinding().creditCardNumber));

        bind(ReactorTextView.bindText(getViewModel().cardNumber.text(), getViewBinding().creditCardNumber));
        bind(ReactorTextView.bindText(getViewModel().cardCCV.text(), getViewBinding().creditCardCcv));
        bind(ReactorTextView.bindText(getViewModel().cardName.text(), getViewBinding().creditCardUserName));
        bind(ReactorTextView.bindText(getViewModel().cardExpirationMonth.text(), getViewBinding().creditCardExpirationMonth));
        bind(ReactorTextView.bindText(getViewModel().cardExpirationYear.text(), getViewBinding().creditCardExpirationYear));
        bind(ReactorView.enabled(getViewModel().cardSubmitButton.enabled(), getViewBinding().creditCardAddCard));
        bind(ReactorView.visibility(getViewModel().prepayTermsAndConditionsView.visibility(), getViewBinding().termsAndConditionsView));
        bind(ReactorView.visibility(getViewModel().saveForLaterUseView.visibility(), getViewBinding().saveForLaterUseView));

        bind(ReactorTextInputLayout.error(getViewModel().cardNameError, getViewBinding().creditCardUserNameLayout));
        bind(ReactorTextInputLayout.error(getViewModel().cardNumberError, getViewBinding().creditCardNumberLayout));
        bind(ReactorTextInputLayout.error(getViewModel().cardExpirationMonthError, getViewBinding().creditCardExpirationMonthLayout));
        bind(ReactorTextInputLayout.error(getViewModel().cardExpirationYearError, getViewBinding().creditCardExpirationYearLayout));
        bind(ReactorTextInputLayout.error(getViewModel().cardCcvError, getViewBinding().creditCardCcvLayout));

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, TAG)
                .state(EHIAnalytics.State.STATE_ADD_CREDIT_CARD.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();

        final View view = getView().findFocus();
        if (view != null) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }, 500);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                getViewModel().setCardIOData((io.card.payment.CreditCard) data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT));
            }
        }
        getViewBinding().creditCardAddCard.requestFocus();
    }

    public void attemptScan() {
        PermissionRequestHandler permissionRequestHandler = (PermissionRequestHandler) getActivity();
        permissionRequestHandler.requestPermissions(
                CAMERA_REQUEST_CODE,
                mPermissionRequester,
                Manifest.permission.CAMERA
        );
    }

    public void doScan() {
        Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, ResourcesCompat.getColor(getResources(), R.color.ehi_primary, getContext().getTheme())); // default: false


        // don't show custom translations for now because text is too long and is not wrapped
//        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_INSTRUCTIONS, getContext().getResources().getString(R.string.payment_card_scan_instructions));

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }
}
