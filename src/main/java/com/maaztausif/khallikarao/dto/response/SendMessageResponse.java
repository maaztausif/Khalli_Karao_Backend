package com.maaztausif.khallikarao.dto.response;

public record SendMessageResponse(
        Boolean status,
        String message,
        MessageResponse data
) {
}
