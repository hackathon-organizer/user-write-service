package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public record EditUserRequestDto(

        String description,

        Set<Tag> tags
) {

}
