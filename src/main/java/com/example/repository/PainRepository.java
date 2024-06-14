package com.example.repository;

import com.example.entity.PainPost;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PainRepository extends JpaRepository<PainPost, Id> {
}
