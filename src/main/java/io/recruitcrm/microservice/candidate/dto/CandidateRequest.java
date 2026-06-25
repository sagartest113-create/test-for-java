package io.recruitcrm.microservice.candidate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Inbound payload for creating a candidate. Bean-Validation constraints are
 * enforced at the controller boundary via {@code @Valid}.
 *
 * @param firstName       required, max 50 chars
 * @param lastName        required, max 50 chars
 * @param email           required, must be a valid email
 * @param phone           optional; when present, 7-15 digits with optional leading '+'
 * @param experienceYears years of experience, between 0 and 60
 */
public record CandidateRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must be 7-15 digits with an optional leading +")
        String phone,

        @Min(value = 0, message = "Experience cannot be negative")
        @Max(value = 60, message = "Experience years is unrealistic")
        int experienceYears
) {
}
