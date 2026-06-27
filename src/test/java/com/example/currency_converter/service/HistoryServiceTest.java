package com.example.currency_converter.service;

import com.example.currency_converter.dto.HistoryResponse;
import com.example.currency_converter.model.ConversionHistory;
import com.example.currency_converter.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private ConversionHistoryRepository repository;

    @InjectMocks
    private HistoryService historyService;

    @Test
    void saveHistory_Success() {
        historyService.saveHistory("testuser", "USD", "EUR", 100.0, 92.0);
        verify(repository, times(1)).save(any(ConversionHistory.class));
    }

    @Test
    void getHistoryForUser_ReturnsDtoList() {
        ConversionHistory mockEntity = new ConversionHistory("testuser", "USD", "EUR", 100.0, 92.0,
                LocalDateTime.now());
        mockEntity.setId(1L);

        when(repository.findByUsernameOrderByTimestampDesc("testuser"))
                .thenReturn(List.of(mockEntity));

        List<HistoryResponse> results = historyService.getHistoryForUser("testuser");

        assertEquals(1, results.size());
        assertEquals("USD", results.get(0).getFromCurrency());
        assertEquals("EUR", results.get(0).getToCurrency());
        assertEquals(100.0, results.get(0).getAmount());
    }
}
