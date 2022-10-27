package com.hackathonorganizer.userwriteservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestCommunicator {

    private final RestTemplate restTemplate;

    public boolean checkIfUserIsTeamOwner(Long userId, Long teamId) {

       boolean isOwner = restTemplate.getForObject("http://localhost:9090/api" +
                "/v1/read/teams" +
                "/" + teamId + "/owners?userId=" + userId, Boolean.class);

       return isOwner;
    }
}
