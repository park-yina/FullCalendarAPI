package com.example.repository;

import com.example.entity.PainPost;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PainRepository extends JpaRepository<PainPost, Long> {
    List<PainPost> findByUserId(Long userId);
    //Optional<PainPost>findById(Long postId);
    List<PainPost> findByDisclosure(Boolean disclosure);
}