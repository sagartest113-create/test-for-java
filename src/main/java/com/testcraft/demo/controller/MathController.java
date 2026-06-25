package com.testcraft.demo.controller;

import com.testcraft.demo.dto.FizzBuzzResponse;
import com.testcraft.demo.service.MathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints exposing common math utilities.
 * Useful for demonstrating TestCraft AI test generation across boundary,
 * happy-path, and error scenarios.
 */
@RestController
@RequestMapping("/api/math")
public class MathController {

    private final MathService mathService;

    public MathController(MathService mathService) {
        this.mathService = mathService;
    }

    /** GET /api/math/factorial/{n} — returns n! */
    @GetMapping("/factorial/{n}")
    public ResponseEntity<Map<String, Object>> factorial(@PathVariable int n) {
        try {
            long result = mathService.factorial(n);
            return ResponseEntity.ok(Map.of("n", n, "factorial", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/math/prime/{n} — returns whether n is prime */
    @GetMapping("/prime/{n}")
    public ResponseEntity<Map<String, Object>> isPrime(@PathVariable int n) {
        try {
            boolean prime = mathService.isPrime(n);
            return ResponseEntity.ok(Map.of("n", n, "prime", prime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/math/fizzbuzz/{n} — returns FizzBuzz list from 1 to n */
    @GetMapping("/fizzbuzz/{n}")
    public ResponseEntity<?> fizzBuzz(@PathVariable int n) {
        try {
            return ResponseEntity.ok(new FizzBuzzResponse(n, mathService.fizzBuzz(n)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/math/fibonacci/{n} — returns the nth Fibonacci number */
    @GetMapping("/fibonacci/{n}")
    public ResponseEntity<Map<String, Object>> fibonacci(@PathVariable int n) {
        try {
            long result = mathService.fibonacci(n);
            return ResponseEntity.ok(Map.of("n", n, "fibonacci", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/math/gcd?a={a}&b={b} — returns gcd(a, b) */
    @GetMapping("/gcd")
    public ResponseEntity<Map<String, Object>> gcd(
            @RequestParam int a,
            @RequestParam int b) {
        try {
            int result = mathService.gcd(a, b);
            return ResponseEntity.ok(Map.of("a", a, "b", b, "gcd", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
