package com.example.service;

import com.example.dto.PainPostDTO;
import com.example.entity.PainPost;
import com.example.repository.PainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<PainPostDTO> getAllDisclosedPainPosts() {
        List<PainPost> painPosts = painRepository.findByDisclosure(true);
        return painPosts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private PainPostDTO convertToDTO(PainPost painPost) {
        PainPostDTO dto = new PainPostDTO();
        dto.setContent(painPost.getContent());
        dto.setDate(painPost.getDate());
        dto.setStart(painPost.getStart());
        dto.setEnd(painPost.getEnd());
        dto.setPill(painPost.isPill());
        dto.setPre_pill(painPost.isPre_pill());
        dto.setPill_name(painPost.getPill_name());
        dto.setDisclosure(painPost.isDisclosure());
        dto.setAuthor(painPost.getAuthor());
        dto.setSeverity(painPost.getSeverity());
        dto.setViews(painPost.getViews());
        dto.setId(painPost.getId());
        return dto;
    }
}
