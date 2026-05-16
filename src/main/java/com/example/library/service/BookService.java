package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book save(Book book) {
        if (book.getAvailable() == null) {
            book.setAvailable(book.getQuantity());
        }
        return bookRepository.save(book);
    }

    public Book update(Long id, Book updated) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setIsbn(updated.getIsbn());
        book.setYear(updated.getYear());
        book.setQuantity(updated.getQuantity());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}