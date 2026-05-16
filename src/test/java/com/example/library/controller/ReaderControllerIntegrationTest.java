package com.example.library.controller;

import com.example.library.config.JwtService;
import com.example.library.model.Reader;
import com.example.library.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReaderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        readerRepository.deleteAll();
        adminToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("admin"));
        userToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("user"));
    }

    @Test
    void getAllReaders_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/readers")
                        .header("Authorization", userToken))
                .andExpect(status().isOk());
    }

    @Test
    void createReader_AsAdmin_ReturnsCreated() throws Exception {
        Reader reader = new Reader();
        reader.setFullName("Іван Франко");
        reader.setEmail("ivan@example.com");
        reader.setPhone("0991234567");

        mockMvc.perform(post("/api/readers")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reader)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Іван Франко"));
    }

    @Test
    void createReader_AsUser_ReturnsForbidden() throws Exception {
        Reader reader = new Reader();
        reader.setFullName("Леся Українка");

        mockMvc.perform(post("/api/readers")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reader)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getReaderById_ReturnsOk() throws Exception {
        Reader reader = new Reader();
        reader.setFullName("Тарас Шевченко");
        Reader saved = readerRepository.save(reader);

        mockMvc.perform(get("/api/readers/" + saved.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Тарас Шевченко"));
    }

    @Test
    void deleteReader_AsUser_ReturnsForbidden() throws Exception {
        Reader reader = new Reader();
        reader.setFullName("Тестовий читач");
        Reader saved = readerRepository.save(reader);

        mockMvc.perform(delete("/api/readers/" + saved.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteReader_AsAdmin_ReturnsOk() throws Exception {
        Reader reader = new Reader();
        reader.setFullName("Тестовий читач");
        Reader saved = readerRepository.save(reader);

        mockMvc.perform(delete("/api/readers/" + saved.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }
}