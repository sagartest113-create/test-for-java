package io.recruitcrm.microservice.candidate.validation;

import java.util.regex.Pattern;

/**
 * Validates and normalises phone numbers. Accepts digits with an optional
 * leading {@code '+'} after stripping common separators (spaces, dashes, parentheses).
 */
public class PhoneNumberValidator {

    private static final Pattern E164_LIKE = Pattern.compile("^\\+?[0-9]{7,15}$");

    /** @return {@code true} when the normalised number is 7-15 digits with an optional leading '+'. */
    public boolean isValid(String raw) {
        String normalized = normalize(raw);
        return normalized != null && E164_LIKE.matcher(normalized).matches();
    }

    /**
     * Strip whitespace, dashes and parentheses from a raw number.
     *
     * @return the cleaned number, or null when {@code raw} is null
     */
    public String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        return raw.replaceAll("[\\s\\-()]", "");
    }
}
