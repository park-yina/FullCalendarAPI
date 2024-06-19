package com.example.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=300000)
    @Nullable
    private byte[] photo1;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=300000)
    @Nullable
    private byte[] photo2;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=300000)
    @Nullable
    private byte[] photo3;

    @Column(columnDefinition = "integer default 0")
    private Long views = 0L;

    private String boardType;
    private String author;
    @Column(length = 300)
    private String title;

    @Column(length = 100000)
    private String content;

    private String username;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;


    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answers = new ArrayList<>();
}
