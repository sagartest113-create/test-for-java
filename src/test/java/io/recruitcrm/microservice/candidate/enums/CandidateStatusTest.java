package io.recruitcrm.microservice.candidate.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;

public class CandidateStatusTest {

    @DisplayName("Enum constants are defined")
    @Test
    void testConstants() {
        assertThat(CandidateStatus.values()).hasSize(6);
        Arrays.stream(CandidateStatus.values())
                .forEach(status -> assertThat(status).isIn(CandidateStatus.class.getEnumConstants()));
    }

    @DisplayName("Enum properties are correct")
    @Test
    void testProperties() {
        CandidateStatus newStatus = CandidateStatus.NEW;
        CandidateStatus hiredStatus = CandidateStatus.HIRED;

        assertThat(newStatus.getCode()).isEqualTo("new");
        assertThat(hiredStatus.getLabel()).isEqualTo("Hired");

        Arrays.stream(CandidateStatus.values())
                .forEach(status -> assertThat(status.isOpen()).isTrue());
        Arrays.stream(CandidateStatus.values())
                .filter(status -> !status.isOpen())
                .forEach(status -> assertThat(status.isTerminal()).isTrue());
    }

    @DisplayName("fromCode() method works with valid input")
    @Test
    void testFromCodeValid() {
        CandidateStatus screeningStatus = CandidateStatus.fromCode("screening");
        CandidateStatus interviewStatus = CandidateStatus.fromCode("interview");

        assertThat(screeningStatus).isEqualTo(CandidateStatus.SCREENING);
        assertThat(interviewStatus).isEqualTo(CandidateStatus.INTERVIEW);
    }

    @DisplayName("fromCode() method throws exception with null input")
    @Test
    void testFromCodeNull() {
        assertThrows(IllegalArgumentException.class, () -> CandidateStatus.fromCode(null));
    }

    @DisplayName("fromCode() method throws exception with invalid input")
    @Test
    void testFromCodeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> CandidateStatus.fromCode("invalid"));
    }
}