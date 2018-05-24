package com.ehi.enterprise.android.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ChangePasswordFragmentBinding;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.widget.ConditionCheckRowView;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;


@ViewModel(ChangePasswordViewModel.class)
public class ChangePasswordFragment extends DataBindingViewModelFragment<ChangePasswordViewModel, ChangePasswordFragmentBinding> {

    @Extra(value = String.class, required = false)
    public static final String CHANGE_PASSWORD_USER_NAME = "CHANGE_PASSWORD_USER_NAME";

    @Extra(value = Boolean.class, required = false)
    public static final String CHANGE_PASSWORD_IS_EC_FLOW = "CHANGE_PASSWORD_IS_EC_FLOW";

    @Extra(value = Boolean.class, required = false)
    public static final String CHANGE_PASSWORD_REMEMBER_ME = "CHANGE_PASSWORD_REMEMBER_ME";

    @Extra(value = String.class, required = false)
    public static final String CHANGE_PASSWORD_OLD_PASSWORD = "CHANGE_PASSWORD_OLD_PASSWORD";


    public static final String SCREEN_NAME = "ChangePasswordFragment";
    public static final int REQUEST_CODE = 644;

    //region OnClickListeners
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getViewBinding().submitButton == view) {
                getViewModel().attemptPasswordChange();
            } else if (view == getViewBinding().toggleConfirmPassword) {
                togglePasswordView(getViewBinding().confirmPasswordEditText, getViewBinding().toggleConfirmPassword);
            } else if (view == getViewBinding().toggleNewPassword) {
                togglePasswordView(getViewBinding().newPasswordEditText, getViewBinding().toggleNewPassword);
            }
        }
    };
    //endregion

    //region lifecycle
    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, ChangePasswordFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_CHANGE_PASSWORD.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_change_password, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangePasswordFragmentHelper.Extractor extractor = new ChangePasswordFragmentHelper.Extractor(this);
        if (extractor.changePasswordUserName() != null) {
            getViewModel().setUserName(extractor.changePasswordUserName());
            getViewModel().setRememberCredentials(extractor.changePasswordRememberMe());
            getViewModel().setResetPasswordFlow(extractor.changePasswordIsEcFlow());
            getViewModel().setOldPassword(extractor.changePasswordOldPassword());
        }
    }

    //endregion

    private void initViews() {
        getViewBinding().submitButton.setOnClickListener(mOnClickListener);
        getViewBinding().toggleNewPassword.setOnClickListener(mOnClickListener);
        getViewBinding().toggleConfirmPassword.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));

        bind(ReactorView.visibility(getViewModel().changePasswordBanner.visibility(), getViewBinding().forceChangePasswordBanner));
        bind(ReactorView.background(getViewModel().newPasswordContainer.background(), getViewBinding().newPasswordContainer));
        bind(ReactorView.background(getViewModel().confirmPasswordContainer.background(), getViewBinding().confirmPasswordContainer));

        bind(ReactorTextView.bindText(getViewModel().newPassword.text(), getViewBinding().newPasswordEditText));
        bind(ReactorTextView.bindText(getViewModel().confirmPassword.text(), getViewBinding().confirmPasswordEditText));

        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().constantCheckPassedCondition.getIconStateVar(), getViewBinding().noPasswordConstantCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().containsLetterCondition.getIconStateVar(), getViewBinding().containsLettersCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().containsNumberCondition.getIconStateVar(), getViewBinding().containsNumbersCondition));
        bind(ConditionCheckRowView.bindCheckStateToCondition(getViewModel().minCharacterCountCondition.getIconStateVar(), getViewBinding().characterCountCondition));
        bind(ConditionCheckRowView.bindCheckMarkViewState(getViewModel().isConfirmPasswordValid, getViewBinding().passwordsDoNotMatchCondition));

        bind(ConditionCheckRowView.bindConditionText(getViewModel().constantCheckPassedCondition.textRes(), getViewBinding().noPasswordConstantCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().containsNumberCondition.textRes(), getViewBinding().containsNumbersCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().containsLetterCondition.textRes(), getViewBinding().containsLettersCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().minCharacterCountCondition.textRes(), getViewBinding().characterCountCondition));
        bind(ConditionCheckRowView.bindConditionText(getViewModel().confirmationPasswordInvalidCondition.textRes(), getViewBinding().passwordsDoNotMatchCondition));

        bind(ReactorView.visibility(getViewModel().confirmationPasswordInvalidCondition.visibility(), getViewBinding().passwordsDoNotMatchCondition));

        bind(ReactorView.enabled(getViewModel().isUpdatePasswordButtonEnabled, getViewBinding().submitButton));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction("PASSWORD_UPDATE_SUCCESS_RESPONSE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().successResponse();
                if (!reactorComputation.isFirstRun()) {
                    Toast.makeText(getActivity(), R.string.cp_successfully_changed, Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        addReaction("PASSWORD_UPDATE_FAILED_RESPONSE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getPasswordResponse();
                if (wrapper != null) {
                    Toast.makeText(getActivity(), wrapper.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
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

}