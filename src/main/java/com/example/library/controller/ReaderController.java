package com.example.library.controller;

import com.example.library.model.Reader;
import com.example.library.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin(origins = "*")
@Tag(name = "Читачі", description = "Управління читачами бібліотеки")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    @Operation(summary = "Отримати всіх читачів")
    public List<Reader> getAll() {
        return readerService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати читача за ID")
    public Reader getById(@PathVariable Long id) {
        return readerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Додати нового читача")
    public Reader create(@RequestBody Reader reader) {
        return readerService.save(reader);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити читача")
    public Reader update(@PathVariable Long id, @RequestBody Reader reader) {
        return readerService.update(id, reader);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити читача")
    public String delete(@PathVariable Long id) {
        readerService.delete(id);
        return "{\"message\": \"Читача видалено\"}";
    }
}