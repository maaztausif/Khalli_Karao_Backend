package com.maaztausif.khallikarao.dto.response;

import java.time.Instant;

public record RecentMessageResponse(
        Long id,
        String Category,
        String preview,
        Boolean messageStatus,
        Instant createdAt
) {
}
