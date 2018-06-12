package com.ehi.enterprise.android.utils;

public class EHIPasswordValidator {

    private static String[] POSSIBLE_PASSWORDS = new String[]{
            "mot de passe",
            "motdepasse",
            "contraseña",
            "kennwort",
            "passwort",
            "password",
            "senha",
            "palavra-passe",
            "palavrapasse",
            "wachtwoord"
    };

    private boolean hasMinimumLength = false;
    private boolean hasLetter = false;
    private boolean hasNumber = false;
    private boolean hasPassword = false;

    public EHIPasswordValidator(String password, int minimumLength) {
        if (password == null) {
            return;
        }

        hasMinimumLength = password.length() >= minimumLength;
        hasLetter = EHIPatterns.HAVE_LETTERS.matcher(password).find();
        hasNumber = EHIPatterns.HAVE_NUMBERS.matcher(password).find();

        final String lowerCasedPassword = password.toLowerCase();
        for (String wordCheck : POSSIBLE_PASSWORDS) {
            hasPassword |= lowerCasedPassword.contains(wordCheck);
        }
    }

    public boolean hasMinimumLength() {
        return hasMinimumLength;
    }

    public boolean hasLetter() {
        return hasLetter;
    }

    public boolean hasNumber() {
        return hasNumber;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public boolean isValid() {
        return hasMinimumLength && hasLetter && hasNumber && !hasPassword;
    }
}
