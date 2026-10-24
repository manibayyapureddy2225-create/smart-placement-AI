package com.vhub.smart.placement.service;

import com.vhub.smart.placement.entity.ResumeAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumeAnalysisService {
    @Autowired private ResumeService resumes;
    public ResumeAnalysis generateOrFetchAnalysis(String email) throws java.io.IOException {
        var resume = resumes.getResumeByEmail(email);
        String text;
        try (PDDocument document = PDDocument.load(new File(resume.getFilePath()))) {
            text = new PDFTextStripper().getText(document).toLowerCase(Locale.ROOT);
        }
        if (text.isBlank()) throw new IllegalArgumentException("No readable text found. Upload a text-based PDF; OCR is not supported.");
        List<String> vocabulary = List.of("Java", "Spring Boot", "Python", "React", "SQL", "MySQL", "PostgreSQL", "Docker", "AWS", "JavaScript", "Git", "HTML", "CSS");
        List<String> found = vocabulary.stream().filter(skill -> Pattern.compile("(?<![a-z0-9])" + Pattern.quote(skill.toLowerCase(Locale.ROOT)) + "(?![a-z0-9])").matcher(text).find()).toList();
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setSummary("Keyword-based resume scan; not an ATS score or AI assessment.");
        analysis.setTechnicalSkills(String.join(",", found));
        analysis.setRecommendations("Review the detected keywords and compare them with each job description.");
        analysis.setAnalyzedAt(LocalDateTime.now());
        return analysis;
    }
}
