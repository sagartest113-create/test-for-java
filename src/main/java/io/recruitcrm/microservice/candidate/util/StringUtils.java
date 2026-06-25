package io.recruitcrm.microservice.candidate.util;

/** Null-safe string helpers. This class is not instantiable. */
public final class StringUtils {

    private static final String ELLIPSIS = "…";

    private StringUtils() {
    }

    /** @return {@code true} when {@code value} is null or contains only whitespace. */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** @return the negation of {@link #isBlank(String)}. */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /** @return {@code value} when it is non-blank, otherwise {@code fallback}. */
    public static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    /**
     * Truncate to at most {@code maxLength} characters, appending an ellipsis when
     * truncation actually occurs.
     *
     * @param value     the input (may be null)
     * @param maxLength the maximum length of the retained prefix; must be {@code >= 0}
     * @return null when the input is null; the input unchanged when short enough;
     *         otherwise a {@code maxLength}-char prefix plus an ellipsis
     * @throws IllegalArgumentException when {@code maxLength} is negative
     */
    public static String truncate(String value, int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must not be negative");
        }
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        if (maxLength == 0) {
            return "";
        }
        return value.substring(0, maxLength) + ELLIPSIS;
    }

    /** Capitalise the first character, leaving the remainder untouched. Blank input is returned as-is. */
    public static String capitalize(String value) {
        if (isBlank(value)) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
