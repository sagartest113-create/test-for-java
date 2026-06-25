package com.testcraft.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MathService {

    /**
     * Returns n! for 0 <= n <= 20. Throws for negative or overflow-prone inputs.
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input must be non-negative, got: " + n);
        }
        if (n > 20) {
            throw new IllegalArgumentException("Input must be <= 20 to avoid overflow, got: " + n);
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Returns true if n is prime. 0 and 1 are not prime. Negatives throw.
     */
    public boolean isPrime(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input must be non-negative, got: " + n);
        }
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; (long) i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /**
     * Classic FizzBuzz for 1..n. Returns list of strings.
     */
    public List<String> fizzBuzz(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be >= 1, got: " + n);
        }
        if (n > 10_000) {
            throw new IllegalArgumentException("n must be <= 10000, got: " + n);
        }
        List<String> result = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0)      result.add("FizzBuzz");
            else if (i % 3 == 0)  result.add("Fizz");
            else if (i % 5 == 0)  result.add("Buzz");
            else                   result.add(String.valueOf(i));
        }
        return result;
    }

    /**
     * Returns the nth Fibonacci number (0-indexed). fib(0)=0, fib(1)=1.
     * Supports n up to 85 before long overflow.
     */
    public long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got: " + n);
        }
        if (n > 85) {
            throw new IllegalArgumentException("n must be <= 85 to avoid overflow, got: " + n);
        }
        if (n == 0) return 0;
        if (n == 1) return 1;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    /**
     * Greatest common divisor of two non-negative integers.
     */
    public int gcd(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Inputs must be non-negative");
        }
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }
}
