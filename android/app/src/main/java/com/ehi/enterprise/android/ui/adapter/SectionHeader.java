package com.ehi.enterprise.android.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;

public class SectionHeader {
	int mFirstPosition;
	int mSectionedPosition;
	private String mTitle;
	private String mSecondaryTitle;
	private String mButtonText;
	private View.OnClickListener mOnClickListener;
	private boolean mShouldUseButton = false;
	private boolean mShowTriangle = false;

    public SectionHeader() {
		mTitle = "";
		mSecondaryTitle = "";
		mButtonText = "";
		mOnClickListener = null;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getSecondaryTitle() {
		return mSecondaryTitle;
	}

	public String getButtonText() {
		return mButtonText;
	}

	public View.OnClickListener getOnClickListener() {
		return mOnClickListener;
	}

	public boolean shouldUseButton() {
		return mShouldUseButton;
	}

	public void setButton(String buttonText, View.OnClickListener onClickListener) {
		setShouldUseButton(true);
		setButtonText(buttonText);
		setOnClickListener(onClickListener);
	}

	private void setButtonText(String buttonText) {
		mButtonText = buttonText;
	}

	private void setOnClickListener(View.OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
	}

	public void setShouldUseButton(boolean shouldUseButton) {
		mShouldUseButton = shouldUseButton;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setSecondaryTitle(String secondaryTitle) {
		mSecondaryTitle = secondaryTitle;
	}

	public void setPosition(int position) {
		mFirstPosition = position;
	}

	public void setShowTriangle(boolean showTriangle) {
		mShowTriangle = showTriangle;
	}

	public boolean shouldShowTriangle() {
		return mShowTriangle;
	}

	public static class Builder {
		private SectionHeader mSectionHeader;
		private static Builder sBuilder;

		/**
		 * Begin building a new section header
		 */
		private Builder() {
			mSectionHeader = new SectionHeader();
		}

		/**
		 * Use this to specify where the section header should go in the adapter
		 * This is a required method
		 *
		 * @param position Position to display the header at
		 * @return Returns the updated builder object
		 */
		public static SectionHeader.Builder atPosition(int position) {
			sBuilder = new Builder();
			sBuilder.mSectionHeader.setPosition(position);
			return sBuilder;
		}

		/**
		 * Use this to set the title on the header.
		 * If this is not used, the header will not display with a title
		 *
		 * @param message Title to display on the header
		 * @return Returns the updated builder object
		 */
		public SectionHeader.Builder setTitle(@NonNull String message) {
			sBuilder.mSectionHeader.setTitle(message);
			return sBuilder;
		}

		public SectionHeader.Builder setSecondaryTitle(@NonNull String message) {
			sBuilder.mSectionHeader.setSecondaryTitle(message);
			return sBuilder;
		}

		/**
		 * Use this to enable an actionable button on the right side of the header.
		 * The OnClickListener should not be null
		 *
		 * @param text            Text to display on the button
		 * @param onClickListener callback action on button press
		 * @return Returns the updated builder object
		 */
		public SectionHeader.Builder withButton(@NonNull String text, @NonNull View.OnClickListener onClickListener) {
			sBuilder.mSectionHeader.setButton(text, onClickListener);
			return sBuilder;
		}

		/**
		 * Build the section header for use
		 *
		 * @return Returns the section header populated with properties during building
		 */
		public SectionHeader build() {
			return sBuilder.mSectionHeader;
		}

		public SectionHeader.Builder showTriangle(boolean show) {
			sBuilder.mSectionHeader.setShowTriangle(show);
			return sBuilder;
		}
	}
}
