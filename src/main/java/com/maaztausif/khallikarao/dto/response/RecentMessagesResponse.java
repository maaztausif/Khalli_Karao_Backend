package com.maaztausif.khallikarao.dto.response;

import java.util.List;

public record RecentMessagesResponse(
        List<RecentMessageResponse> message,
        int pageNo,
        Boolean hasNext
) {
}
