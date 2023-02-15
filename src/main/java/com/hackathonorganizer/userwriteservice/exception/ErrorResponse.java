package com.hackathonorganizer.userwriteservice.exception;

import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details
) {
}
