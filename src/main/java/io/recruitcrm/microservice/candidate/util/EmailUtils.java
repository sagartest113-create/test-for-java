package io.recruitcrm.microservice.candidate.util;

/** Helpers for normalising and masking email addresses. This class is not instantiable. */
public final class EmailUtils {

    private EmailUtils() {
    }

    /** Lower-case and trim the address. Returns null for null input. */
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /**
     * Extract the domain part after the {@code '@'}.
     *
     * @return the domain, or an empty string when there is no usable domain
     */
    public static String extractDomain(String email) {
        if (email == null) {
            return "";
        }
        int at = email.indexOf('@');
        return at >= 0 && at < email.length() - 1 ? email.substring(at + 1) : "";
    }

    /**
     * Mask the local part for display, keeping the first character and replacing
     * the rest with {@code '*'} — e.g. {@code "john@x.com"} becomes {@code "j***@x.com"}.
     *
     * @throws IllegalArgumentException when the address is null or has no {@code '@'}
     */
    public static String mask(String email) {
        if (email == null || email.indexOf('@') < 0) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);
        if (local.isEmpty()) {
            return "*" + domain;
        }
        StringBuilder masked = new StringBuilder().append(local.charAt(0));
        for (int i = 1; i < local.length(); i++) {
            masked.append('*');
        }
        return masked + domain;
    }
}
