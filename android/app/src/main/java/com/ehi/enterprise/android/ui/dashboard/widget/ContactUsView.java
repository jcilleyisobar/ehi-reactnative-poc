package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ContactUsViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ContactUsViewViewModel.class)
public class ContactUsView extends DataBindingViewModelView<ContactUsViewViewModel, ContactUsViewBinding> {

	//region constructors
	public ContactUsView(Context context) {
		this(context, null);
	}

	public ContactUsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContactUsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode()) {
			createViewBinding(R.layout.v_contact_us);
		} else {
			addView(inflate(context, R.layout.v_contact_us, null));
		}
	}
	//endregion

	@Override
	protected void initDependencies() {
		super.initDependencies();
		bind(ReactorTextView.text(getViewModel().viewTitle.text(), getViewBinding().viewTitle));
		bind(ReactorTextView.text(getViewModel().viewSubTitle.text(), getViewBinding().viewSubtitle));
	}

	public void populateView(final String title, final String subTitle) {
		if(EHITextUtils.isEmpty(subTitle)){
			getViewBinding().viewTitle.setPadding(0,12,12,12);
			getViewBinding().viewSubtitle.setVisibility(GONE);
			getViewBinding().callIcon.setVisibility(VISIBLE);
		}
		else {
			getViewBinding().callIcon.setVisibility(GONE);
		}
		getViewModel().setViewTitle(title);
		getViewModel().setViewSubtitle(subTitle);
	}

}