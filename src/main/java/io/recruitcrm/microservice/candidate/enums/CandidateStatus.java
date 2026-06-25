package io.recruitcrm.microservice.candidate.enums;

import java.util.Arrays;

/**
 * Lifecycle status of a candidate within the recruitment pipeline.
 *
 * <p>Statuses are either <em>open</em> (the candidate is still progressing) or
 * <em>terminal</em> (hired / rejected).
 */
public enum CandidateStatus {

    NEW("new", "New", true),
    SCREENING("screening", "Screening", true),
    INTERVIEW("interview", "Interview", true),
    OFFERED("offered", "Offered", true),
    HIRED("hired", "Hired", false),
    REJECTED("rejected", "Rejected", false);

    private final String code;
    private final String label;
    private final boolean open;

    CandidateStatus(String code, String label, boolean open) {
        this.code = code;
        this.label = label;
        this.open = open;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /** @return {@code true} while the candidate is still progressing through the pipeline. */
    public boolean isOpen() {
        return open;
    }

    /** @return {@code true} for terminal states (hired / rejected). */
    public boolean isTerminal() {
        return !open;
    }

    /**
     * Resolve a status from its wire code (case-insensitive, trimmed).
     *
     * @param code the wire code, e.g. {@code "screening"}
     * @return the matching status
     * @throws IllegalArgumentException if {@code code} is null or does not match any status
     */
    public static CandidateStatus fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("status code must not be null");
        }
        return Arrays.stream(values())
                .filter(status -> status.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown candidate status: " + code));
    }
}
