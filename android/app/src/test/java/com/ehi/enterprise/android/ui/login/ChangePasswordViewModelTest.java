package com.ehi.enterprise.android.ui.login;

import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ChangePasswordViewModelTest extends BaseViewModelTest<ChangePasswordViewModel> {

	@Override
	protected Class<ChangePasswordViewModel> getViewModelClass() {
		return ChangePasswordViewModel.class;
	}

	@Before
	public void before() {
		getViewModel().onAttachToView();
		assertFalse(getViewModel().isNewPasswordValid.getRawValue());
		assertFalse(getViewModel().isConfirmPasswordValid.getRawValue());
	}

	@Test
	public void testIsValidPasswordLength() {
		String password = "validpassone1";
		String username = "username";
		when(getMockedDelegate().getLoginManager().getUserName()).thenReturn(username);
		getViewModel().confirmPassword.setText(password);
		getViewModel().newPassword.setText(password);
		assertTrue(getViewModel().checkIsNewPasswordValid(password));
	}

	@Test
	public void testIsNotValidPasswordLength() {
		String password = "pass";
		String username = "username";
		when(getMockedDelegate().getLoginManager().getUserName()).thenReturn(username);
		getViewModel().confirmPassword.setText(password);
		getViewModel().newPassword.setText(password);
		assertFalse(getViewModel().checkIsNewPasswordValid(password));
	}

	@Test
	public void testIsNotValidPasswordComma() {
		String password = "pass,word";
		String username = "username";
		when(getMockedDelegate().getLoginManager().getUserName()).thenReturn(username);
		getViewModel().confirmPassword.setText(password);
		getViewModel().newPassword.setText(password);
		assertFalse(getViewModel().checkIsNewPasswordValid(password));
	}

	@Test
	public void testIsNotValidPasswordSpace() {
		String password = "pass word";
		String username = "username";
		when(getMockedDelegate().getLoginManager().getUserName()).thenReturn(username);
		getViewModel().confirmPassword.setText(password);
		getViewModel().newPassword.setText(password);
		assertFalse(getViewModel().checkIsNewPasswordValid(password));
	}

	@Test
	public void testIsConfirmPasswordValid() {
		String password = "password";
		String username = "username";
		when(getMockedDelegate().getLoginManager().getUserName()).thenReturn(username);
		getViewModel().confirmPassword.setText(password);
		getViewModel().newPassword.setText(password);
		assertTrue(getViewModel().checkIsConfirmPasswordValid(password, password));
	}

	@Test
	public void testLoginManagerSetEncryptedCredentials() {
		String encryptedCredentials = "encryptedCredentials";
		when(getMockedDelegate().getLoginManager().getEncryptedCredentials()).thenReturn(encryptedCredentials);
		assertEquals(encryptedCredentials, getMockedDelegate().getLoginManager().getEncryptedCredentials());
	}

}