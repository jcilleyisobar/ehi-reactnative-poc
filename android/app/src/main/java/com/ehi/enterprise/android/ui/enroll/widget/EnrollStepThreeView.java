package com.ehi.enterprise.android.ui.enroll.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.EnrollThirdStepViewBinding;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.login.widget.ConditionCheckRowView;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextInputLayout;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EnrollStepThreeViewViewModel.class)
public class EnrollStepThreeView extends DataBindingViewModelView<EnrollStepThreeViewViewModel, EnrollThirdStepViewBinding>
        implements FormContract.FormView {

    private static final String TAG = "EnrollStepThreeView";
    private String state;
    private String countryCode;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().signUpEmailContainer) {
                trackClick(EHIAnalytics.Action.ACTION_PROMO_EMAIL_BOX.value);
                getViewModel().signUpEmailClicked();
            } else if (view == getViewBinding().termAndConditionsContainer) {
                trackClick(EHIAnalytics.Action.ACTION_TERMS_BOX.value);
                getViewModel().termAndConditionsClicked();
            } else if (view == getViewBinding().toggleCreatePassword) {
                togglePasswordView(getViewBinding().createPassword, getViewBinding().toggleCreatePassword);
            } else if (view == getViewBinding().toggleConfirmPassword) {
                togglePasswordView(getViewBinding().confirmPassword, getViewBinding().toggleConfirmPassword);
            } else if (view == getViewBinding().termAndConditionsText) {
                trackClick(EHIAnalytics.Action.ACTION_TERMS_LINK.value);
                if (termsAndConditionsClickListener != null) {
                    termsAndConditionsClickListener.onClick(view);
                }
            }
        }
    };

    private void trackClick(String item) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_ENROLL_STEP_3.value, TAG)
                .state(state)
                .addDictionary(EHIAnalyticsDictionaryUtils.enroll(state, countryCode))
                .action(EHIAnalytics.Motion.MOTION_TAP.value, item)
                .tagScreen()
                .tagEvent();
    }

    private View.OnClickListener termsAndConditionsClickListener;

    public EnrollStepThreeView(Context context) {
        this(context, null);
    }

    public EnrollStepThreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnrollStepThreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_enroll_step_three, null));
            return;
        }

        createViewBinding(R.layout.v_enroll_step_three);

        initView();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().phoneNumber.text(), getViewBinding().phoneNumber));
        bind(ReactorTextView.bindText(getViewModel().email.text(), getViewBinding().email));
        bind(ReactorTextView.bindText(getViewModel().createPassword.text(), getViewBinding().createPassword));
        bind(ReactorTextView.bindText(getViewModel().confirmPassword.text(), getViewBinding().confirmPassword));

        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().constantCheckPassedCondition.getIconStateVar(), getViewBinding().noPasswordConstantCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().containsLetterCondition.getIconStateVar(), getViewBinding().containsLettersCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().containsNumberCondition.getIconStateVar(), getViewBinding().containsNumbersCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().minCharacterCountCondition.getIconStateVar(), getViewBinding().characterCountCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().confirmationPasswordInvalidCondition.getIconStateVar(), getViewBinding().passwordsDoNotMatchCondition));

        bind(ConditionCheckRowView.bindConditionText(getViewModel().constantCheckPassedCondition.textRes(), getViewBinding().noPasswordConstantCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().containsNumberCondition.textRes(), getViewBinding().containsNumbersCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().containsLetterCondition.textRes(), getViewBinding().containsLettersCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().minCharacterCountCondition.textRes(), getViewBinding().characterCountCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().confirmationPasswordInvalidCondition.textRes(), getViewBinding().passwordsDoNotMatchCondition));

        bind(ReactorView.visibility(getViewModel().confirmationPasswordInvalidCondition.visibility(), getViewBinding().passwordsDoNotMatchCondition));

        bind(ReactorCompoundButton.bindChecked(getViewModel().signUpEmailCheckBox.checked(), getViewBinding().signUpEmailCheckBox));
        bind(ReactorCompoundButton.bindChecked(getViewModel().termAndConditionsCheckBox.checked(), getViewBinding().termAndConditionsCheckBox));

        bind(ReactorView.visibility(getViewModel().enrollStepTitleArea.visibility(), getViewBinding().enrollStepTitleArea));
        bind(ReactorTextView.text(getViewModel().enrollStepTitle, getViewBinding().enrollStepTitle));

        bind(ReactorTextInputLayout.error(getViewModel().phoneNumberError, getViewBinding().phoneNumberLayout));
        bind(ReactorTextInputLayout.error(getViewModel().emailError, getViewBinding().emailLayout));
        bind(ReactorTextInputLayout.error(getViewModel().createPasswordError, getViewBinding().createPasswordLayout));
        bind(ReactorTextInputLayout.error(getViewModel().confirmPasswordError, getViewBinding().confirmPasswordLayout));
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile, boolean needCheckEmailNotificationsByDefault) {
        getViewModel().setPresetData(ehiEnrollProfile, needCheckEmailNotificationsByDefault);
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        return getViewModel().updateEnrollProfile(ehiEnrollProfile);
    }

    @Override
    public boolean isValid() {
        return getViewModel().isValid();
    }

    @Override
    public void highlightInvalidFields() {
        getViewModel().highlightInvalidFields();
    }

    @Override
    public List<String> getErrorMessageList() {
        return getViewModel().getErrorMessageList();
    }

    @Override
    public void startHighlightInvalidFieldsOnFormChange() {
        getViewModel().startHighlightInvalidFieldsOnFormChange();
    }

    @Override
    public void stopHighlightInvalidFieldsOnFormChange() {
        getViewModel().stopHighlightInvalidFieldsOnFormChange();
    }

    public void setFormListener(FormContract.FormListener listener) {
        getViewModel().setFormListener(listener);
    }

    public String getPassword() {
        return getViewModel().getPassword();
    }

    public void showHeader() {
        getViewModel().showHeader();
    }

    public void hideHeader() {
        getViewModel().hideHeader();
    }

    public void setTermsAndConditionsClickListener(OnClickListener listener) {
        termsAndConditionsClickListener = listener;
    }

    public void setScrollView(final ScrollView scrollView) {
        if (scrollView == null) {
            getViewBinding().confirmPassword.setOnFocusChangeListener(null);
            return;
        }

        getViewBinding().confirmPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    return;
                }

                // do some magic
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getViewBinding().passwordsDoNotMatchCondition.measure();

                            int position = getViewBinding().confirmPasswordLayout.getBottom()
                                    + getViewBinding().passwordsDoNotMatchCondition.getMeasuredHeight()
                                    + scrollView.getScrollY();

                            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                                    scrollView,
                                    "scrollY",
                                    position
                            );
                            objectAnimator.setDuration(300);
                            objectAnimator.start();
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        SpannableString spannableString = new SpannableString(
                getContext().getString(R.string.enroll_terms_and_conditions_string)
        );
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)),
                0,
                spannableString.toString().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        getViewBinding().termAndConditionsText.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.POLICIES, spannableString)
                .formatString(R.string.enroll_terms_and_conditions_title)
                .format());

        getViewBinding().signUpEmailContainer.setOnClickListener(mOnClickListener);
        getViewBinding().termAndConditionsContainer.setOnClickListener(mOnClickListener);
        getViewBinding().termAndConditionsText.setOnClickListener(mOnClickListener);
        getViewBinding().toggleCreatePassword.setOnClickListener(mOnClickListener);
        getViewBinding().toggleConfirmPassword.setOnClickListener(mOnClickListener);
    }

    private void togglePasswordView(EditText passwordEditText, ImageView toggleImageView) {
        if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_show_02));
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_show));
        }

        passwordEditText.setTypeface(passwordEditText.getTypeface());
        passwordEditText.setSelection(passwordEditText.getText().toString().length());
    }

    public void setState(String value) {
        state = value;
    }

    public void setCountryCode(String value) {
        countryCode = value;
    }
}
