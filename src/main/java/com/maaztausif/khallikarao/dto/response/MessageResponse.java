package com.maaztausif.khallikarao.dto.response;

import com.maaztausif.khallikarao.entity.MessageStatus;

import java.time.Instant;

public record MessageResponse(
        Long id,
        long categoryId,
        String category,
        String recipentEmail,
        String subject,
        String body,
        MessageStatus status,
        Instant createdAt,
        Instant sentAt

) {
}
