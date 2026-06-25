package io.recruitcrm.microservice.candidate.validation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.regex.Pattern;

public class PhoneNumberValidatorTest {

    private final PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    @DisplayName("Validates a valid phone number")
    void testValidPhoneNumber() {
        String phoneNumber = "+1234567890";
        assertThat(validator.isValid(phoneNumber)).isTrue();
    }

    @Test
    @DisplayName("Rejects an invalid phone number with too few digits")
    void testInvalidPhoneNumberTooFewDigits() {
        String phoneNumber = "+12345";
        assertThat(validator.isValid(phoneNumber)).isFalse();
    }

    @Test
    @DisplayName("Rejects an invalid phone number with too many digits")
    void testInvalidPhoneNumberTooManyDigits() {
        String phoneNumber = "+123456789012";
        assertThat(validator.isValid(phoneNumber)).isFalse();
    }

    @Test
    @DisplayName("Rejects a phone number without leading '+'")
    void testPhoneNumberWithoutLeadingPlus() {
        String phoneNumber = "1234567890";
        assertThat(validator.isValid(phoneNumber)).isFalse();
    }

    @Test
    @DisplayName("Accepts a phone number with whitespace, dashes and parentheses")
    void testPhoneNumberWithSeparators() {
        String phoneNumber = "+1 234 567 890";
        assertThat(validator.isValid(phoneNumber)).isTrue();
    }

    @Test
    @DisplayName("Rejects null input")
    void testNullInput() {
        String phoneNumber = null;
        assertThat(validator.isValid(phoneNumber)).isFalse();
    }
}