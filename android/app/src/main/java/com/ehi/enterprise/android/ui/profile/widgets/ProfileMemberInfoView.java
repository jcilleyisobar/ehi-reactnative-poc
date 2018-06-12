package com.ehi.enterprise.android.ui.profile.widgets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ItemProfilePhonePaymentViewBinding;
import com.ehi.enterprise.android.databinding.ProfileMemberInfoBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

@ViewModel(ManagersAccessViewModel.class)
public class ProfileMemberInfoView extends DataBindingViewModelView<ManagersAccessViewModel, ProfileMemberInfoBinding> {

    public ProfileMemberInfoView(Context context) {
        this(context, null, 0);
    }

    public ProfileMemberInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileMemberInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_profile_member_info, null));
            return;
        }

        createViewBinding(R.layout.v_profile_member_info);
    }

    public void setProfile(ProfileCollection profile) {
        final EHILoyaltyData ehiLoyaltyData = profile.getBasicProfile().getLoyaltyData();

        getViewBinding().profileNameView.setText(profile.getBasicProfile().getFullName());
        getViewBinding().profileEmailView.setText(profile.getContactProfile().getMaskEmail());
        getViewBinding().profileMemberIdView.setText(ehiLoyaltyData != null ? ehiLoyaltyData.getLoyaltyNumber() : "");
        if (!ListUtils.isEmpty(profile.getAddressProfile().getStreetAddresses())) {
            getViewBinding().profileAddressView.setText(profile.getAddressProfile().getStreetAddresses().get(0));
        }

        if (profile.getProfile().hasCorporateAccount()) {
            getViewBinding().profileAccountIdView.setText(profile.getProfile().getCorporateAccount().getMaskedName());
            getViewBinding().profileAccountArea.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().profileAccountArea.setVisibility(View.GONE);
        }

        List<EHIPhone> phones = profile.getContactProfile().getPhones();
        getViewBinding().profilePhonesContainer.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < phones.size(); i++) {
            EHIPhone number = phones.get(i);
            SpannableStringBuilder bld = new SpannableStringBuilder();
            bld.append(number.getMaskPhoneNumber());
            bld.append(" ");
            SpannableString phoneTypeSpan = new SpannableString("(" + getResources().getString(number.getTypeString()) + ")");
            phoneTypeSpan.setSpan(new RelativeSizeSpan(0.75f), 0, phoneTypeSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            bld.append(phoneTypeSpan);

            ItemProfilePhonePaymentViewBinding binding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_profile_phone_payment,
                    getViewBinding().profilePhonesContainer,
                    false);
            binding.itemProfilePhonePaymentView.setText(bld);
            getViewBinding().profilePhonesContainer.addView(binding.getRoot());
        }

    }
}