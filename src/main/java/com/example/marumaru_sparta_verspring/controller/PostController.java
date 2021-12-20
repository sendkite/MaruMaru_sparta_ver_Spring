package com.example.marumaru_sparta_verspring.controller;

import com.example.marumaru_sparta_verspring.domain.articles.Post;
import com.example.marumaru_sparta_verspring.domain.articles.PostComment;
import com.example.marumaru_sparta_verspring.domain.articles.PostLike;
import com.example.marumaru_sparta_verspring.dto.articles.*;
import com.example.marumaru_sparta_verspring.repository.PostRepository;
import com.example.marumaru_sparta_verspring.security.UserDetailsImpl;
import com.example.marumaru_sparta_verspring.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostRepository postrepository;
    private final ModelMapper modelMapper;

    //게시글 목록
    @GetMapping("/post-list")
    public List<PostResponseDto> getPostList(){
        List<PostResponseDto> postList = postService.getPostList();
        PostResponseDto best;
        if(postList.size()>0) {
            best = postList.stream().sorted(Comparator.comparing(PostResponseDto::getView)).collect(Collectors.toList()).get(postList.size() - 1);
            postList.add(0,best);
        }

        return postList;
    }

    //검색 리스트
    @GetMapping("/posts/search")
    public List<PostResponseDto> getSearchList(@RequestParam("category") String category, @RequestParam("keyword") String keyword){
        List<Post> searchPostList = postService.getPostSearchList(category, keyword);

        return searchPostList.stream().map(post -> modelMapper.map(post, PostResponseDto.class)).collect(Collectors.toList());
    }

    
    //조회
    @GetMapping("/posts/detail")
    public PostResponseDto getPostDetail(@RequestParam Long id){
        Post post = postService.getPostDetail(id);

        PostResponseDto postResponseDto =  modelMapper.map(post,PostResponseDto.class);
        return postResponseDto;
    }

    //생성
    @PostMapping("/posts")
    public void createPosController(@Valid @ModelAttribute PostRequestDto postrequestdto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws SQLException, IOException {
        Long userId = userDetails.getUser().getId();
        postService.createPost(postrequestdto,userId);
    }

    //수정
    @PutMapping("/posts/detail")
    public void updatePost(@Valid @RequestPart(value = "key") PostRequestDto postrequestdto, @RequestPart(value = "img",required = false) MultipartFile img) throws IOException{
        postrequestdto.setImg(img);
        postService.updatePost(postrequestdto);

    }

    //수정시 본인확인
    @GetMapping("/posts/check")
    public boolean checkUser(@RequestParam Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return postService.getPostUserCheck(id,userId);

    }

    //삭제
    @DeleteMapping("posts/detail")
    public String delPost(@RequestParam Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return postService.deletePost(id, userId);
    }

    //댓글 생성
    @PostMapping("/posts/comment")
    public List<PostComment> createPostComment(@RequestBody PostCommentRequsetDto postCommentRequsetDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        postService.createComment(postCommentRequsetDto, userId);

        Post post = postService.getPostDetail(postCommentRequsetDto.getPostid());

        return post.getComments();
    }

    //댓글 삭제
    @DeleteMapping("/posts/comment")
    public String delPostComment(@RequestBody PostCommentRequsetDto postCommentRequsetDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return postService.deletePostComment(postCommentRequsetDto.getCommentid(),userId);
    }

    //댓글 수정
    @PutMapping("/posts/comment")
    public String updateComment(@RequestBody PostCommentRequsetDto postCommentRequsetDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return postService.updatePostComment(postCommentRequsetDto,userId);
    }

    //좋아요
    @PostMapping("/posts/like")
    public PostLikeResponseDto updateLike(@RequestBody PostLikeDto postLikeDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();

        Post post = postService.setPostLike(postLikeDto,userId);

        return modelMapper.map(post, PostLikeResponseDto.class);

    }

    //좋아요 유저 확인
    @GetMapping("/posts/like/user")
    public boolean checkUserLike(@RequestParam Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return postService.checkUserLike(id, userId);
    }


}
