package com.example.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionDTO {

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

    private Long views ;

    private String boardType;
    private String author;
    @Column(length=300)
    private String title;
    @Column(length=100000)

    private String content;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;


}
