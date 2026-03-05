package com.testcraft.demo.controller;

import com.testcraft.demo.dto.EchoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/echo")
public class EchoController {

    @PostMapping
    public ResponseEntity<Map<String, String>> echo(@Valid @RequestBody EchoRequest request) {
        return ResponseEntity.ok(Map.of("output", request.input()));
    }
}
