package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAll_ReturnsListOfBooks() {
        Book book = new Book();
        book.setTitle("Кобзар");
        book.setAuthor("Шевченко");
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.getAll();

        assertEquals(1, result.size());
        assertEquals("Кобзар", result.get(0).getTitle());
    }

    @Test
    void getById_ReturnsBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Кобзар");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getById(1L);

        assertNotNull(result);
        assertEquals("Кобзар", result.getTitle());
    }

    @Test
    void getById_ReturnsNull_WhenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Book result = bookService.getById(99L);

        assertNull(result);
    }

    @Test
    void save_SetsAvailableEqualsQuantity_WhenAvailableIsNull() {
        Book book = new Book();
        book.setQuantity(5);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.save(book);

        assertEquals(5, result.getAvailable());
    }

    @Test
    void delete_CallsRepository() {
        bookService.delete(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
}