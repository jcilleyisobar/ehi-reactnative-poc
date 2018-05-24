package com.ehi.enterprise.android.app;

import com.isobar.android.tokenizedstring.TokenizedString;

public enum EHIStringToken implements TokenizedString.Token {
    MILES("miles"),
    COUNT("count"),
    QUERY("query"),
    MAKE_MODEL("make_model"),
    VEHICLES("vehicles"),
    TIER("tier"),
    DATE("date"),
    TIME("time"),
    NAME("name"),
    NUMBER_OF_DAYS("number_of_days"),
    DAYS("days"),
    PRICE("price"),
    NUMBER("number"),
    UNIT("unit"),
    DURATION("duration"),
    TO_DATE("to_date"),
    NEEDED("needed"),
    ACCOUNT_NAME("account_name"),
    LOCATION_NAME("location-name"),
    PROGRESS("progress"),
    ACCOUNT("account"),
    AGE_OR_OLDER("age_or_older"),
    AGE_TO("age_to"),
    AGE_OR_YOUNGER("age_or_younger"),
    CURRENCY_CODE("currency_code"),
    POINTS("points"),
    LOCATION_COUNT("location_count"),
    PHONE_NUMBER("phone_number"),
    DIFFERENCE("difference"),
    AMOUNT("amount"),
    ABOUT("about"),
    CLOSED_ON("closed_on"),
    POLICIES("policies"),
    TERMS("terms"),
    CONTRACT_NAME("contract_name"),
    DAY("day"),
    VAT("vat_number"),
    REFUND("refund"),
    INVOICE("invoice_number"),
    RENTAL("rental_number"),
    STEP("step"),
    STEP_COUNT("step_count"),
    CHARGED("charged"),
    METHOD("method"),
    PERCENT("percent");

    private String mValue;

    EHIStringToken(String value) {
        mValue = value;
    }

    @Override
    public String getValue() {
        return mValue;
    }
}
