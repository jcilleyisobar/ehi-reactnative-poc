package com.ehi.enterprise.android.ui.reservation;


import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.activity.EHIBaseActivity;
import com.ehi.enterprise.android.ui.reservation.interfaces.IKeyFactsActionDelegate;
import com.ehi.enterprise.android.ui.reservation.key_facts.KeyFactsDetailsFragment;
import com.ehi.enterprise.android.ui.reservation.key_facts.KeyFactsDetailsFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.key_facts.KeyFactsFragmentHelper;
import com.ehi.enterprise.android.utils.FragmentUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class KeyFactsActionDelegate implements IKeyFactsActionDelegate {
    private WeakReference<EHIBaseActivity> mActivity;

    public KeyFactsActionDelegate(final EHIBaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void showKeyFacts(final EHILocation pickupLocation, final List<EHIKeyFactsPolicy> policies, final EHIExtras carClassDetailsExtras) {
        if (mActivity.get() != null) {
            mActivity.get().showModal(new KeyFactsFragmentHelper.Builder().keyFactsList(policies)
                    .pickupLocation(pickupLocation)
                    .carClassDetailsExtras(carClassDetailsExtras)
                    .build());

        }
    }

    @Override
    public void onKeyFactsPolicyClicked(final EHIKeyFactsPolicy policy) {
        if (mActivity.get() != null) {
            new FragmentUtils.Transaction(mActivity.get().getSupportFragmentManager(), FragmentUtils.ADD)
                    .fragment(new KeyFactsDetailsFragmentHelper.Builder().policy(policy)
                                                                         .build())
                    .withAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .withPopAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .into(R.id.modal_container)
                    .addToBackStack(KeyFactsDetailsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onKeyFactsExclusionsClicked(final List<EHIKeyFactsPolicy> exclusions) {
        if (mActivity.get() != null) {
            new FragmentUtils.Transaction(mActivity.get().getSupportFragmentManager(), FragmentUtils.ADD)
                    .fragment(new KeyFactsDetailsFragmentHelper.Builder().policy(exclusions.get(0))
                                                                         .build())
                    .withAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .withPopAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .into(R.id.modal_container)
                    .addToBackStack(KeyFactsDetailsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onExtraItemClicked(final EHIExtraItem ehiExtraItem) {
        if (mActivity.get() != null) {
            new FragmentUtils.Transaction(mActivity.get().getSupportFragmentManager(), FragmentUtils.ADD)
                    .fragment(new KeyFactsDetailsFragmentHelper.Builder().extra(ehiExtraItem)
                                                                         .build())
                    .withAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .withPopAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .into(R.id.modal_container)
                    .addToBackStack(KeyFactsDetailsFragment.TAG)
                    .commit();

        }
    }
}
