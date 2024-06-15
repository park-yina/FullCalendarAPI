package com.example.service;


import com.example.entity.PostEntity;
import com.example.repository.PostRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;

@Service
public class BoardService {

    private final PostRepository postRepository;

    public BoardService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostEntity createPostForABoard(String title, String content, String author, byte[] photo,String boardType) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setContent(content);
        postEntity.setPhoto(photo);
        postEntity.setAuthor(author);
        postEntity.setBoardType(boardType);
        return postRepository.save(postEntity);
    }
    public String findAuthorById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id의 게시물을 찾을 수 없습니다: " + id));
        return post.getAuthor();
    }
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
    public void updatePost(Long postId, String title, String content, @Nullable byte[] photo) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 id의 게시물을 찾을 수 없습니다: " + postId));

        postEntity.setTitle(title);
        postEntity.setContent(content);
        postEntity.setUpdatedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul"))); // 수정 시각을 한국 시간으로 설정
        if (photo != null) {
            postEntity.setPhoto(photo);
        }
        postRepository.save(postEntity);
    }


}
