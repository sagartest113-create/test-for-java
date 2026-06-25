package com.testcraft.demo.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.assertAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.junit.jupiter.api.function.ThrowableRunnable;
import org.junit.jupiter.api.function.ThrowableSupplier;
import org.junit.jupiter.api.function.ThrowableConsumer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

@ExtendWith({org.junit.jupiter.api.extension.RegisterExtension.class})
public class BinarySearchResponseTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("Constructors")
    public class Constructors {

        @Test
        @DisplayName("should create a new BinarySearchResponse with all fields")
        void shouldCreateBinarySearchResponse() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.searchId()).isEqualTo(1L);
            Assertions.assertThat(response.target()).isEqualTo(5);
            Assertions.assertThat(response.found()).isEqualTo(true);
            Assertions.assertThat(response.index()).isEqualTo(2);
            Assertions.assertThat(response.comparisons()).isEqualTo(3);
            Assertions.assertThat(response.steps()).isEqualTo(List.of("step1", "step2"));
        }

        @Test
        @DisplayName("should throw NPE when searchId is null")
        void shouldThrowNPEWhenSearchIdIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(null, 5, true, 2, 3, List.of("step1", "step2")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NPE when target is null")
        void shouldThrowNPEWhenTargetIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(1L, null, true, 2, 3, List.of("step1", "step2")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NPE when found is null")
        void shouldThrowNPEWhenFoundIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(1L, 5, null, 2, 3, List.of("step1", "step2")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NPE when index is null")
        void shouldThrowNPEWhenIndexIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(1L, 5, true, null, 3, List.of("step1", "step2")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NPE when comparisons is null")
        void shouldThrowNPEWhenComparisonsIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(1L, 5, true, 2, null, List.of("step1", "step2")))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should throw NPE when steps is null")
        void shouldThrowNPEWhenStepsIsNull() {
            Assertions.assertThatThrownBy(() -> new BinarySearchResponse(1L, 5, true, 2, 3, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Accessor methods")
    public class AccessorMethods {

        @Test
        @DisplayName("should return the correct searchId")
        void shouldReturnSearchId() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.searchId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return the correct target")
        void shouldReturnTarget() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.target()).isEqualTo(5);
        }

        @Test
        @DisplayName("should return the correct found")
        void shouldReturnFound() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.found()).isEqualTo(true);
        }

        @Test
        @DisplayName("should return the correct index")
        void shouldReturnIndex() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.index()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return the correct comparisons")
        void shouldReturnComparisons() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.comparisons()).isEqualTo(3);
        }

        @Test
        @DisplayName("should return the correct steps")
        void shouldReturnSteps() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.steps()).isEqualTo(List.of("step1", "step2"));
        }
    }

    @Nested
    @DisplayName("Equals and hashCode")
    public class EqualsAndHashCode {

        @Test
        @DisplayName("should return true when two BinarySearchResponse objects are equal")
        void shouldReturnTrueWhenTwoObjectsAreEqual() {
            BinarySearchResponse response1 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            BinarySearchResponse response2 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response1).isEqualTo(response2);
        }

        @Test
        @DisplayName("should return false when two BinarySearchResponse objects are not equal")
        void shouldReturnFalseWhenTwoObjectsAreNotEqual() {
            BinarySearchResponse response1 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            BinarySearchResponse response2 = new BinarySearchResponse(1L, 6, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("should return true when two BinarySearchResponse objects are equal in hashCode")
        void shouldReturnTrueWhenTwoObjectsAreEqualInHashCode() {
            BinarySearchResponse response1 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            BinarySearchResponse response2 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("should return false when two BinarySearchResponse objects are not equal in hashCode")
        void shouldReturnFalseWhenTwoObjectsAreNotEqualInHashCode() {
            BinarySearchResponse response1 = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            BinarySearchResponse response2 = new BinarySearchResponse(1L, 6, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString")
    public class ToString {

        @Test
        @DisplayName("should return the correct string representation")
        void shouldReturnCorrectStringRepresentation() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Assertions.assertThat(response.toString()).isEqualTo("BinarySearchResponse[searchId=1, target=5, found=true, index=2, comparisons=3, steps=[step1, step2]]");
        }
    }

    @Nested
    @DisplayName("Validation")
    public class Validation {

        @Test
        @DisplayName("should not throw any constraint violations when all fields are valid")
        void shouldNotThrowAnyConstraintViolationsWhenAllFieldsAreValid() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isEmpty();
        }

        @Test
        @DisplayName("should throw a constraint violation when searchId is null")
        void shouldThrowConstraintViolationWhenSearchIdIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(null, 5, true, 2, 3, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("searchId");
        }

        @Test
        @DisplayName("should throw a constraint violation when target is null")
        void shouldThrowConstraintViolationWhenTargetIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(1L, null, true, 2, 3, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("target");
        }

        @Test
        @DisplayName("should throw a constraint violation when found is null")
        void shouldThrowConstraintViolationWhenFoundIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, null, 2, 3, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("found");
        }

        @Test
        @DisplayName("should throw a constraint violation when index is null")
        void shouldThrowConstraintViolationWhenIndexIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, null, 3, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("index");
        }

        @Test
        @DisplayName("should throw a constraint violation when comparisons is null")
        void shouldThrowConstraintViolationWhenComparisonsIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, null, List.of("step1", "step2"));
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("comparisons");
        }

        @Test
        @DisplayName("should throw a constraint violation when steps is null")
        void shouldThrowConstraintViolationWhenStepsIsNull() {
            BinarySearchResponse response = new BinarySearchResponse(1L, 5, true, 2, 3, null);
            Set<ConstraintViolation<BinarySearchResponse>> constraintViolations = validator.validate(response);
            Assertions.assertThat(constraintViolations).isNotEmpty();
            Assertions.assertThat(constraintViolations).extracting("propertyPath").contains("steps");
        }
    }
}