package com.ehi.enterprise.android.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MemberInfoFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIEmailPreference;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.PhoneFormatTextWatcher;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.LinkedList;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.widget.ReactorTextView;

@NoExtras
@ViewModel(MemberInfoViewModel.class)
public class MemberInfoFragment extends DataBindingViewModelFragment<MemberInfoViewModel, MemberInfoFragmentBinding> {

    public static final String SCREEN_NAME = "MemberInfoFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().memberInfoName) {
                showModalDialogForResult(getActivity(), new EditCallUsMemberInfoFragmentHelper.Builder().description(getString(R.string.profile_edit_member_info_non_editable_name_details)).build(), EditCallUsMemberInfoFragment.REQUEST_CODE);
            } else if (view == getViewBinding().memberInfoMemberId) {
                showModalDialogForResult(getActivity(), new EditCallUsMemberInfoFragmentHelper.Builder().description(getString(R.string.profile_edit_member_info_non_editable_member_id_details)).build(), EditCallUsMemberInfoFragment.REQUEST_CODE);
            } else if (view == getViewBinding().memberInfoAccountId) {
                showModalDialogForResult(getActivity(), new EditCallUsMemberInfoFragmentHelper.Builder().description(getString(R.string.profile_edit_member_info_non_editable_corp_account)).build(), EditCallUsMemberInfoFragment.REQUEST_CODE);
            } else if (view == getViewBinding().memberInfoMainPhoneTypeArea) {
                showPhoneTypeSelector(0);
            } else if (view == getViewBinding().memberInfoAdditionalPhoneTypeArea) {
                showPhoneTypeSelector(1);
            } else if (view == getViewBinding().memberInfoCountry) {
                showCountrySelector();
            } else if (view == getViewBinding().memberInfoSubdivision) {
                showSubdivisionSelector();
            } else if (view == getViewBinding().memberInfoSaveChanges) {
                attemptSaveChanges();
            }
        }
    };
    private boolean mIsSpecialOffersByDefault;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileCollection profileCollection = getViewModel().getProfileNoCache();
        getViewModel().setProfile(profileCollection.getProfile());
        getViewModel().setAddressProfile(profileCollection.getAddressProfile());
        getViewModel().setContactProfile(profileCollection.getContactProfile());
        if (profileCollection.getPreference() != null) {
            getViewModel().setEmailPreferences(profileCollection.getPreference().getEmailPreference());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_member_info, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MemberInfoFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_EDIT_MEMBER_INFO.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == EditCallUsMemberInfoFragment.REQUEST_CODE) {
            DialogUtils.showDialogWithTitleAndText(getActivity(), getString(R.string.location_services_generic_error), "");
        }
    }

    private void initViews() {
        getViewBinding().memberInfoName.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoMemberId.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoAccountId.setOnClickListener(mOnClickListener);

        getViewBinding().memberInfoEmailCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MemberInfoFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_EDIT_MEMBER_INFO.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EMAIL_OPT_IN.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_MY_PROFILE.value, MemberInfoFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_EDIT_MEMBER_INFO.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EMAIL_OPT_OUT.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                            .tagScreen()
                            .tagEvent();
                }
                getViewModel().getEmailPreferences().setSpecialOffers(mIsSpecialOffersByDefault, checked);
                //TODO temporary commented per CH request
//                if (getViewModel().isGerman() && checked) {
//                    getViewBinding().germanDoubleOptInText.setVisibility(View.VISIBLE);
//                } else {
                getViewBinding().germanDoubleOptInText.setVisibility(View.GONE);
