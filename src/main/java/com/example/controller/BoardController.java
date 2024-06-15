package com.example.controller;

import com.example.dto.PostDTO;
import com.example.dto.UserEditDTO;
import com.example.entity.PostEntity;
import com.example.repository.PostRepository;
import com.example.service.BoardService;
import com.example.service.UserService;
import com.example.util.CommonUtil;
import com.example.util.ImgUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final BoardService boardService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final CommonUtil commonUtil;

    @GetMapping("")
    public String board(HttpSession session) {
        Object username=session.getAttribute("username");
        if(username!=null) {
            return "board";
        }else {
            return "redirect:/user/login";
        }

    }

    @GetMapping("/{boardType}")
    public String getBoardPosts(@PathVariable("boardType") String boardType, Model model) {
        model.addAttribute("boardType", boardType);

        List<PostEntity> posts = postRepository.findByBoardType(boardType);
        model.addAttribute("posts", posts);


        return "detail_board";
    }


    @GetMapping("/post/{boardType}")
    public String showPost(@PathVariable("boardType") String boardType, Model model, HttpSession session) {
        Object username=session.getAttribute("username");
        if(username==null) {
            return "redirect:/user/login";
        }
        else {
            model.addAttribute("boardType", boardType);

            model.addAttribute("postDTO", new PostDTO());
//
//        }
            if(boardType.equals("notice")){
                if(username.equals("kln99988@naver.com")){
                    return "new_post";
                }
                else{
                    model.addAttribute("error","공지 게시판은 운영자만 사용가능합니다.");
                    return "error";
                }
            }

            return "new_post";
        }
    }


    @PostMapping("/post/{boardType}")
    public String createPostForABoard(@PathVariable("boardType") String boardType,
                                      @RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam("photo") MultipartFile photo
            ,
                                      HttpSession session
    ) {
        try {
            Object username = session.getAttribute("username");
            byte[] profileBytes = photo.isEmpty() ? null : photo.getBytes();

            // 게시물 엔티티 생성 및 필드 설정
            PostEntity postEntity = new PostEntity();
            postEntity.setTitle(title);
            postEntity.setContent(content);
            postEntity.setPhoto(profileBytes);
            UserEditDTO author = userService.getUserName(String.valueOf(username));
            postEntity.setAuthor(author.getNickname());
            postEntity.setBoardType(boardType);
            postEntity.setUsername(String.valueOf(username));
            String htmlContent = commonUtil.markdown(postEntity.getContent());
            // HTML로 변환된 내용을 다시 postDTO에 설정
            postEntity.setContent(htmlContent);
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            postEntity.setCreatedDate(now);
            postEntity.setUpdatedDate(now);
            // 게시물 저장
            postRepository.save(postEntity);

            return "redirect:/";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    // findById를 사용한 코드
    @GetMapping("/{boardType}/{postId}")
    public String viewPostDetail(@PathVariable("boardType") String boardType,
                                 @PathVariable("postId") Long postId, Model model) {
        Optional<PostEntity> postEntityOptional = postRepository.findById(postId);
        if (postEntityOptional.isPresent()) {
            PostEntity postEntity = postEntityOptional.get();
            String profileBase64 = null;
            if (postEntity.getPhoto() != null) {
                try {
                    profileBase64 = ImgUtil.resizeImage(postEntity.getPhoto(), 100, 100); // 원하는 크기로 조정합니다.
                } catch (IOException e) {
                    logger.error("이미지 크기 조정 중 오류 발생", e);
                }
            }
            postEntity.setViews(postEntity.getViews()+1);
            postRepository.save(postEntity);
            model.addAttribute("post", postEntity);
            model.addAttribute("profileBase64", profileBase64);

            return "post_detail"; // 게시물 상세 페이지의 뷰 이름을 리턴
        } else {
            // 게시물이 존재하지 않을 경우에 대한 처리
            logger.info("게시물이 존재하지 않습니다.");
            return "redirect:/error"; // 또는 다른 적절한 처리 수행
        }
    }
    @GetMapping("/edit/{postId}")
    public String getEdit(@PathVariable("postId") Long postId, Model model, HttpSession session) {
        Optional<PostEntity> postEntityOptional = postRepository.findById(postId);
        if (postEntityOptional.isPresent()) {
            String author = boardService.findAuthorById(postId);
            String author2 = userService.getUserName(String.valueOf(session.getAttribute("username"))).getNickname();
            PostEntity postEntity = postEntityOptional.get();
            if (author.equalsIgnoreCase(author2)) {
                model.addAttribute("post", postEntity);
                String profileBase64 = null;
                if (postEntity.getPhoto() != null) {
                    try {
                        profileBase64 = ImgUtil.resizeImage(postEntity.getPhoto(), 100, 100); // 원하는 크기로 조정합니다.
                    } catch (IOException e) {
                        logger.error("이미지 크기 조정 중 오류 발생", e);
                    }
                }
                model.addAttribute("profileBase64", profileBase64);
                return "modify_post";
            } else {
                return "redirect:/error";
            }
        } else {
            return "redirect:/board";
        }
    }
    @PostMapping("/edit/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("photo") MultipartFile photo,
                             HttpSession session,
                             Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/user/login";
        }

        String author = boardService.findAuthorById(postId);
        String author2 = userService.getUserName(String.valueOf(session.getAttribute("username"))).getNickname();
        if (!author.equalsIgnoreCase(author2)) {
            model.addAttribute("error", "자신이 작성한 글이 아닙니다.");
            return "error";
        }

        try {
            byte[] photoBytes = photo.isEmpty() ? null : photo.getBytes();
            String htmlContent = commonUtil.markdown(content);
            // HTML로 변환된 내용을 다시 postDTO에 설정
            content=htmlContent;
            boardService.updatePost(postId, title, content, photoBytes);
        } catch (IOException e) {
            logger.error("파일 업로드 중 오류 발생", e);
        }

        return "redirect:/board";
    }


    @PostMapping("/delete/{postId}")
    public String deletePost (@PathVariable("postId") Long postId,HttpSession session,Model model) {
        if(session.getAttribute("username")==null) {
            return "redirect:/user/login";
        }
        else {
            String Author=boardService.findAuthorById(postId);
            String Author2=userService.getUserName(String.valueOf(session.getAttribute("username"))).getNickname();
            if(Author.equalsIgnoreCase(Author2)) {
                boardService.deletePostById(postId);
            }
            else {
                model.addAttribute("error", "자신이 작성한 글이 아닙니다.");
                return "error";
            }
        }
        return "redirect:/board";

    }

}