package com.testcraft.demo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class MathServiceTest {

    @InjectMocks
    private MathService mathService;

    @Test
    @DisplayName("Factorial of 0 is 1")
    void factorial_zero() {
        long result = mathService.factorial(0);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Factorial of 1 is 1")
    void factorial_one() {
        long result = mathService.factorial(1);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Factorial of 2 is 2")
    void factorial_two() {
        long result = mathService.factorial(2);
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("Factorial of 20 is correct")
    void factorial_twenty() {
        long result = mathService.factorial(20);
        assertThat(result).isEqualTo(2432902008176640000L);
    }

    @Test
    @DisplayName("Factorial of negative number throws")
    void factorial_negative() {
        assertThatThrownBy(() -> mathService.factorial(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must be non-negative, got: -1");
    }

    @Test
    @DisplayName("Factorial of number greater than 20 throws")
    void factorial_too_large() {
        assertThatThrownBy(() -> mathService.factorial(21))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must be <= 20 to avoid overflow, got: 21");
    }

    @Test
    @DisplayName("Is prime for 2")
    void is_prime_two() {
        boolean result = mathService.isPrime(2);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Is prime for 3")
    void is_prime_three() {
        boolean result = mathService.isPrime(3);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Is prime for 4")
    void is_prime_four() {
        boolean result = mathService.isPrime(4);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Is prime for 5")
    void is_prime_five() {
        boolean result = mathService.isPrime(5);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Is prime for 0")
    void is_prime_zero() {
        boolean result = mathService.isPrime(0);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Is prime for 1")
    void is_prime_one() {
        boolean result = mathService.isPrime(1);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Is prime for negative number throws")
    void is_prime_negative() {
        assertThatThrownBy(() -> mathService.isPrime(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must be non-negative, got: -1");
    }

    @Test
    @DisplayName("FizzBuzz for 1")
    void fizz_buzz_one() {
        List<String> result = mathService.fizzBuzz(1);
        assertThat(result).containsExactly("1");
    }

    @Test
    @DisplayName("FizzBuzz for 3")
    void fizz_buzz_three() {
        List<String> result = mathService.fizzBuzz(3);
        assertThat(result).containsExactly("1", "2", "Fizz");
    }

    @Test
    @DisplayName("FizzBuzz for 5")
    void fizz_buzz_five() {
        List<String> result = mathService.fizzBuzz(5);
        assertThat(result).containsExactly("1", "2", "Fizz", "4", "Buzz");
    }

    @Test
    @DisplayName("FizzBuzz for 15")
    void fizz_buzz_fifteen() {
        List<String> result = mathService.fizzBuzz(15);
        assertThat(result).containsExactly("1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz", "11", "Fizz", "13", "14", "FizzBuzz");
    }

    @Test
    @DisplayName("FizzBuzz for negative number throws")
    void fizz_buzz_negative() {
        assertThatThrownBy(() -> mathService.fizzBuzz(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("n must be >= 1, got: -1");
    }

    @Test
    @DisplayName("FizzBuzz for number greater than 10000 throws")
    void fizz_buzz_too_large() {
        assertThatThrownBy(() -> mathService.fizzBuzz(10001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("n must be <= 10000, got: 10001");
    }

    @Test
    @DisplayName("Fibonacci of 0")
    void fibonacci_zero() {
        long result = mathService.fibonacci(0);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Fibonacci of 1")
    void fibonacci_one() {
        long result = mathService.fibonacci(1);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Fibonacci of 2")
    void fibonacci_two() {
        long result = mathService.fibonacci(2);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Fibonacci of 3")
    void fibonacci_three() {
        long result = mathService.fibonacci(3);
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("Fibonacci of 85")
    void fibonacci_eighty_five() {
        long result = mathService.fibonacci(85);
        assertThat(result).isEqualTo(203928013748997662L);
    }

    @Test
    @DisplayName("Fibonacci of negative number throws")
    void fibonacci_negative() {
        assertThatThrownBy(() -> mathService.fibonacci(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("n must be non-negative, got: -1");
    }

    @Test
    @DisplayName("Fibonacci of number greater than 85 throws")
    void fibonacci_too_large() {
        assertThatThrownBy(() -> mathService.fibonacci(86))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("n must be <= 85 to avoid overflow, got: 86");
    }

    @Test
    @DisplayName("GCD of 12 and 15")
    void gcd_twelve_fifteen() {
        int result = mathService.gcd(12, 15);
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("GCD of 24 and 30")
    void gcd_twenty_four_thirty() {
        int result = mathService.gcd(24, 30);
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("GCD of 48 and 18")
    void gcd_forty_eight_eighteen() {
        int result = mathService.gcd(48, 18);
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("GCD of negative number throws")
    void gcd_negative() {
        assertThatThrownBy(() -> mathService.gcd(-1, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Inputs must be non-negative");
    }
}