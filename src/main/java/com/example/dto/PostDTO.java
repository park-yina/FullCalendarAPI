package com.example.dto;

import com.example.entity.PostEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {
    private String title;
    private String content;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=300000)
    private byte[] photo;

    @Column(nullable = false)
    private String author;

    private String boardType;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Long views;
    @Column(nullable=false)
    private String username;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

}