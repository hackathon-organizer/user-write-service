package com.hackathonorganizer.userwriteservice.tag.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record TagEditDto(

        @NotNull
        Long id,

        @NotEmpty
        String name
) {

}
