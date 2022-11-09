package com.hackathonorganizer.userwriteservice.exception;

import java.time.LocalDateTime;
import java.util.List;

record ErrorResponse(
        String message,
        List<String> details
) {

}
