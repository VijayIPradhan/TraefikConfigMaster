package com.traefikconfig.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cors-test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CorsTestController {

    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> simpleTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS test successful");
        response.put("timestamp", System.currentTimeMillis());
        response.put("cors", "enabled");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/preflight")
    public ResponseEntity<Map<String, Object>> preflightTest(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS preflight test successful");
        response.put("received", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}