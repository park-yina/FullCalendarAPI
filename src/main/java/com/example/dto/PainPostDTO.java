package com.example.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PainPostDTO {
    private String content;
    private String date;
    private String start;
    private String end;
    private boolean pill;
    private boolean pre_pill;
    private String pill_name;
    private boolean disclosure;
    private String author;
    @Max(10)
    private int severity;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Long views;
}
