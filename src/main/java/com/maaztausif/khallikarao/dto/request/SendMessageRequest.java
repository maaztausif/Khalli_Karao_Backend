package com.maaztausif.khallikarao.dto.request;

public record SendMessageRequest(
        long categoryId,
        String body
) {
}
