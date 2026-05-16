package com.example.library.controller;

import com.example.library.config.JwtService;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.Reader;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

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
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
        adminToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("admin"));
        userToken = "Bearer " + jwtService.generateToken(userDetailsService.loadUserByUsername("user"));
    }

    @Test
    void getAllLoans_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/loans")
                        .header("Authorization", userToken))
                .andExpect(status().isOk());
    }

    @Test
    void createLoan_AsAdmin_ReturnsCreated() throws Exception {
        Book book = new Book();
        book.setTitle("Кобзар");
        book.setAuthor("Шевченко");
        book.setQuantity(5);
        book.setAvailable(5);
        Book savedBook = bookRepository.save(book);

        Reader reader = new Reader();
        reader.setFullName("Іван Франко");
        Reader savedReader = readerRepository.save(reader);

        Map<String, Long> body = Map.of(
                "book_id", savedBook.getId(),
                "reader_id", savedReader.getId()
        );

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void createLoan_AsUser_ReturnsForbidden() throws Exception {
        Map<String, Long> body = Map.of("book_id", 1L, "reader_id", 1L);

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnBook_AsAdmin_ReturnsOk() throws Exception {
        Book book = new Book();
        book.setTitle("Лісова пісня");
        book.setAuthor("Леся Українка");
        book.setQuantity(3);
        book.setAvailable(2);
        Book savedBook = bookRepository.save(book);

        Reader reader = new Reader();
        reader.setFullName("Тарас Шевченко");
        Reader savedReader = readerRepository.save(reader);

        Loan loan = new Loan();
        loan.setBook(savedBook);
        loan.setReader(savedReader);
        loan.setReturned(false);
        Loan savedLoan = loanRepository.save(loan);

        mockMvc.perform(put("/api/loans/" + savedLoan.getId() + "/return")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void createLoan_WhenBookNotAvailable_ReturnsBadRequest() throws Exception {
        Book book = new Book();
        book.setTitle("Недоступна книга");
        book.setAuthor("Автор");
        book.setQuantity(1);
        book.setAvailable(0);
        Book savedBook = bookRepository.save(book);

        Reader reader = new Reader();
        reader.setFullName("Читач");
        Reader savedReader = readerRepository.save(reader);

        Map<String, Long> body = Map.of(
                "book_id", savedBook.getId(),
                "reader_id", savedReader.getId()
        );

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}