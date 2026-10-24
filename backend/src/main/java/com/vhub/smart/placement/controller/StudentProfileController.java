package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.dto.StudentProfileRequest;
import com.vhub.smart.placement.entity.StudentProfile;
import com.vhub.smart.placement.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/profile")
public class StudentProfileController {

    @Autowired
    private StudentProfileService profileService;

    @PostMapping
    public ResponseEntity<?> saveProfile(java.security.Principal principal, @RequestBody StudentProfileRequest request) {
        try {
            StudentProfile profile = profileService.saveOrUpdateProfile(principal.getName(), request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfile(java.security.Principal principal) {
        try {
            StudentProfile profile = profileService.getProfileByEmail(principal.getName());
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}