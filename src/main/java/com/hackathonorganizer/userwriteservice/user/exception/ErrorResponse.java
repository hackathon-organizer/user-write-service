package com.hackathonorganizer.userwriteservice.user.exception;

import java.util.List;

public record ErrorResponse (
    String message,
    List<String> details
) {
}
