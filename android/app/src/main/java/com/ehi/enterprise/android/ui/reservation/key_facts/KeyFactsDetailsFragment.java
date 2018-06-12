package com.ehi.enterprise.android.ui.reservation.key_facts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.KeyFactsDetailsFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(KeyFactsDetailsViewModel.class)
public class KeyFactsDetailsFragment extends DataBindingViewModelFragment<KeyFactsDetailsViewModel, KeyFactsDetailsFragmentBinding> {
    public static final String TAG = "KeyFactsDetailsFragment";
    @Extra(value = EHIKeyFactsPolicy.class, required = false)
    public static final String POLICY = "POLICY";
    @Extra(value = EHIExtraItem.class, required = false)
    public static final String EXTRA = "EXTRA";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final KeyFactsDetailsFragmentHelper.Extractor extractor = new KeyFactsDetailsFragmentHelper.Extractor(this);

        if (extractor.policy() != null) {
            getViewModel().setPolicy(extractor.policy());
        } else if (extractor.extra() != null) {
            getViewModel().setExtra(extractor.extra());
        } else {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_key_facts_details, container);
        return getViewBinding().getRoot();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(ReactorTextView.text(getViewModel().keyFactsName.text(), getViewBinding().policyTitle));
        bind(ReactorTextView.text(getViewModel().content.text(), getViewBinding().policyContent));
    }

    @Override
    public void onDetach() {
        getActivity().setTitle(R.string.reservation_about_your_rental_section_title);
        super.onDetach();

    }
}
