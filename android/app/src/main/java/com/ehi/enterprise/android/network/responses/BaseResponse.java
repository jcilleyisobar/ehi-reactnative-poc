package com.ehi.enterprise.android.network.responses;

import com.ehi.enterprise.android.models.EHIErrorMessage;
import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Class that abstracts the "messages" portion of an GBO response
 */
public class BaseResponse extends EHIModel {

	@SerializedName("messages")
	private ArrayList<EHIErrorMessage> mMessages;

	@SerializedName("errors")
	private ArrayList<EHIErrorMessage> mErrorMessages;

	/**
	 * Returns list of error object as received from server.
	 */
	public ArrayList<EHIErrorMessage> getMessages() {
		return mMessages;
	}

	/**
	 * Returns list of service error objects as received from server.
	 */
	public ArrayList<EHIErrorMessage> getErrorMessages() {
		return mErrorMessages;
	}

	/**
	 * Returns string with all error messages from array.
	 */
	public String getMessagesString() {
		StringBuilder bld = new StringBuilder();
		if (mMessages != null
				&& mMessages.size() > 0) {
			for (EHIErrorMessage message : mMessages) {
				bld.append(message.getErrorMessage());
				bld.append(" ");
			}
		}
		return bld.toString().trim();
	}

	/**
	 * Returns string with all error messages from array.
	 */
	public String getErrorMessagesString() {
		StringBuilder bld = new StringBuilder();
		if (mErrorMessages != null
				&& mErrorMessages.size() > 0) {
			for (EHIErrorMessage message : mErrorMessages) {
				bld.append(message.getErrorMessage());
				bld.append("\n\n");
			}
		}

		if (mMessages != null && !mMessages.isEmpty()) {
			for (EHIErrorMessage errorMessage : mMessages) {
				bld.append(errorMessage.getErrorMessage());
				bld.append("\n\n");
			}
		}

		return bld.toString().trim();
	}

	public String getServiceErrorCode() {
		StringBuilder bld = new StringBuilder();
		if (mErrorMessages != null
				&& mErrorMessages.size() > 0) {
			for (EHIErrorMessage message : mErrorMessages) {
				if (bld.length() > 0) {
					bld.append(",");
				}
				bld.append(message.getErrorCode());
			}
		}

		if (mMessages != null && !mMessages.isEmpty()) {
			for (EHIErrorMessage errorMessage : mMessages) {
				if (bld.length() > 0) {
					bld.append(",");
				}
				bld.append(errorMessage.getErrorCode());
			}
		}
		return bld.toString().trim();
	}

	public String getServiceErrorDisplayAs() {
		StringBuilder bld = new StringBuilder();
		if (mErrorMessages != null
				&& mErrorMessages.size() > 0) {
			for (EHIErrorMessage message : mErrorMessages) {
				if (message.getDisplayAs() != null) {
					bld.append(message.getDisplayAs());
					bld.append(" ");
				}
			}
		}
		else if (mMessages != null && !mMessages.isEmpty()) {
			for (EHIErrorMessage errorMessage : mMessages) {
				if (errorMessage.getDisplayAs() != null) {
					bld.append(errorMessage.getDisplayAs());
					bld.append(" ");
				}
			}
		}
		return bld.toString().trim();
	}
}
