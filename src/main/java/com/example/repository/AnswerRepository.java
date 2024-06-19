package com.example.repository;

import com.example.entity.AnswerEntity;
import com.example.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity,Long> {
    List<AnswerEntity>findByQuestion(QuestionEntity question);
}
