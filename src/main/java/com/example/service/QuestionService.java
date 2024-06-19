package com.example.service;

import com.example.entity.PostEntity;
import com.example.entity.QuestionEntity;
import com.example.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public void updateQuestion(Long postId, String title, String content, @Nullable byte[] photo1, @Nullable byte[] photo2, @Nullable byte[] photo3) {
        QuestionEntity questionEntity = questionRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 id의 게시물을 찾을 수 없습니다: " + postId));

        questionEntity.setTitle(title);
        questionEntity.setContent(content);
        questionEntity.setUpdatedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정 시각을 한국 시간으로 설정

        if (photo1 != null) {
            questionEntity.setPhoto1(photo1);
        }
        if (photo2 != null) {
            questionEntity.setPhoto2(photo2);
        }
        if (photo3 != null) {
            questionEntity.setPhoto3(photo3);
        }

        questionRepository.save(questionEntity);
    }
    public void delete(Long postId){
        questionRepository.deleteById(postId);
    }
}
