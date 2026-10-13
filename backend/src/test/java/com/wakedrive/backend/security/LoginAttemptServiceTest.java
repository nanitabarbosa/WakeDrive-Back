package com.wakedrive.backend.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Test
    void blocksAfterFiveFailuresRegardlessOfEmailCase() {
        for (int i = 0; i < 4; i++) {
            service.recordFailure("User@Mail.com");
        }
        assertFalse(service.isBlocked("user@mail.com"));

        service.recordFailure("user@mail.com");
        assertTrue(service.isBlocked("USER@mail.com"));
    }

    @Test
    void successfulLoginResetsCounter() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("a@b.com");
        }
        service.reset("a@b.com");

        assertFalse(service.isBlocked("a@b.com"));
    }

    @Test
    void otherEmailsAreNotAffected() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("a@b.com");
        }

        assertFalse(service.isBlocked("c@d.com"));
    }
}
