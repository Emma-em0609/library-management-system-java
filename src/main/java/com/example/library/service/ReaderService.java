package com.example.library.service;

import com.example.library.model.Reader;
import com.example.library.repository.ReaderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public List<Reader> getAll() {
        return readerRepository.findAll();
    }

    public Reader getById(Long id) {
        return readerRepository.findById(id).orElse(null);
    }

    public Reader save(Reader reader) {
        return readerRepository.save(reader);
    }

    public Reader update(Long id, Reader updated) {
        Reader reader = readerRepository.findById(id).orElseThrow();
        reader.setFullName(updated.getFullName());
        reader.setEmail(updated.getEmail());
        reader.setPhone(updated.getPhone());
        return readerRepository.save(reader);
    }

    public void delete(Long id) {
        readerRepository.deleteById(id);
    }
}