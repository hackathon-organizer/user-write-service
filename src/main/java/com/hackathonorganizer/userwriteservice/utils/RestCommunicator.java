package com.hackathonorganizer.userwriteservice.utils;

import com.hackathonorganizer.userwriteservice.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestCommunicator {

    private final RestTemplate restTemplate;

    public boolean checkIfUserIsTeamOwner(Long userId, Long teamId) {

        try {
            return Boolean.TRUE.equals(restTemplate.getForObject("http://localhost:9090/" +
                    "api/v1/read/teams/" + teamId + "/owners?userId=" + userId, Boolean.class));
        } catch (HttpServerErrorException.ServiceUnavailable ex) {

            log.warn("Team service is unavailable. Can't check ownership");

            throw new UserException("Team service is unavailable. Can't check ownership",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}

