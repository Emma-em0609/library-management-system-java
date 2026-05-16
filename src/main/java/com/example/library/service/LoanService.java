package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.ReaderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       ReaderRepository readerRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    public List<Loan> getAll() {
        return loanRepository.findAllByOrderByIdDesc();
    }

    public Loan create(Long bookId, Long readerId) {
        Book book = bookRepository.findById(bookId).orElseThrow();

        if (book.getAvailable() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Книга недоступна");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setReader(readerRepository.findById(readerId).orElseThrow());

        book.setAvailable(book.getAvailable() - 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public Loan returnBook(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(book.getAvailable() + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public void delete(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow();

        if (!loan.getReturned()) {
            Book book = loan.getBook();
            book.setAvailable(book.getAvailable() + 1);
            bookRepository.save(book);
        }

        loanRepository.deleteById(id);
    }
}