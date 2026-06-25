package io.recruitcrm.microservice.candidate.controller;

import io.recruitcrm.microservice.candidate.dto.CandidateRequest;
import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.model.Candidate;
import io.recruitcrm.microservice.candidate.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST API for candidate management. */
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    /** Create a candidate; returns 201 with the created entity. */
    @PostMapping
    public ResponseEntity<Candidate> create(@Valid @RequestBody CandidateRequest request) {
        Candidate created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Fetch a candidate by id; returns 404 when absent. */
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /** List candidates, optionally filtered by status code. */
    @GetMapping
    public List<Candidate> list(@RequestParam(name = "status", required = false) String status) {
        if (status == null) {
            return service.listAll();
        }
        return service.listByStatus(CandidateStatus.fromCode(status));
    }

    /** Update a candidate's status; returns the updated entity. */
    @PutMapping("/{id}/status")
    public ResponseEntity<Candidate> updateStatus(@PathVariable Long id,
                                                  @RequestParam("value") String value) {
        return ResponseEntity.ok(service.updateStatus(id, CandidateStatus.fromCode(value)));
    }
}
