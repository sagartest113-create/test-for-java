package io.recruitcrm.microservice.candidate.repository;

import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.model.Candidate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CandidateRepositoryTest {

    private CandidateRepository repository;

    @BeforeEach
    public void setup() {
        repository = new CandidateRepository();
    }

    @Test
    @DisplayName("Save a candidate with generated id")
    public void testSaveGeneratedId() {
        // Given
        Candidate candidate = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);

        // When
        Candidate savedCandidate = repository.save(candidate);

        // Then
        assertThat(savedCandidate.getId()).isNotNull();
    }

    @Test
    @DisplayName("Save a candidate with existing id")
    public void testSaveExistingId() {
        // Given
        Candidate candidate = new Candidate(1L, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate);

        // When
        Candidate savedCandidate = repository.save(candidate);

        // Then
        assertThat(savedCandidate.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Find a candidate by id")
    public void testFindById() {
        // Given
        Candidate candidate = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate);

        // When
        Optional<Candidate> foundCandidate = repository.findById(1L);

        // Then
        assertThat(foundCandidate).isPresent();
    }

    @Test
    @DisplayName("Find a candidate by id (not found)")
    public void testFindByIdNotFound() {
        // Given

        // When
        Optional<Candidate> foundCandidate = repository.findById(1L);

        // Then
        assertThat(foundCandidate).isEmpty();
    }

    @Test
    @DisplayName("Find all candidates")
    public void testFindAll() {
        // Given
        Candidate candidate1 = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        Candidate candidate2 = new Candidate(null, "Jane", "Doe", "jane.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate1);
        repository.save(candidate2);

        // When
        List<Candidate> allCandidates = repository.findAll();

        // Then
        assertThat(allCandidates).hasSize(2);
    }

    @Test
    @DisplayName("Find candidates by status")
    public void testFindByStatus() {
        // Given
        Candidate candidate1 = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        Candidate candidate2 = new Candidate(null, "Jane", "Doe", "jane.doe@example.com", null, 5, CandidateStatus.HIRED);
        repository.save(candidate1);
        repository.save(candidate2);

        // When
        List<Candidate> candidatesByStatus = repository.findByStatus(CandidateStatus.NEW);

        // Then
        assertThat(candidatesByStatus).hasSize(1);
    }

    @Test
    @DisplayName("Check if a candidate exists by email")
    public void testExistsByEmail() {
        // Given
        Candidate candidate = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate);

        // When
        boolean exists = repository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Check if a candidate does not exist by email")
    public void testExistsByEmailNotFound() {
        // Given

        // When
        boolean exists = repository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Delete a candidate by id")
    public void testDeleteById() {
        // Given
        Candidate candidate = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate);

        // When
        boolean deleted = repository.deleteById(1L);

        // Then
        assertThat(deleted).isTrue();
    }

    @Test
    @DisplayName("Delete a candidate by id (not found)")
    public void testDeleteByIdNotFound() {
        // Given

        // When
        boolean deleted = repository.deleteById(1L);

        // Then
        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("Count all candidates")
    public void testCount() {
        // Given
        Candidate candidate1 = new Candidate(null, "John", "Doe", "john.doe@example.com", null, 5, CandidateStatus.NEW);
        Candidate candidate2 = new Candidate(null, "Jane", "Doe", "jane.doe@example.com", null, 5, CandidateStatus.NEW);
        repository.save(candidate1);
        repository.save(candidate2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}