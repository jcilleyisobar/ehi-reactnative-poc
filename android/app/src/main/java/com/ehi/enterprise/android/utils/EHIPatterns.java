package com.ehi.enterprise.android.utils;

import java.util.regex.Pattern;

public class EHIPatterns {
    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static final Pattern CREDIT_CARD_SPACES
            = Pattern.compile(
            "^\\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d{1,2}$");

    public static final Pattern CCV
            = Pattern.compile("^\\d\\d\\d{1,2}$");

    public static final Pattern HAVE_LETTERS = Pattern.compile("[a-zA-Z]");

    public static final Pattern HAVE_NUMBERS = Pattern.compile(".*\\d+.*");

    public static final Pattern ONLY_PHONE_NUMBERS = Pattern.compile("[\\d\\-*]+");

    public static final Pattern FORMATTED_PHONE_NUMBERS = Pattern.compile("^\\D*(\\d\\D*){10}");

    public static final Pattern PHONE_NUMBER = Pattern.compile("[\\d+\\-*]{12}");
}
