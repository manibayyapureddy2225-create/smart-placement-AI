package com.vhub.smart.placement.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(Exception e) { return ResponseEntity.badRequest().body(Map.of("error", "Check required fields, email format and password length (8–72 characters).")); }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    ResponseEntity<?> conflict(Exception e) { return ResponseEntity.status(409).body(Map.of("error", "This record already exists or is invalid.")); }
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    ResponseEntity<?> large(Exception e) { return ResponseEntity.status(413).body(Map.of("error", "Maximum upload size is 10 MB.")); }
}
