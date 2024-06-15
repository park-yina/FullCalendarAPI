package com.example.entity;

import java.time.LocalDateTime;


import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="post")
@EntityListeners(AuditingEntityListener.class)

public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length=100000)
    private String content;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=300000)
    private byte[] photo;
    @Column(columnDefinition = "integer default 0")
    private Long views=0L;
    private String boardType;
    private String author;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;
    private String username;
}