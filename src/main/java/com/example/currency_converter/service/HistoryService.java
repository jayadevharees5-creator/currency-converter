package com.example.currency_converter.service;

import com.example.currency_converter.model.ConversionHistory;
import com.example.currency_converter.repository.ConversionHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    private final ConversionHistoryRepository historyRepository;

    public HistoryService(ConversionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(String username, String from, String to, double amount, double convertedAmount) {
        ConversionHistory history = new ConversionHistory(username, from, to, amount, convertedAmount, LocalDateTime.now());
        historyRepository.save(history);
    }

    public List<ConversionHistory> getHistoryForUser(String username) {
        return historyRepository.findByUsernameOrderByTimestampDesc(username);
    }
}
