package com.example.library.controller;

import com.example.library.model.Loan;
import com.example.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
@Tag(name = "Видача книг", description = "Управління видачею книг читачам")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @Operation(summary = "Отримати всі видачі")
    public List<Loan> getAll() {
        return loanService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Видати книгу читачу")
    public Loan create(@RequestBody Map<String, Long> body) {
        return loanService.create(body.get("book_id"), body.get("reader_id"));
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Повернути книгу")
    public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити запис про видачу")
    public String delete(@PathVariable Long id) {
        loanService.delete(id);
        return "{\"message\": \"Видачу видалено\"}";
    }
}