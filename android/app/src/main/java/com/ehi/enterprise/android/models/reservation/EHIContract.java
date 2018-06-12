package com.ehi.enterprise.android.models.reservation;

import android.text.TextUtils;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EHIContract extends EHIModel {

    public static final String CONTRACT_TYPE_CORPORATE = "CORPORATE";
    public static final String CONTRACT_TYPE_PROMOTION = "PROMOTION";

    @SerializedName("contract_description")
    private String mContractDescription;

    @SerializedName("contract_number")
    private String mContractNumber;

    @SerializedName("contract_type")
    private String mContractType;

    @SerializedName("contract_name")
    private String mContractName;

    @SerializedName("contract_accepts_billing")
    private boolean mContractAcceptsBilling;

    @SerializedName("terms_and_conditions")
    private String mTermsAndConditions;

    @SerializedName("additional_information")
    private List<EHIAdditionalInformation> mAdditionalInformation;

    @SerializedName("mob_description")
    private List<String> mDescription;

    @SerializedName("mob_descriptions") //fallback to address EA-5717
    private List<String> mDescriptions;

    @SerializedName("mob_short_description")
    private List<String> mShortDescription;

    @SerializedName("contract_billing_account")
    private String mBillingAccount;

    @SerializedName("third_party_email_notify")
    private boolean mThirdPartyEmailNotify;

    @SerializedName("marketing_message_indicator")
    private boolean mMarketingMessageIndicator;

    @SerializedName("contract_has_additional_benefits")
    private boolean mContractHasAdditionalBenefits;

    public static EHIContract manualCid(String cid) {
        EHIContract acc = new EHIContract();
        acc.setContractNumber(cid);
        acc.setContractName(cid);
        return acc;
    }

    public String getContractDescription() {
        return mContractDescription;
    }

    public String getContractNumber() {
        return mContractNumber;
    }

    public void setContractNumber(String contractNumber) {
        mContractNumber = contractNumber;
    }

    public boolean isContractAcceptsBilling() {
        return mContractAcceptsBilling;
    }

    public String getContractType() {
        return mContractType;
    }

    public String getContractName() {
        return mContractName;
    }

    public void setContractName(String contractName) {
        mContractName = contractName;
    }

    public List<EHIAdditionalInformation> getAllAdditionalInformation() {
        Collections.sort(mAdditionalInformation, new Comparator<EHIAdditionalInformation>() {
            @Override
            public int compare(EHIAdditionalInformation t1, EHIAdditionalInformation t2) {
                return t1.getSequence().compareTo(t2.getSequence());
            }
        });
        return mAdditionalInformation;
    }

    public List<EHIAdditionalInformation> getPreRateAdditionalInformation() {
        LinkedList<EHIAdditionalInformation> preRateInfoList = new LinkedList<>();
        if (mAdditionalInformation != null
                && mAdditionalInformation.size() > 0) {
            for (EHIAdditionalInformation info : mAdditionalInformation) {
                if (info.isPreRateInfo()) {
                    preRateInfoList.add(info);
                }
            }
        }
        return preRateInfoList;
    }

    public String getBillingAccount() {
        return mBillingAccount;
    }

    public String getContractOrBillingName() {
        return getContractName() != null ? getContractName() : (getBillingAccount() != null ? getBillingAccount() : "");
    }

    public void setAdditionalInformation(List<EHIAdditionalInformation> additionalInformation) {
        mAdditionalInformation = additionalInformation;
    }

    public String getMaskedName() {
        StringBuilder bld = new StringBuilder();
        bld.append(getContractName());
        bld.append(" (");
        String contractNumber = getContractNumber();
        if (contractNumber != null && contractNumber.length() > 4) {
            bld.append(TextUtils.join("", Collections.nCopies(contractNumber.length() - 4, "*")));
            bld.append(contractNumber.substring(contractNumber.length() - 4, contractNumber.length()));
        } else {
            bld.append(TextUtils.join("", Collections.nCopies(4, "*")));
        }
        bld.append(")");
        return bld.toString();
    }

    public List<String> getDescription() {
        if (mDescription == null){
            return mDescriptions;
        }
        return mDescription;
    }

    public List<String> getShortDescription() {
        return mShortDescription;
    }

    public String getTermsAndConditions() {
        return mTermsAndConditions;
    }

    public boolean is3rdPartyEmailNotify() {
        return mThirdPartyEmailNotify;
    }

    public boolean hideMarketingMessageOptIn() {
        return !mMarketingMessageIndicator;
    }

    public boolean contractHasAdditionalBenefits() {
        return mContractHasAdditionalBenefits;
    }

    public boolean isCorporateContract() {
        return CONTRACT_TYPE_CORPORATE.equalsIgnoreCase(mContractType);
    }
}