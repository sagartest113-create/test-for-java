package io.recruitcrm.microservice.candidate.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CandidateTest {

    @Test
    @DisplayName("Default constructor should create a valid candidate")
    void defaultConstructor() {
        Candidate candidate = new Candidate();
        assertNotNull(candidate);
        assertNull(candidate.getId());
        assertNull(candidate.getFirstName());
        assertNull(candidate.getLastName());
        assertNull(candidate.getEmail());
        assertNull(candidate.getPhone());
        assertEquals(0, candidate.getExperienceYears());
        assertEquals(CandidateStatus.NEW, candidate.getStatus());
    }

    @Test
    @DisplayName("Constructor with all fields should create a valid candidate")
    void constructorWithAllFields() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertNotNull(candidate);
        assertEquals(1L, candidate.getId());
        assertEquals("John", candidate.getFirstName());
        assertEquals("Doe", candidate.getLastName());
        assertEquals("john.doe@example.com", candidate.getEmail());
        assertEquals("+1234567890", candidate.getPhone());
        assertEquals(5, candidate.getExperienceYears());
        assertEquals(CandidateStatus.SCREENING, candidate.getStatus());
    }

    @Test
    @DisplayName("fullName() should return the full name of the candidate")
    void fullName() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertEquals("John Doe", candidate.fullName());
    }

    @Test
    @DisplayName("fullName() should return an empty string when first name is null")
    void fullNameFirstNameNull() {
        Candidate candidate = new Candidate(
                1L,
                null,
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertEquals("Doe", candidate.fullName());
    }

    @Test
    @DisplayName("fullName() should return an empty string when last name is null")
    void fullNameLastNameNull() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                null,
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertEquals("John", candidate.fullName());
    }

    @Test
    @DisplayName("hasMinimumExperience() should return true when experience years is greater than or equal to the given years")
    void hasMinimumExperienceTrue() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertTrue(candidate.hasMinimumExperience(3));
    }

    @Test
    @DisplayName("hasMinimumExperience() should return false when experience years is less than the given years")
    void hasMinimumExperienceFalse() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                3,
                CandidateStatus.SCREENING
        );
        assertFalse(candidate.hasMinimumExperience(5));
    }

    @Test
    @DisplayName("equals() should return true when two candidates are equal")
    void equalsEqualCandidates() {
        Candidate candidate1 = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        Candidate candidate2 = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertTrue(candidate1.equals(candidate2));
    }

    @Test
    @DisplayName("equals() should return false when two candidates are not equal")
    void equalsNotEqualCandidates() {
        Candidate candidate1 = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        Candidate candidate2 = new Candidate(
                2L,
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "+9876543210",
                10,
                CandidateStatus.INTERVIEW
        );
        assertFalse(candidate1.equals(candidate2));
    }

    @Test
    @DisplayName("equals() should return false when comparing with a null object")
    void equalsNullObject() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertFalse(candidate.equals(null));
    }

    @Test
    @DisplayName("equals() should return false when comparing with an object of a different class")
    void equalsDifferentClass() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertFalse(candidate.equals("not a candidate"));
    }

    @Test
    @DisplayName("hashCode() should return the same hash code for equal candidates")
    void hashCodeEqualCandidates() {
        Candidate candidate1 = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        Candidate candidate2 = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        assertEquals(candidate1.hashCode(), candidate2.hashCode());
    }

    @Test
    @DisplayName("toString() should return a string representation of the candidate")
    void toString() {
        Candidate candidate = new Candidate(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                5,
                CandidateStatus.SCREENING
        );
        String expectedString = "Candidate{id=1, email='john.doe@example.com', status=SCREENING}";
        assertEquals(expectedString, candidate.toString());
    }
}