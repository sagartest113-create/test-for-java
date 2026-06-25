package io.recruitcrm.microservice.candidate.repository;

import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.model.Candidate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Simple in-memory store for {@link Candidate} entities. */
@Repository
public class CandidateRepository {

    private final List<Candidate> store = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    /**
     * Persist a candidate. New candidates (null id) are assigned a generated id;
     * existing ones are replaced in place.
     *
     * @return the stored candidate (with id populated)
     */
    public Candidate save(Candidate candidate) {
        if (candidate.getId() == null) {
            candidate.setId(sequence.getAndIncrement());
        } else {
            deleteById(candidate.getId());
        }
        store.add(candidate);
        return candidate;
    }

    public Optional<Candidate> findById(Long id) {
        return store.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    /** @return a defensive copy of all stored candidates. */
    public List<Candidate> findAll() {
        return new ArrayList<>(store);
    }

    public List<Candidate> findByStatus(CandidateStatus status) {
        return store.stream().filter(c -> c.getStatus() == status).toList();
    }

    public boolean existsByEmail(String email) {
        return store.stream().anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email));
    }

    /** @return {@code true} when a candidate was removed. */
    public boolean deleteById(Long id) {
        return store.removeIf(c -> c.getId().equals(id));
    }

    public long count() {
        return store.size();
    }
}
