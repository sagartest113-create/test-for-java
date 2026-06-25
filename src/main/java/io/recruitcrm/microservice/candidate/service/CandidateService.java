package io.recruitcrm.microservice.candidate.service;

import io.recruitcrm.microservice.candidate.dto.CandidateRequest;
import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.exception.ResourceNotFoundException;
import io.recruitcrm.microservice.candidate.model.Candidate;
import io.recruitcrm.microservice.candidate.repository.CandidateRepository;
import io.recruitcrm.microservice.candidate.util.EmailUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/** Business operations over {@link Candidate} entities. */
@Service
public class CandidateService {

    private final CandidateRepository repository;

    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new candidate from a request, starting in {@link CandidateStatus#NEW}.
     * Emails are normalised and must be unique.
     *
     * @throws IllegalArgumentException when the email is already registered
     */
    public Candidate create(CandidateRequest request) {
        String email = EmailUtils.normalize(request.email());
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        Candidate candidate = new Candidate(
                null, request.firstName(), request.lastName(), email,
                request.phone(), request.experienceYears(), CandidateStatus.NEW);
        return repository.save(candidate);
    }

    /** @throws ResourceNotFoundException when no candidate has the given id */
    public Candidate getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Candidate", id));
    }

    public List<Candidate> listAll() {
        return repository.findAll();
    }

    public List<Candidate> listByStatus(CandidateStatus status) {
        return repository.findByStatus(status);
    }

    /**
     * Move a candidate to a new status.
     *
     * @throws ResourceNotFoundException when the candidate does not exist
     * @throws IllegalStateException     when the candidate is already in a terminal state
     */
    public Candidate updateStatus(Long id, CandidateStatus newStatus) {
        Candidate candidate = getById(id);
        if (candidate.getStatus().isTerminal()) {
            throw new IllegalStateException("Cannot change status of a terminal candidate");
        }
        candidate.setStatus(newStatus);
        return repository.save(candidate);
    }

    /** @return number of candidates still open in the pipeline */
    public long countActive() {
        return repository.findAll().stream()
                .filter(c -> c.getStatus().isOpen())
                .count();
    }
}
