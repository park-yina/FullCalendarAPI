package com.example.controller;

import com.example.dto.AnswerDTO;
import com.example.dto.UserEditDTO;
import com.example.entity.AnswerEntity;
import com.example.entity.PostEntity;
import com.example.entity.QuestionEntity;
import com.example.repository.AnswerRepository;
import com.example.repository.QuestionRepository;
import com.example.service.BoardService;
import com.example.service.QuestionService;
import com.example.service.UserService;
import com.example.util.CommonUtil;
import com.example.util.ImgUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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
public class QnaController {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private final UserService userService;
    private  final CommonUtil commonUtil;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    @Autowired
    private  final QuestionService questionService;
    @PostMapping("/post/qna")
    public String createQuestion(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("photo1") MultipartFile photo1,
            @RequestParam("photo2")MultipartFile photo2,
            @RequestParam("photo3")MultipartFile photo3,
            HttpSession session
    ){

        try {
            Object username = session.getAttribute("username");
            byte[] profileBytes = photo1.isEmpty() ? null : photo1.getBytes();
            byte[] profileBytes2 = photo2.isEmpty() ? null : photo2.getBytes();
            byte[] profileBytes3 = photo3.isEmpty() ? null : photo3.getBytes();

            // 게시물 엔티티 생성 및 필드 설정
            QuestionEntity questionEntity=new QuestionEntity();
            questionEntity.setViews(0L);
            questionEntity.setTitle(title);
            questionEntity.setBoardType("qna");
            questionEntity.setPhoto1(profileBytes);
            questionEntity.setPhoto2(profileBytes2);
            questionEntity.setPhoto3(profileBytes3);
            UserEditDTO author = userService.getUserName(String.valueOf(username));
            questionEntity.setAuthor(author.getNickname());
            questionEntity.setUsername(String.valueOf(username));
            String htmlContent = commonUtil.markdown(content);
            // HTML로 변환된 내용을 다시 postDTO에 설정
            questionEntity.setContent(htmlContent);
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            questionEntity.setCreatedDate(now);
            questionEntity.setUpdatedDate(now);
            // 게시물 저장
            questionRepository.save(questionEntity);

            return "redirect:/";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    @GetMapping("/qna/edit/{postId}")
    public String editPost(@PathVariable("postId") Long postId, Model model, HttpSession session) {
        String username = String.valueOf(session.getAttribute("username"));
        if (username == null) {
            return "redirect:/user/login";
        }
        Optional<QuestionEntity> optionalQuestionEntity = questionRepository.findById(postId);
        if (optionalQuestionEntity.isPresent()) {
            String profileBase64 = null;

            QuestionEntity question = optionalQuestionEntity.get();
            if (!question.getUsername().equals(username)) {
                model.addAttribute("error", "내가 쓴 게시물만 수정 가능합니다.");
                return "error";
            } else {
                model.addAttribute("questionDTO", question);
            }
        } else {
            model.addAttribute("error", "존재하지 않는 질문 게시물입니다.");
            return "error";
        }
        return "modify_question";
    }
    @PostMapping("/qna/update/{postId}")
    public String saveQuestion(@PathVariable("postId") Long postId,
                               @RequestParam("photo1") MultipartFile photo1,
                               @RequestParam("photo2") MultipartFile photo2,
                               @RequestParam("photo3") MultipartFile photo3,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/user/login";
        }

        try {
            byte[] photoBytes = photo1.isEmpty() ? null : photo1.getBytes();
            byte[] photoBytes2 = photo2.isEmpty() ? null : photo2.getBytes();
            byte[] photoBytes3 = photo3.isEmpty() ? null : photo3.getBytes();
            String htmlContent = commonUtil.markdown(content);

            questionService.updateQuestion(postId, title, htmlContent, photoBytes, photoBytes2, photoBytes3);
        } catch (IOException e) {
            logger.error("파일 업로드 중 오류 발생", e);
            return "error";
        }

        return "redirect:/board/qna/" + postId;
    }


    @GetMapping("/qna/{postId}")
    public String viewPostDetail(@PathVariable("postId") Long postId, Model model) {
        Optional<QuestionEntity> postEntityOptional = questionRepository.findById(postId);
        if (postEntityOptional.isPresent()) {
            QuestionEntity questionEntity = postEntityOptional.get();
            String profileBase64 = null;
            String photo2 = null;
            String photo3 = null;

            if (questionEntity.getPhoto1() != null) {
                try {
                    profileBase64 = ImgUtil.resizeImage(questionEntity.getPhoto1(), 100, 100);
                } catch (IOException e) {
                    logger.error("이미지 크기 조정 중 오류 발생", e);
                }
            }
            if (questionEntity.getPhoto2() != null) {
                try {
                    photo2 = ImgUtil.resizeImage(questionEntity.getPhoto2(), 100, 100);
                } catch (IOException e) {
                    logger.error("이미지 크기 조절 중 오류 발생", e);
                }
            }
            if (questionEntity.getPhoto3() != null) {
                try {
                    photo3 = ImgUtil.resizeImage(questionEntity.getPhoto3(), 100, 100);
                } catch (IOException e) {
                    logger.error("이미지 크기 조절 중 오류 발생", e);
                }
            }

            questionEntity.setViews(questionEntity.getViews() + 1);
            questionRepository.save(questionEntity);

            List<AnswerEntity> answer = answerRepository.findByQuestion(questionEntity);
            model.addAttribute("post", questionEntity);
            model.addAttribute("profileBase64", profileBase64);
            model.addAttribute("photo2", photo2);
            model.addAttribute("photo3", photo3);
            model.addAttribute("answer", answer);
            return "detail_question";
        } else {
            model.addAttribute("error", "게시물이 없습니다.");
            return "error"; // 게시물이 없으면 에러 페이지로 리다이렉트
        }
    }

    @GetMapping("/answer/post/{postId}")
    public String postAnswer(HttpSession session, @PathVariable("postId") Long postId, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/user/login";
        } else {
            model.addAttribute("postId", postId);
            model.addAttribute("answerDTO", new AnswerDTO());
            return "post_answer";
        }
    }

