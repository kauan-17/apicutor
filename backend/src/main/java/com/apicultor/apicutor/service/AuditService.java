package com.apicultor.apicutor.service;

import com.apicultor.apicutor.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final ConcurrentHashMap<String, LocalDateTime> loginAttempts = new ConcurrentHashMap<>();

    public void logLoginSuccess(Usuario usuario) {
        logger.info("LOGIN_SUCCESS: username={}, id={}, timestamp={}",
                usuario.getUsername(), usuario.getId(), LocalDateTime.now());
        loginAttempts.remove(usuario.getUsername());
    }

    public void logLoginFailure(String username, String reason) {
        logger.warn("LOGIN_FAILURE: username={}, reason={}, timestamp={}",
                username, reason, LocalDateTime.now());
        loginAttempts.put(username, LocalDateTime.now());
    }

    public boolean isBruteForceAttempt(String username) {
        LocalDateTime lastAttempt = loginAttempts.get(username);
        if (lastAttempt == null) return false;
        return lastAttempt.isAfter(LocalDateTime.now().minusMinutes(5));
    }
}