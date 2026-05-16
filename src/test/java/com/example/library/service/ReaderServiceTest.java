package com.example.library.service;

import com.example.library.model.Reader;
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
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private ReaderService readerService;

    @Test
    void getAll_ReturnsListOfReaders() {
        Reader reader = new Reader();
        reader.setFullName("Іван Франко");
        when(readerRepository.findAll()).thenReturn(List.of(reader));

        List<Reader> result = readerService.getAll();

        assertEquals(1, result.size());
        assertEquals("Іван Франко", result.get(0).getFullName());
    }

    @Test
    void getById_ReturnsReader() {
        Reader reader = new Reader();
        reader.setId(1L);
        reader.setFullName("Іван Франко");
        when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));

        Reader result = readerService.getById(1L);

        assertNotNull(result);
        assertEquals("Іван Франко", result.getFullName());
    }

    @Test
    void getById_ReturnsNull_WhenNotFound() {
        when(readerRepository.findById(99L)).thenReturn(Optional.empty());

        Reader result = readerService.getById(99L);

        assertNull(result);
    }

    @Test
    void save_SavesReader() {
        Reader reader = new Reader();
        reader.setFullName("Леся Українка");
        when(readerRepository.save(reader)).thenReturn(reader);

        Reader result = readerService.save(reader);

        assertNotNull(result);
        assertEquals("Леся Українка", result.getFullName());
    }

    @Test
    void delete_CallsRepository() {
        readerService.delete(1L);
        verify(readerRepository, times(1)).deleteById(1L);
    }
}