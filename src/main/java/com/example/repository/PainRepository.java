package com.example.repository;

import com.example.entity.PainPost;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PainRepository extends JpaRepository<PainPost, Long> {
    List<PainPost> findByUserId(Long userId);
    //Optional<PainPost>findById(Long postId)
    //
    // ;
    @Query("SELECT p FROM PainPost p WHERE p.disclosure= :disclosure ORDER BY p.date ASC")
    List<PainPost> findByDisclosure(Boolean disclosure);

    @Query("SELECT p FROM PainPost p WHERE p.user.id = :userId ORDER BY p.date ASC")
    List<PainPost> findByUserIdOrderByDateAsc(@Param("userId") Long userId);

}