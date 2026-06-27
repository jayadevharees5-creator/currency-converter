package com.example.currency_converter.service;

import com.example.currency_converter.dto.HistoryResponse;
import com.example.currency_converter.model.ConversionHistory;
import com.example.currency_converter.repository.ConversionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing currency conversion history.
 * Persists conversion records and retrieves them per user.
 */
@Service
public class HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryService.class);

    private final ConversionHistoryRepository historyRepository;

    public HistoryService(ConversionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Saves a conversion to the database.
     */
    public void saveHistory(String username, String from, String to, double amount, double convertedAmount) {
        log.debug("Saving conversion history for user: {} ({} {} → {} {})",
                username, amount, from, convertedAmount, to);
        ConversionHistory history = new ConversionHistory(
                username, from, to, amount, convertedAmount, LocalDateTime.now()
        );
        historyRepository.save(history);
        log.info("Conversion history saved for user: {}", username);
    }

    /**
     * Retrieves conversion history for a specific user.
     * Returns DTOs instead of entities — never expose entities in API responses.
     *
     * @param username the authenticated user's username
     * @return list of HistoryResponse DTOs sorted by timestamp (newest first)
     */
    public List<HistoryResponse> getHistoryForUser(String username) {
        log.debug("Fetching conversion history for user: {}", username);
        List<ConversionHistory> entities = historyRepository.findByUsernameOrderByTimestampDesc(username);
        log.info("Found {} history entries for user: {}", entities.size(), username);

        return entities.stream()
                .map(entity -> new HistoryResponse(
                        entity.getId(),
                        entity.getFromCurrency(),
                        entity.getToCurrency(),
                        entity.getAmount(),
                        entity.getConvertedAmount(),
                        entity.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
