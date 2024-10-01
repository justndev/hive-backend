package com.bezkoder.springjwt.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    public Map<String, String> getUserSessions() {
        return userSessions;
    }
}
