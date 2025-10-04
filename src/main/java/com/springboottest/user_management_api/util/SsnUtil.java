package com.springboottest.user_management_api.util;

public class SsnUtil {

    public static final int SSN_LENGTH = 16;

    /**
     * Pad SSN with leading zeros to make it 16 digits
     */
    public static String padSSN(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            return ssn;
        }

        // Remove any non-digit characters
        String cleanSsn = ssn.replaceAll("\\D", "");

        // Pad with leading zeros
        return String.format("%0" + SSN_LENGTH + "d", Long.parseLong(cleanSsn));
    }
}
