package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.Reader;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.ReaderRepository;
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
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void getAll_ReturnsListOfLoans() {
        Loan loan = new Loan();
        when(loanRepository.findAllByOrderByIdDesc()).thenReturn(List.of(loan));

        List<Loan> result = loanService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void create_CreatesLoan_WhenBookAvailable() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(3);

        Reader reader = new Reader();
        reader.setId(1L);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setReader(reader);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = loanService.create(1L, 1L);

        assertNotNull(result);
        assertEquals(2, book.getAvailable());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void create_ThrowsException_WhenBookNotAvailable() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(Exception.class, () -> loanService.create(1L, 1L));
    }

    @Test
    void returnBook_UpdatesLoanAndBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(2);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setReturned(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        Loan result = loanService.returnBook(1L);

        assertTrue(result.getReturned());
        assertEquals(3, book.getAvailable());
    }

    @Test
    void delete_WhenNotReturned_IncreasesAvailable() {
        Book book = new Book();
        book.setId(1L);
        book.setAvailable(1);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setReturned(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        loanService.delete(1L);

        assertEquals(2, book.getAvailable());
        verify(loanRepository, times(1)).deleteById(1L);
    }
}