package com.vhub.smart.placement.service;

import com.vhub.smart.placement.entity.Resume;
import com.vhub.smart.placement.entity.User;
import com.vhub.smart.placement.repository.ResumeRepository;
import com.vhub.smart.placement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/resumes/";

    public Resume uploadResume(String email, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Create upload directory safely using Files.createDirectories
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF resumes are supported");
        }
        if (file.getSize() > 10 * 1024 * 1024) throw new IllegalArgumentException("Maximum file size is 10 MB");
        try (InputStream input = file.getInputStream()) {
            if (!new String(input.readNBytes(5), java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-"))
                throw new IllegalArgumentException("Invalid PDF file");
        }
        try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(file.getBytes())) {
            if (document.isEncrypted()) throw new IllegalArgumentException("Encrypted PDFs are not supported");
            if (document.getNumberOfPages() == 0) throw new IllegalArgumentException("PDF has no pages");
        }
        originalFileName = originalFileName.replace('\\', '/');
        originalFileName = originalFileName.substring(originalFileName.lastIndexOf('/') + 1);
        String storedFileName = UUID.randomUUID() + ".pdf";
        Path filePath = uploadPath.resolve(storedFileName);

        // Save file locally
        try (InputStream input = file.getInputStream()) { Files.copy(input, filePath); }

        // Save metadata in database
        Resume resume = resumeRepository.findByUser(user).orElse(new Resume());
        String oldPath = resume.getFilePath();
        resume.setUser(user);
        resume.setFileName(originalFileName);
        resume.setStoredFileName(storedFileName);
        resume.setFileType("application/pdf");
        resume.setFileSize(file.getSize());
        resume.setFilePath(filePath.toString());
        resume.setUploadedAt(LocalDateTime.now());

        Resume saved;
        try { saved = resumeRepository.save(resume); }
        catch (RuntimeException ex) { Files.deleteIfExists(filePath); throw ex; }
        if (oldPath != null && !oldPath.equals(filePath.toString())) {
            try { Files.deleteIfExists(Paths.get(oldPath)); } catch (IOException ignored) { /* metadata remains valid */ }
        }
        return saved;
    }

    public Resume getResumeByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return resumeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Resume not found for this user!"));
    }
}