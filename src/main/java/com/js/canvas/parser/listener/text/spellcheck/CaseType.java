package com.js.canvas.parser.listener.text.spellcheck;

public enum CaseType {

    ALL_LOWERCASE,
    INITIAL_UPPERCASE_THEN_LOWERCASE,
    ALL_UPPERCASE,
    MIXED;

    public static CaseType getCaseType(String text) {
        if (text.toLowerCase().equals(text))
            return ALL_LOWERCASE;
        if ((text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase()).equals(text))
            return INITIAL_UPPERCASE_THEN_LOWERCASE;
        if (text.toUpperCase().equals(text))
            return ALL_UPPERCASE;
        return MIXED;
    }

    public static String forceCase(String text, CaseType caseType) {
        if (caseType == MIXED)
            throw new IllegalArgumentException();
        if (caseType == ALL_LOWERCASE)
            return text.toLowerCase();
        if (caseType == INITIAL_UPPERCASE_THEN_LOWERCASE)
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        if (caseType == ALL_UPPERCASE)
            return text.toUpperCase();
        return text;
    }

}