//                }
            }
        });

        getViewBinding().memberInfoMainPhone.addTextChangedListener(new PhoneFormatTextWatcher() {
            @Override
            public void onPhoneNumberFormatted(String formattedNumber, String digitOnlyNumber) {
                if (!EHITextUtils.isMaskedField(getViewBinding().memberInfoMainPhone.getText().toString())) {
                    getViewModel().getContactProfile().setPhoneNumber(0, digitOnlyNumber);
                    getViewBinding().memberInfoMainPhone.setText(formattedNumber);
                    getViewBinding().memberInfoMainPhone.setSelection(formattedNumber.length());
                }
            }
        });
        getViewBinding().memberInfoMainPhoneTypeArea.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoAdditionalPhone.addTextChangedListener(new PhoneFormatTextWatcher() {
            @Override
            public void onPhoneNumberFormatted(String formattedNumber, String digitOnlyNumber) {
                if (!EHITextUtils.isMaskedField(getViewBinding().memberInfoAdditionalPhone.getText().toString())) {
                    getViewModel().getContactProfile().setPhoneNumber(1, digitOnlyNumber);
                    getViewBinding().memberInfoAdditionalPhone.setText(formattedNumber);
                    getViewBinding().memberInfoAdditionalPhone.setSelection(formattedNumber.length());
                }
            }
        });
        getViewBinding().memberInfoAdditionalPhoneTypeArea.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoCountry.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoSubdivision.setOnClickListener(mOnClickListener);
        getViewBinding().memberInfoSaveChanges.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        addReaction("BASIC_PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                FragmentUtils.removeProgressFragment(getActivity());
                EHIProfile profile = getViewModel().getProfile();
                if (profile != null) {
                    updateCorpDataFromModel(profile);
                }
            }
        });

        addReaction("LICENCE_PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIAddressProfile profile = getViewModel().getAddressProfile();
                if (profile != null) {
                    updateAddressDataFromModel(profile);
                }
            }
        });

        addReaction("CONTACT_PROFILE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIContactProfile profile = getViewModel().getContactProfile();
                if (profile != null) {
                    updateContactDataFromModel(profile);
                }
            }
        });

        addReaction("EMAIL_PREFERENCES__REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIEmailPreference profile = getViewModel().getEmailPreferences();
                if (profile != null) {
                    updatePreferencesDataFromModel(profile);
                }
            }
        });

        addReaction("ERROR_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getGeneralErrorWrapper();
                FragmentUtils.removeProgressFragment(getActivity());
                if (wrapper != null) {
                    DialogUtils.showErrorDialog(getActivity(), wrapper);
                    getViewModel().setGeneralErrorWrapper(null);
                }
            }
        });

        addReaction("SUCCESS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getGeneralSuccessWrapper() != null) {
                    FragmentUtils.removeProgressFragment(getActivity());
                    getActivity().finish();
                }
            }
        });

        addReaction("COUNTRY_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHICountry selectedCountry = getViewModel().getSelectedCountry();
                if (selectedCountry != null) {
                    getViewBinding().memberInfoCountry.setText(selectedCountry.getCountryName());
                    if (selectedCountry.getCountryCode() != null
                            && selectedCountry.getCountryCode().length() > 0) {
                        if (selectedCountry.hasSubdivisions()) {
                            getViewBinding().memberInfoSubdivisionArea.setVisibility(View.VISIBLE);
                            getViewBinding().memberInfoSubdivision.setText("");
                        } else {
                            getViewBinding().memberInfoSubdivisionArea.setVisibility(View.GONE);
                        }
                        getViewModel().requestRegionsForCountry(selectedCountry.getCountryCode());
                        FragmentUtils.addProgressFragment(getActivity());
                    } else {
                        getViewBinding().memberInfoSubdivisionArea.setVisibility(View.GONE);
                        FragmentUtils.removeProgressFragment(getActivity());
                    }
                } else {
                    FragmentUtils.addProgressFragment(getActivity());
                }
            }
        });

        addReaction("REGION_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIRegion selectedRegion = getViewModel().getSelectedRegion();
                if (selectedRegion != null) {
                    if (selectedRegion.getSubdivisionName() != null) {
                        getViewBinding().memberInfoSubdivision.setText(selectedRegion.getSubdivisionName());
                    } else {
                        getViewBinding().memberInfoSubdivision.setText("");
                    }
                    FragmentUtils.removeProgressFragment(getActivity());
                }
            }
        });

        bind(ReactorTextView.bindText(getViewModel().email, getViewBinding().memberInfoEmail));
        bind(ReactorTextView.bindText(getViewModel().street1, getViewBinding().memberInfoStreet1));
        bind(ReactorTextView.bindText(getViewModel().street2, getViewBinding().memberInfoStreet2));
        bind(ReactorTextView.bindText(getViewModel().city, getViewBinding().memberInfoCity));
        bind(ReactorTextView.bindText(getViewModel().zip, getViewBinding().memberInfoZip));
    }

    private void updatePreferencesDataFromModel(EHIEmailPreference profile) {
        mIsSpecialOffersByDefault = profile.isSpecialOffers();
        getViewBinding().memberInfoEmailCheckBox.setChecked(profile.isSpecialOffers());
    }

    private void updateContactDataFromModel(EHIContactProfile profile) {
        getViewBinding().memberInfoEmail.setText(profile.getEmail());
        getViewBinding().memberInfoMainPhone.setText(profile.getPhone(0).getMaskPhoneNumber());
        getViewBinding().memberInfoMainPhoneType.setText(profile.getPhone(0).getTypeString());
        getViewBinding().memberInfoAdditionalPhone.setText(profile.getPhone(1).getMaskPhoneNumber());
        getViewBinding().memberInfoAdditionalPhoneType.setText(profile.getPhone(1).getTypeString());
    }

    private void updateCorpDataFromModel(EHIProfile profile) {
        final EHILoyaltyData ehiLoyaltyData = profile.getBasicProfile().getLoyaltyData();

        getViewBinding().memberInfoName.setText(profile.getBasicProfile().getFullName());
        getViewBinding().memberInfoMemberId.setText(ehiLoyaltyData != null ? ehiLoyaltyData.getLoyaltyNumber() : "");

        if (profile.hasCorporateAccount()) {
            getViewBinding().memberInfoAccountId.setVisibility(View.VISIBLE);
            getViewBinding().memberInfoNoAccountArea.setVisibility(View.GONE);
            getViewBinding().memberInfoAccountId.setText(profile.getCorporateAccount().getMaskedName());
        } else {
            getViewBinding().memberInfoAccountId.setVisibility(View.GONE);
            getViewBinding().memberInfoNoAccountArea.setVisibility(View.VISIBLE);
        }
    }

    private void updateAddressDataFromModel(EHIAddressProfile profile) {
        getViewBinding().memberInfoCountry.setText(profile.getCountryName());

        if (getViewModel().hasSubdivisions(profile.getCountryCode())) {
            getViewBinding().memberInfoSubdivisionArea.setVisibility(View.VISIBLE);
            getViewBinding().memberInfoSubdivision.setText(profile.getCountrySubdivisionName());
        } else {
            getViewBinding().memberInfoSubdivisionArea.setVisibility(View.GONE);
        }

        getViewBinding().memberInfoCity.setText(profile.getCity());
        for (int i = 0; i < profile.getStreetAddresses().size(); i++) {
            if (i == 0) {
                getViewBinding().memberInfoStreet1.setText(profile.getStreetAddresses().get(0));
            } else if (i == 1) {
                getViewBinding().memberInfoStreet2.setText(profile.getStreetAddresses().get(1));
            }
        }

        getViewBinding().memberInfoZip.setText(profile.getPostal());

    }

    private void showSubdivisionSelector() {
        if (getViewModel().getSubdivisions().size() == 0) {
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);
        for (EHIRegion region : getViewModel().getSubdivisions()) {
            arrayAdapter.add(region.getSubdivisionName());
        }
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().setSelectedRegion(getViewModel().getSubdivisions().get(which));
                    }
                });
        builderSingle.show();
    }

    private void showCountrySelector() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);
        for (EHICountry country : getViewModel().getCountries()) {
            arrayAdapter.add(country.getCountryName());
        }
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().setSelectedCountry(getViewModel().getCountries().get(which));
                    }
                });
        builderSingle.show();
    }

    private void showPhoneTypeSelector(final int position) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_item);

        final List<EHIPhone> phoneTypes = new LinkedList<>();
        phoneTypes.add(new EHIPhone(EHIPhone.PhoneType.MOBILE.getValue()));
        phoneTypes.add(new EHIPhone(EHIPhone.PhoneType.HOME.getValue()));
        phoneTypes.add(new EHIPhone(EHIPhone.PhoneType.WORK.getValue()));
        phoneTypes.add(new EHIPhone(EHIPhone.PhoneType.FAX.getValue()));
        phoneTypes.add(new EHIPhone(EHIPhone.PhoneType.OTHER.getValue()));

        for (EHIPhone phoneNumber : phoneTypes) {
            arrayAdapter.add(getString(phoneNumber.getTypeString()));
        }

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getViewModel().getContactProfile().setPhoneType(position, phoneTypes.get(which).getPhoneType().getValue());
                        if (position == 0) {
                            getViewBinding().memberInfoMainPhoneType.setText(phoneTypes.get(which).getTypeString());
                        } else if (position == 1) {
                            getViewBinding().memberInfoAdditionalPhoneType.setText(phoneTypes.get(which).getTypeString());
                        }
                    }
                });

        builderSingle.show();
    }

    private void attemptSaveChanges() {
        if (getViewModel().isEmailEmpty()
                || getViewModel().isMainPhoneNumberEmpty()
                || getViewModel().isAddressEmpty()
                || getViewModel().isCityEmpty()
                || getViewModel().isZipEmpty()) {
            performVisualChecks(getViewBinding().memberInfoEmail, !getViewModel().isEmailEmpty());
            performVisualChecks(getViewBinding().memberInfoMainPhone, !getViewModel().isMainPhoneNumberEmpty());
            performVisualChecks(getViewBinding().memberInfoStreet1, !getViewModel().isStreet1Empty());
            performVisualChecks(getViewBinding().memberInfoCity, !getViewModel().isCityEmpty());
            performVisualChecks(getViewBinding().memberInfoZip, !getViewModel().isZipEmpty());
            return;
        }
        getViewModel().saveChanges();
        FragmentUtils.addProgressFragment(getActivity());
    }

    private void performVisualChecks(View view, boolean isValid) {
        view.setBackgroundResource(isValid
                ? R.drawable.edit_text_transparent_dark_border
                : R.drawable.edit_text_red_border);
    }
}
