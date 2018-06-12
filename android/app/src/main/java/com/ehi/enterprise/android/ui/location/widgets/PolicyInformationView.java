package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PolicyCellViewBinding;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.location.interfaces.OnPoliciesInfoClickListener;
import com.ehi.enterprise.android.ui.viewholder.DataBindingViewHolder;

import java.util.HashMap;
import java.util.List;

public class PolicyInformationView extends LinearLayout {

	private static final String TAG = PolicyInformationView.class.getSimpleName();

	private boolean mAlreadyInflated = false;
	private HashMap<String, Integer> mPoliciesToSurface;

	private OnPoliciesInfoClickListener mListener;
	private boolean mPoliciesPopulated;

	public PolicyInformationView(Context context) {
		super(context);
	}

	public PolicyInformationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PolicyInformationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (!mAlreadyInflated) {
			setOrientation(VERTICAL);
			mPoliciesToSurface = new HashMap<>();
			mPoliciesToSurface.put("RQMT", 0);
			mPoliciesToSurface.put("AGE", 1);
			mPoliciesToSurface.put("PYMT", 2);

			mAlreadyInflated = true;
			mPoliciesPopulated = false;
		}
	}

	public void setOnPoliciesInfoClickListener(OnPoliciesInfoClickListener listener) {
		mListener = listener;
	}

	public void setPolicies(List<EHIPolicy> policies) {
		if (policies == null || policies.size() == 0) {
			setVisibility(View.GONE);
			return;
		}
		else if (!mPoliciesPopulated) {
			setVisibility(View.VISIBLE);
		}
		else {
			return;
		}

		final SparseArray<EHIPolicy> policiesToDisplay = new SparseArray<>();
		for (EHIPolicy policy : policies) {
			if (mPoliciesToSurface.containsKey(policy.getCode())) {
				policiesToDisplay.put(mPoliciesToSurface.get(policy.getCode()), policy);
			}
		}

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		int key;
		for (int i = 0; i < policiesToDisplay.size(); i++) {
			key = policiesToDisplay.keyAt(i);

			final EHIPolicy ehiPolicy = policiesToDisplay.get(key);
			if (ehiPolicy != null) {
				PolicyCellViewHolder policyCell = new PolicyCellViewHolder((PolicyCellViewBinding) DataBindingUtil.inflate(layoutInflater,R.layout.v_policy_cell, this, false),
						ehiPolicy.getDescription());
				policyCell.getPolicyView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mListener.onClickPolicy(ehiPolicy);
					}
				});
				policyCell.getPolicyView().setId(key);
				addView(policyCell.getPolicyView());
			}
		}

		PolicyCellViewHolder morePolicies = new PolicyCellViewHolder((PolicyCellViewBinding) DataBindingUtil.inflate(layoutInflater,R.layout.v_policy_cell, this, false),
				getResources().getString(R.string.location_details_more_policies),
				false);
		morePolicies.getPolicyView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onShowMorePolicies();
			}
		});
		morePolicies.getPolicyView().setId(policiesToDisplay.size() + 1);
		morePolicies.setPolicyTextColor(getResources().getColor(R.color.ehi_primary));
		addView(morePolicies.getPolicyView());

		mPoliciesPopulated = true;
	}

	private static class PolicyCellViewHolder extends DataBindingViewHolder<PolicyCellViewBinding> {

		private String mPolicyTextContent;
		private boolean mShowDivider = true;

		public PolicyCellViewHolder(PolicyCellViewBinding viewBinding, String policyTextContent) {
			super(viewBinding);
			mPolicyTextContent = policyTextContent;
			setPolicyText(mPolicyTextContent);
			showDivider(mShowDivider);
		}

		public PolicyCellViewHolder(PolicyCellViewBinding viewBinding, String policyTextContent, boolean showDivider) {
			super(viewBinding);
			mPolicyTextContent = policyTextContent;
			mShowDivider = showDivider;
			setPolicyText(mPolicyTextContent);
			showDivider(mShowDivider);
		}

		public void setPolicyText(@NonNull String text) {
			getViewBinding().policyInfoText.setText(text);
		}

		public void setPolicyTextColor(@ColorInt int color) {
			getViewBinding().policyInfoText.setTextColor(color);
		}

		public void showDivider(boolean show) {
			getViewBinding().divider.setVisibility(show ? VISIBLE : GONE);
		}

		public View getPolicyView() {
			return getViewBinding().getRoot();
		}
	}
}
