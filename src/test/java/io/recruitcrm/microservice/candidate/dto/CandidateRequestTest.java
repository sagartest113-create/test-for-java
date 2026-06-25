package io.recruitcrm.microservice.candidate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CandidateRequestTest {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private final Validator validator = VALIDATOR_FACTORY.getValidator();

    @Test
    @DisplayName("Happy path: all fields present")
    void testCandidateRequest_HappyPath() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10
        );
        Assertions.assertThat(candidateRequest.firstName()).isEqualTo("John");
        Assertions.assertThat(candidateRequest.lastName()).isEqualTo("Doe");
        Assertions.assertThat(candidateRequest.email()).isEqualTo("john.doe@example.com");
        Assertions.assertThat(candidateRequest.phone()).isEqualTo("+1234567890");
        Assertions.assertThat(candidateRequest.experienceYears()).isEqualTo(10);
    }

    @Test
    @DisplayName("Negative case: empty first name")
    void testCandidateRequest_EmptyFirstName() {
        var candidateRequest = new CandidateRequest(
                "",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("First name is required");
    }

    @Test
    @DisplayName("Negative case: empty last name")
    void testCandidateRequest_EmptyLastName() {
        var candidateRequest = new CandidateRequest(
                "John",
                "",
                "john.doe@example.com",
                "+1234567890",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Last name is required");
    }

    @Test
    @DisplayName("Negative case: empty email")
    void testCandidateRequest_EmptyEmail() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "",
                "+1234567890",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Email is required");
    }

    @Test
    @DisplayName("Negative case: invalid email")
    void testCandidateRequest_InvalidEmail() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "invalid_email",
                "+1234567890",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Email must be valid");
    }

    @Test
    @DisplayName("Negative case: empty phone")
    void testCandidateRequest_EmptyPhone() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(0);
    }

    @Test
    @DisplayName("Negative case: invalid phone")
    void testCandidateRequest_InvalidPhone() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "12345678901234567890",
                10
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Phone must be 7-15 digits with an optional leading +");
    }

    @Test
    @DisplayName("Negative case: negative experience years")
    void testCandidateRequest_NegativeExperienceYears() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                -1
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Experience cannot be negative");
    }

    @Test
    @DisplayName("Negative case: experience years too high")
    void testCandidateRequest_ExperienceYearsTooHigh() {
        var candidateRequest = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                61
        );
        Set<ConstraintViolation<CandidateRequest>> violations = validator.validate(candidateRequest);
        Assertions.assertThat(violations).hasSize(1);
        ConstraintViolation<CandidateRequest> violation = violations.iterator().next();
        Assertions.assertThat(violation.getMessage()).isEqualTo("Experience years is unrealistic");
    }
}