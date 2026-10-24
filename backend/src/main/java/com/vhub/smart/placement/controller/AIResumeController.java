package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.entity.ResumeAnalysis;
import com.vhub.smart.placement.service.ResumeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/resume-analysis")
public class AIResumeController {

    @Autowired
    private ResumeAnalysisService analysisService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAnalysis(java.security.Principal principal) {
        try {
            ResumeAnalysis analysis = analysisService.generateOrFetchAnalysis(principal.getName());
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}