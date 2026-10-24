package com.vhub.smart.placement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vhub.smart.placement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkflowTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired ObjectMapper json;
    private String auth(String email) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":reviewPass123").getBytes(StandardCharsets.UTF_8));
    }
    private void register(String email) throws Exception {
        mvc.perform(post("/api/auth/register").contentType("application/json")
            .content("{\"name\":\"Reviewer\",\"email\":\"" + email + "\",\"password\":\"reviewPass123\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber());
    }
    @Test void accountsAndOwnership() throws Exception {
        mvc.perform(get("/api/jobs")).andExpect(status().isUnauthorized());
        register("owner@example.test"); register("other@example.test");
        assertTrue(users.findByEmail("owner@example.test").orElseThrow().getPassword().startsWith("$2a$"));
        mvc.perform(post("/api/auth/login").contentType("application/json")
            .content("{\"email\":\"owner@example.test\",\"password\":\"wrong\"}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/admin/jobs").header("Authorization", auth("owner@example.test"))
            .contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/student/profile").param("email", "other@example.test")
            .header("Authorization", auth("owner@example.test")).contentType("application/json")
            .content("{\"college\":\"Owner College\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.user.email").value("owner@example.test"))
            .andExpect(jsonPath("$.user.password").doesNotExist());
        mvc.perform(get("/api/student/profile").header("Authorization", auth("other@example.test")))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/applications/apply").header("Authorization", auth("owner@example.test"))
            .contentType("application/json").content("{\"jobId\":1,\"studentId\":999}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.studentId").value(users.findByEmail("owner@example.test").orElseThrow().getId()));
        mvc.perform(post("/api/applications/apply").header("Authorization", auth("owner@example.test"))
            .contentType("application/json").content("{\"jobId\":1}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/applications/apply").header("Authorization", auth("owner@example.test"))
            .contentType("application/json").content("{\"jobId\":999999}"))
            .andExpect(status().isBadRequest());
    }
    @Test void pdfScanAndUploadValidation() throws Exception {
        register("pdf@example.test");
        mvc.perform(multipart("/api/resumes/upload").file(new MockMultipartFile("file", "bad.pdf", "application/pdf", "fake".getBytes()))
            .header("Authorization", auth("pdf@example.test"))).andExpect(status().isBadRequest());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(); document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText(); content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(20, 700); content.showText("Java Spring Boot React SQL"); content.endText();
            }
            document.save(output);
        }
        mvc.perform(multipart("/api/resumes/upload").file(new MockMultipartFile("file", "../../resume.pdf", "application/pdf", output.toByteArray()))
            .header("Authorization", auth("pdf@example.test")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.storedFileName").value(org.hamcrest.Matchers.matchesPattern("[a-f0-9-]+\\.pdf")))
            .andExpect(jsonPath("$.filePath").doesNotExist()).andExpect(jsonPath("$.user.password").doesNotExist());
        mvc.perform(get("/api/ai/resume-analysis/latest").header("Authorization", auth("pdf@example.test")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.technicalSkills").value("Java,Spring Boot,React,SQL"));
    }
}
