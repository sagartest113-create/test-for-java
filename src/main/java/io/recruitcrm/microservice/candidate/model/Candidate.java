package io.recruitcrm.microservice.candidate.model;

import io.recruitcrm.microservice.candidate.enums.CandidateStatus;

import java.util.Objects;

/** Domain model representing a candidate in the recruitment pipeline. */
public class Candidate {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int experienceYears;
    private CandidateStatus status = CandidateStatus.NEW;

    public Candidate() {
    }

    public Candidate(Long id, String firstName, String lastName, String email,
                     String phone, int experienceYears, CandidateStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.experienceYears = experienceYears;
        this.status = status;
    }

    /** @return {@code "First Last"}, trimming any missing part gracefully. */
    public String fullName() {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        return (first + " " + last).trim();
    }

    /** @return {@code true} when the candidate has at least {@code years} of experience. */
    public boolean hasMinimumExperience(int years) {
        return experienceYears >= years;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate that = (Candidate) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Candidate{id=" + id + ", email='" + email + "', status=" + status + "}";
    }
}
