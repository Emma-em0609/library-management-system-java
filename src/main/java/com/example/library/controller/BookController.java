package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@Tag(name = "Книги", description = "Управління книгами бібліотеки")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Отримати всі книги")
    public List<Book> getAll() {
        return bookService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати книгу за ID")
    public Book getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Додати нову книгу")
    public Book create(@RequestBody Book book) {
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити книгу")
    public Book update(@PathVariable Long id, @RequestBody Book book) {
        return bookService.update(id, book);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити книгу")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "{\"message\": \"Книгу видалено\"}";
    }
}