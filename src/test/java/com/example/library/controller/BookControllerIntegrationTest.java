package com.example.library.controller;

import com.example.library.config.JwtService;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

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
        bookRepository.deleteAll();
        adminToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("admin"));
        userToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("user"));
    }

    @Test
    void getAllBooks_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", userToken))
                .andExpect(status().isOk());
    }

    @Test
    void createBook_AsAdmin_ReturnsCreated() throws Exception {
        Book book = new Book();
        book.setTitle("Кобзар");
        book.setAuthor("Шевченко");
        book.setQuantity(5);
        book.setAvailable(5);

        mockMvc.perform(post("/api/books")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Кобзар"))
                .andExpect(jsonPath("$.author").value("Шевченко"));
    }

    @Test
    void createBook_AsUser_ReturnsForbidden() throws Exception {
        Book book = new Book();
        book.setTitle("Кобзар");
        book.setAuthor("Шевченко");
        book.setQuantity(5);

        mockMvc.perform(post("/api/books")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookById_ReturnsOk() throws Exception {
        Book book = new Book();
        book.setTitle("Лісова пісня");
        book.setAuthor("Леся Українка");
        book.setQuantity(3);
        book.setAvailable(3);
        Book saved = bookRepository.save(book);

        mockMvc.perform(get("/api/books/" + saved.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Лісова пісня"));
    }

    @Test
    void deleteBook_AsUser_ReturnsForbidden() throws Exception {
        Book book = new Book();
        book.setTitle("Тестова книга");
        book.setAuthor("Автор");
        book.setQuantity(1);
        book.setAvailable(1);
        Book saved = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + saved.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_AsAdmin_ReturnsOk() throws Exception {
        Book book = new Book();
        book.setTitle("Тестова книга");
        book.setAuthor("Автор");
        book.setQuantity(1);
        book.setAvailable(1);
        Book saved = bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + saved.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }
}