    @PostMapping("/answer/post/{postId}")
    public String saveAnswer(@PathVariable("postId") Long postId,
                             @ModelAttribute("answerDTO") AnswerDTO answerDTO,
                             HttpSession session, Model model) {
        String username = String.valueOf(session.getAttribute("username"));
        if (username == null) {
            return "redirect:/user/login";
        }

        Optional<QuestionEntity> questionOptional = questionRepository.findById(postId);
        if (questionOptional.isEmpty()) {
            model.addAttribute("error", "질문 게시물을 찾을 수 없습니다.");
            return "error";
        }

        QuestionEntity question = questionOptional.get();
        UserEditDTO author = userService.getUserName(username);
        AnswerEntity answer = new AnswerEntity();
        answer.setContent(answerDTO.getContent());
        answer.setAuthor(author.getNickname());
        answer.setQuestion(question);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        answer.setCreatedDate(now);
        answer.setUpdatedDate(now);

        answerRepository.save(answer);

        return "redirect:/board/qna";
    }
    @PostMapping("/qna/delete/{postId}")
    public String deleteQuestion(@PathVariable("postId") Long postId){
        questionService.delete(postId);
        return "redirect:/board/qna";
    }
    @GetMapping("/qna/likes/{ansId}")
    public String likeAnswer(@PathVariable("ansId") Long ansId,Model model,HttpSession session){
        String username=String.valueOf(session.getAttribute("username"));
        UserEditDTO author = userService.getUserName(username);
        Optional<AnswerEntity>answerEntityOptional=answerRepository.findById(ansId);
        if(answerEntityOptional.isPresent()){
         AnswerEntity answer= answerEntityOptional.get();
         if(answer.getAuthor().equals(author.getNickname())){
             model.addAttribute("error","자신이 쓴 답변에는 좋아요가 불가합니다.");
             return "error";
         }
         answer.setLikes(answer.getLikes()+1);
         answerRepository.save(answer);
        }
        else{
            model.addAttribute("error","존재하는 답변이 아닙니다");
            return "error";
        }
        return "redirect:/board/qna";
    }
    @GetMapping("/answer/modify/{ansId}")
    public String modifyAnswer(@PathVariable("ansId")Long ansId,Model model,HttpSession session){
        String username=String.valueOf(session.getAttribute("username"));
        if(username==null){
            return "redirect:/user/login";
        }
        Optional<AnswerEntity> answerDTO=answerRepository.findById(ansId);
        model.addAttribute("answerDTO",answerDTO);
        return "modify_answer";
    }
}
