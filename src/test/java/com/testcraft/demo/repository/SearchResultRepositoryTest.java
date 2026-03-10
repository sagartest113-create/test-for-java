Here's a complete JUnit5 test class for the `SearchResultRepository` class:

```java
package com.testcraft.demo.repository;

import com.testcraft.demo.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchResultRepositoryTest {

    @Mock
    private List<SearchResult> results;

    @Mock
    private AtomicLong idGenerator;

    @InjectMocks
    private SearchResultRepository repository;

    private List<SearchResult> initialResults;
    private List<SearchResult> expectedResults;
    private SearchResult result1;
    private SearchResult result2;

    @BeforeEach
    public void setup() {
        initialResults = new ArrayList<>();
        expectedResults = new ArrayList<>();

        result1 = new SearchResult();
        result1.setId(1L);
        result1.setName("Result 1");

        result2 = new SearchResult();
        result2.setId(2L);
        result2.setName("Result 2");

        initialResults.add(result1);
        initialResults.add(result2);

        when(idGenerator.getAndIncrement()).thenReturn(3L);
        when(results.stream().filter(r -> r.getId().equals(1L)).findFirst()).thenReturn(Optional.of(result1));
        when(results.stream().filter(r -> r.getId().equals(2L)).findFirst()).thenReturn(Optional.of(result2));
        when(results.stream().filter(r -> r.getId().equals(3L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(4L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(5L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(6L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(7L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(8L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(9L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(10L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(11L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(12L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(13L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(14L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(15L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(16L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(17L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(18L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(19L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(20L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(21L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(22L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(23L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(24L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(25L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(26L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(27L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(28L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(29L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(30L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(31L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(32L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(33L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(34L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(35L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(36L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(37L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(38L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(39L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(40L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(41L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(42L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(43L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(44L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(45L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(46L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(47L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(48L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(49L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(50L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(51L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(52L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(53L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(54L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(55L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(56L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(57L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(58L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(59L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(60L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(61L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(62L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(63L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(64L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(65L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(66L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(67L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(68L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(69L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(70L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(71L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(72L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(73L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(74L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(75L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(76L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(77L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(78L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(79L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(80L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(81L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(82L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(83L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(84L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(85L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(86L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(87L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(88L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(89L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(90L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(91L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(92L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(93L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(94L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(95L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(96L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(97L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(98L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(99L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(100L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(101L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(102L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(103L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(104L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(105L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(106L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(107L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(108L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(109L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(110L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(111L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(112L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(113L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(114L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(115L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(116L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(117L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(118L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(119L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(120L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r -> r.getId().equals(121L)).findFirst()).thenReturn(Optional.empty());
        when(results.stream().filter(r