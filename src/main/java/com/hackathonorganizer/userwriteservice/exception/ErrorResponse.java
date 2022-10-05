package com.hackathonorganizer.userwriteservice.exception;

import java.time.LocalDateTime;

record ErrorResponse(
        String httpStatus,
        String message,
        LocalDateTime localDateTime
) {

}
