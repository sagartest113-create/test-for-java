package io.recruitcrm.microservice.candidate.service;

import io.recruitcrm.microservice.candidate.dto.CandidateRequest;
import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.exception.ResourceNotFoundException;
import io.recruitcrm.microservice.candidate.model.Candidate;
import io.recruitcrm.microservice.candidate.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.recruitcrm.microservice.candidate.enums.CandidateStatus.HIRED;
import static io.recruitcrm.microservice.candidate.enums.CandidateStatus.NEW;
import static io.recruitcrm.microservice.candidate.enums.CandidateStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private CandidateRepository repository;

    @InjectMocks
    private CandidateService service;

    @BeforeEach
    public void setup() {
        when(repository.existsByEmail(EmailUtils.normalize("test@example.com"))).thenReturn(false);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("happy path: new candidate created with valid request")
        void testCreateCandidate() {
            CandidateRequest request = new CandidateRequest(
                    "John",
                    "Doe",
                    "test@example.com",
                    "+1234567890",
                    5);

            Candidate actual = service.create(request);
            assertThat(actual.getEmail()).isEqualTo("test@example.com");
            verify(repository).save(new Candidate(null, "John", "Doe", "test@example.com", "+1234567890", 5, NEW));
        }

        @Test
        @DisplayName("negative case: email already registered")
        void testEmailAlreadyRegistered() {
            when(repository.existsByEmail(EmailUtils.normalize("test@example.com"))).thenReturn(true);

            CandidateRequest request = new CandidateRequest(
                    "John",
                    "Doe",
                    "test@example.com",
                    "+1234567890",
                    5);

            assertThrows(IllegalArgumentException.class, () -> service.create(request));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("happy path: candidate found by id")
        void testGetCandidate() {
            Candidate candidate = new Candidate(1L, "John", "Doe", "test@example.com", "+1234567890", 5, NEW);
            when(repository.findById(1L)).thenReturn(Optional.of(candidate));

            Candidate actual = service.getById(1L);
            assertThat(actual).isEqualTo(candidate);
        }

        @Test
        @DisplayName("negative case: candidate not found")
        void testCandidateNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
        }
    }

    @Nested
    @DisplayName("listAll")
    class ListAll {

        @Test
        @DisplayName("happy path: all candidates returned")
        void testListCandidates() {
            Candidate candidate = new Candidate(1L, "John", "Doe", "test@example.com", "+1234567890", 5, NEW);
            when(repository.findAll()).thenReturn(List.of(candidate));

            List<Candidate> actual = service.listAll();
            assertThat(actual).isEqualTo(List.of(candidate));
        }
    }

    @Nested
    @DisplayName("listByStatus")
    class ListByStatus {

        @Test
        @DisplayName("happy path: candidates by status returned")
        void testListCandidatesByStatus() {
            Candidate candidate1 = new Candidate(1L, "John", "Doe", "test@example.com", "+1234567890", 5, NEW);
            Candidate candidate2 = new Candidate(2L, "Jane", "Doe", "test2@example.com", "+9876543210", 10, HIRED);
            when(repository.findByStatus(HIRED)).thenReturn(List.of(candidate2));

            List<Candidate> actual = service.listByStatus(HIRED);
            assertThat(actual).isEqualTo(List.of(candidate2));
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("happy path: email exists in repository")
        void testEmailExists() {
            when(repository.existsByEmail(EmailUtils.normalize("test@example.com"))).thenReturn(true);

            boolean actual = service.existsByEmail("test@example.com");
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("negative case: email does not exist in repository")
        void testEmailDoesNotExist() {
            when(repository.existsByEmail(EmailUtils.normalize("test@example.com"))).thenReturn(false);

            boolean actual = service.existsByEmail("test@example.com");
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("happy path: candidate deleted by id")
        void testDeleteCandidate() {
            Candidate candidate = new Candidate(1L, "John", "Doe", "test@example.com", "+1234567890", 5, NEW);
            when(repository.findById(1L)).thenReturn(Optional.of(candidate));

            boolean actual = service.deleteById(1L);
            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("negative case: candidate not found")
        void testCandidateNotFound() {
            assertThrows(ResourceNotFoundException.class, () -> service.deleteById(1L));
        }
    }
}