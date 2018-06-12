package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorRateButtonState extends ReactorViewState {
    ReactorVar<CharSequence> mPrimaryText;
    private ReactorPropertyChangedListener<CharSequence> mPrimaryTextChangedListener;
    ReactorVar<CharSequence> mSecondaryText;
    private ReactorPropertyChangedListener<CharSequence> mSecondaryTextChangedListener;
    ReactorVar<CharSequence> mPrice;
    private ReactorPropertyChangedListener<CharSequence> mPriceChangedListener;
    ReactorVar<CharSequence> mPriceSubtitle;
    private ReactorPropertyChangedListener<CharSequence> mPriceSubtitleChangedListener;
    ReactorVar<CharSequence> mSavingPrice;
    private ReactorPropertyChangedListener<CharSequence> mSavingPriceChangedListener;
    ReactorVar<Boolean> mSavingPriceVisibility;
    private ReactorPropertyChangedListener<Boolean> mSavingPriceVisibilityChangedListener;
    ReactorVar<Boolean> mArrow;
    private ReactorPropertyChangedListener<Boolean> mArrowChangedListener;
    ReactorVar<Boolean> mShouldShowWarningIcon;
    private ReactorPropertyChangedListener<Boolean> mShouldShowWarningIconListener;


    public void setPrimaryTextChangedListener(final ReactorPropertyChangedListener<CharSequence> primaryTextChangedListener) {
        mPrimaryTextChangedListener = primaryTextChangedListener;
    }

    public ReactorVar<CharSequence> primaryText() {
        if (mPrimaryText == null) {
            mPrimaryText = new ReactorVar<CharSequence>() {
                @Override
                public void setValue(final CharSequence value) {
                    super.setValue(value);
                    if (mPrimaryTextChangedListener != null) {
                        mPrimaryTextChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mPrimaryText;
    }

    public void setPrimaryText(CharSequence primaryText) {
        primaryText().setValue(primaryText);
    }

    public void setSecondaryTextChangedListener(final ReactorPropertyChangedListener<CharSequence> secondaryTextChangedListener) {
        mSecondaryTextChangedListener = secondaryTextChangedListener;
    }

    public ReactorVar<CharSequence> secondaryText() {
        if (mSecondaryText == null) {
            mSecondaryText = new ReactorVar<CharSequence>() {
                @Override
                public void setValue(final CharSequence value) {
                    super.setValue(value);
                    if (mSecondaryTextChangedListener != null) {
                        mSecondaryTextChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mSecondaryText;
    }

    public void setSecondaryText(CharSequence secondaryText) {
        secondaryText().setValue(secondaryText);
    }

    public void setPriceChangedListener(final ReactorPropertyChangedListener<CharSequence> priceChangedListener) {
        mPriceChangedListener = priceChangedListener;
    }

    public ReactorVar<CharSequence> price() {
        if (mPrice == null) {
            mPrice = new ReactorVar<CharSequence>() {
                @Override
                public void setValue(final CharSequence value) {
                    super.setValue(value);
                    if (mPriceChangedListener != null) {
                        mPriceChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mPrice;
    }

    public void setPrice(final CharSequence price) {
        price().setValue(price);
    }

    public ReactorVar<Boolean> arrow() {
        if (mArrow == null) {
            mArrow = new ReactorVar<Boolean>() {
                @Override
                public void setValue(final Boolean value) {
                    super.setValue(value);
                    if (mArrowChangedListener != null) {
                        mArrowChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mArrow;
    }

    public void setArrow(boolean arrow) {
        arrow().setValue(arrow);
    }

    public void setArrowChangedListener(final ReactorPropertyChangedListener<Boolean> arrowChangedListener) {
        mArrowChangedListener = arrowChangedListener;
    }

    public ReactorVar<Boolean> savingPriceVisibility() {
        if (mSavingPriceVisibility == null) {
            mSavingPriceVisibility = new ReactorVar<Boolean>() {
                @Override
                public void setValue(final Boolean value) {
                    super.setValue(value);
                    if (mSavingPriceVisibilityChangedListener != null) {
                        mSavingPriceVisibilityChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mSavingPriceVisibility;
    }

    public void setSavingPriceVisibility(boolean visibility) {
        savingPriceVisibility().setValue(visibility);
    }

    public void setSavingPriceVisibilityChangedListener(final ReactorPropertyChangedListener<Boolean> savingPriceVisibilityChangedListener) {
        mSavingPriceVisibilityChangedListener = savingPriceVisibilityChangedListener;
    }

    public void setPriceSubtitleChangedListener(final ReactorPropertyChangedListener<CharSequence> priceSubtitleChangedListener) {
        mPriceSubtitleChangedListener = priceSubtitleChangedListener;
    }

    public ReactorVar<CharSequence> priceSubtitle() {
        if (mPriceSubtitle == null) {
            mPriceSubtitle = new ReactorVar<CharSequence>() {
                @Override
                public void setValue(final CharSequence value) {
                    super.setValue(value);
                    if (mPriceSubtitleChangedListener != null) {
                        mPriceSubtitleChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mPriceSubtitle;
    }

    public void setPriceSubtitle(final CharSequence priceSubtitle) {
        priceSubtitle().setValue(priceSubtitle);
    }

    public ReactorVar<CharSequence> savingPrice() {
        if (mSavingPrice == null) {
            mSavingPrice = new ReactorVar<CharSequence>() {
                @Override
                public void setValue(final CharSequence value) {
                    super.setValue(value);
                    if (mSavingPriceChangedListener != null) {
                        mSavingPriceChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mSavingPrice;
    }

    public void setSavingPrice(final CharSequence savingPrice) {
        savingPrice().setValue(savingPrice);
    }

    public void setSavingPriceChangedListener(final ReactorPropertyChangedListener<CharSequence> savingPriceChangedListener) {
        mSavingPriceChangedListener = savingPriceChangedListener;
    }

    public void setShouldShowWarningIconListener(final ReactorPropertyChangedListener<Boolean> shouldShowWarningIconListener) {
        mShouldShowWarningIconListener = shouldShowWarningIconListener;
    }

    public void setShouldShowWarningIcon(final Boolean shouldShowWarningIcon) {
        warningIcon().setValue(shouldShowWarningIcon);
    }

    public ReactorVar<Boolean> warningIcon() {
        if (mShouldShowWarningIcon == null) {
            mShouldShowWarningIcon = new ReactorVar<Boolean>() {
                @Override
                public void setValue(final Boolean value) {
                    super.setValue(value);
                    if (mShouldShowWarningIconListener != null) {
                        mShouldShowWarningIconListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mShouldShowWarningIcon;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorRateButtonState$$Unbinder.unbind(this);
        mPrimaryTextChangedListener = null;
        mSecondaryTextChangedListener = null;
        mPriceSubtitleChangedListener = null;
        mPriceChangedListener = null;
        mSavingPriceChangedListener = null;
        mArrowChangedListener = null;
        mShouldShowWarningIconListener = null;
    }
}
