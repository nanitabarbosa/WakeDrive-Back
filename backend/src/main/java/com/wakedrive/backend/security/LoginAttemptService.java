package com.wakedrive.backend.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    private static final int MAX_TRACKED_EMAILS = 10_000;

    private record Attempts(int count, Instant lastFailure) {
        boolean expired() {
            return lastFailure.plus(LOCK_DURATION).isBefore(Instant.now());
        }
    }

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String email) {
        Attempts current = attempts.get(key(email));
        if (current == null) {
            return false;
        }
        if (current.expired()) {
            attempts.remove(key(email));
            return false;
        }
        return current.count() >= MAX_ATTEMPTS;
    }

    public void recordFailure(String email) {
        if (attempts.size() > MAX_TRACKED_EMAILS) {
            attempts.values().removeIf(Attempts::expired);
        }
        attempts.merge(key(email), new Attempts(1, Instant.now()),
                (old, fresh) -> old.expired() ? fresh : new Attempts(old.count() + 1, fresh.lastFailure()));
    }

    public void reset(String email) {
        attempts.remove(key(email));
    }

    private String key(String email) {
        return email.trim().toLowerCase();
    }
}
