package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.entity.Resume;
import com.vhub.smart.placement.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(java.security.Principal principal, @RequestParam("file") MultipartFile file) {
        try {
            Resume resume = resumeService.uploadResume(principal.getName(), file);
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getResume(java.security.Principal principal) {
        try {
            Resume resume = resumeService.getResumeByEmail(principal.getName());
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}