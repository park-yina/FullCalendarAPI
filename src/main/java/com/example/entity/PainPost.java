package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "pain_boards")
public class PainPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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

    @Column(columnDefinition = "integer default 0")
    private Long views=0L;

}
