package io.recruitcrm.microservice.candidate.controller;

import io.recruitcrm.microservice.candidate.dto.CandidateRequest;
import io.recruitcrm.microservice.candidate.enums.CandidateStatus;
import io.recruitcrm.microservice.candidate.model.Candidate;
import io.recruitcrm.microservice.candidate.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static io.recruitcrm.microservice.candidate.util.ObjectMapperUtil.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CandidateService service;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CandidateController(service)).build();
    }

    @Test
    @DisplayName("Create a candidate")
    public void createCandidate() throws Exception {
        // Given
        CandidateRequest request = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        // Then
        Candidate created = service.getById(1L);
        assertThat(created.getFirstName()).isEqualTo("John");
        assertThat(created.getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Create a candidate with invalid request")
    public void createCandidateInvalidRequest() throws Exception {
        // Given
        CandidateRequest request = new CandidateRequest(
                "John",
                null,
                "john.doe@example.com",
                "+1234567890",
                10);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());

        // Then
        Candidate created = service.getById(1L);
        assertThat(created).isNull();
    }

    @Test
    @DisplayName("Get a candidate by id")
    public void getCandidateById() throws Exception {
        // Given
        CandidateRequest request = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/api/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("Get a candidate by id with invalid id")
    public void getCandidateByIdInvalidId() throws Exception {
        // When
        mockMvc.perform(get("/api/candidates/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("List candidates")
    public void listCandidates() throws Exception {
        // Given
        CandidateRequest request1 = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);
        CandidateRequest request2 = new CandidateRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "+9876543210",
                20);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request2)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("List candidates with status filter")
    public void listCandidatesWithStatusFilter() throws Exception {
        // Given
        CandidateRequest request1 = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);
        CandidateRequest request2 = new CandidateRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "+9876543210",
                20);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request2)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/api/candidates?status=new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Update a candidate's status")
    public void updateCandidateStatus() throws Exception {
        // Given
        CandidateRequest request = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(put("/api/candidates/1/status")
                .param("value", "offered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CandidateStatus.OFFERED));
    }

    @Test
    @DisplayName("Update a candidate's status with invalid id")
    public void updateCandidateStatusInvalidId() throws Exception {
        // When
        mockMvc.perform(put("/api/candidates/99999/status")
                .param("value", "offered"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update a candidate's status with invalid new status")
    public void updateCandidateStatusInvalidNewStatus() throws Exception {
        // Given
        CandidateRequest request = new CandidateRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890",
                10);

        // When
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(put("/api/candidates/1/status")
                .param("value", "invalid"))
                .andExpect(status().isBadRequest());
    }
}