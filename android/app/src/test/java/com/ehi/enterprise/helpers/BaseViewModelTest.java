package com.ehi.enterprise.helpers;

import android.view.View;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.ehi.enterprise.mock.android.DefaultMockedContext;
import com.ehi.enterprise.mock.network.MockRequestProcessor;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

public abstract class BaseViewModelTest<T extends ManagersAccessViewModel> {

	private DefaultMockedContext mMockedContext;
	private MockManagerDelegate mMockedDelegate;
	private MockRequestProcessor mMockRequestProcessor;
	protected abstract Class<T> getViewModelClass();

	private T mViewModel;

	@Before
	public void setup() {
		try {
			getViewModel().onAttachToView();
		}
		catch (Exception e) {
			// Do nothing, this lets tests handle possible cases where the viewmodel's onAttachToView could throw an exception
		}
		ReactorViewState.VISIBLE = 0x00000000;
		ReactorViewState.INVISIBLE = 0x00000004;
		ReactorViewState.GONE = 0x00000008;
	}

	@After
	public void cleanUp() {
		getViewModel().onDetachFromView();
		mViewModel = null;
	}

	protected DefaultMockedContext getMockedContext(){
		if(mMockedContext == null){
			mMockedContext = new DefaultMockedContext();
		}
		return mMockedContext;
	}

	protected MockManagerDelegate getMockedDelegate(){
		if(mMockedDelegate == null){
			mMockedDelegate = new MockManagerDelegate();
		}
		return mMockedDelegate;
	}

	public T getViewModel() {
		if (mViewModel == null) {
			mViewModel = Mockito.spy(getViewModelClass());
			mViewModel.setResources(getMockedContext().getMockedResources().getMockedObject());
			mViewModel.setManagersDelegate(getMockedDelegate());
			mViewModel.setApiService(getMockRequestService());
		}
		return mViewModel;
	}

	public static void assertVisible(int visibility) {
		Assert.assertEquals(View.VISIBLE, visibility);
	}

	public static void assertGone(int visibility) {
		Assert.assertEquals(View.GONE, visibility);
	}

	public static void assertInvisible(int visibility) {
		Assert.assertEquals(View.INVISIBLE, visibility);
	}

	public static void assertVisible(ReactorViewState viewState) {
		Assert.assertEquals(View.VISIBLE, viewState.visibility().getRawValue().intValue());
	}

	public static void assertGone(ReactorViewState viewState) {
		Assert.assertEquals(View.GONE, viewState.visibility().getRawValue().intValue());
	}

	public static void assertInvisible(ReactorViewState viewState) {
		Assert.assertEquals(View.INVISIBLE, viewState.visibility().getRawValue().intValue());
	}

	public MockRequestProcessor getMockRequestService() {
		if(mMockRequestProcessor == null){
			mMockRequestProcessor = new MockRequestProcessor();
		}
		return mMockRequestProcessor;
	}
}