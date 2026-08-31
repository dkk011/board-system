package com.example.board_service.service;

import com.example.board_service.client.UserClient;
import com.example.board_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientWrapper {

    private final UserClient userClient;

    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserFallback")
    public UserDto getUser(Long userId) {
        return userClient.getUser(userId);
    }

    public UserDto getUserFallback(Long userId, Exception e) {

        log.warn("user-service 호출 실패! Fallback 실행. userId={}, error={}", userId, e.getMessage());

        return new UserDto(userId, "알 수 없는 사용자", null, null);
    }
}