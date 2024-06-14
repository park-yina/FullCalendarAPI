package com.example.dto;

import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPainPostDTO {
    private String content;
    private String date;
    private String start;
    private String end;
    private boolean pill;
    private boolean pre_pill;
    private String pill_name;
    private boolean disclosure;
    @Max(10)
    private int severity;
}
