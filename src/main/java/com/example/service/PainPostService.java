package com.example.service;

import com.example.entity.PainPost;
import com.example.repository.PainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class PainPostService {
    @Autowired
    private PainRepository painRepository;
    public Optional<PainPost> checkPainPost(Long postId) {
        return painRepository.findById(postId);
    }
    public String calculateDuration(PainPost painPost) {
        if ("종일".equals(painPost.getEnd())) {
            return "종일";
        } else {
            LocalTime startTime = LocalTime.parse(painPost.getStart());
            LocalTime endTime = LocalTime.parse(painPost.getEnd());
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            return hours + "시간 " + minutes + "분";
        }
    }
}
