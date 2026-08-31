package com.example.board_service.service;

import com.example.board_service.client.UserClient;
import com.example.board_service.domain.Post;
import com.example.board_service.dto.PostRequestDto;
import com.example.board_service.dto.PostResponseDto;
import com.example.board_service.dto.UserDto;
import com.example.board_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserClient userClient;

    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto request) {
        // Post 생성
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(request.getUserId())
                .build();

        Post savedPost = postRepository.save(post);

        UserDto user = userClient.getUser(savedPost.getUserId());

        return PostResponseDto.from(savedPost, user);
    }

    // 게시글 목록 조회
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(post -> {
                    UserDto user = userClient.getUser(post.getUserId());
                    return PostResponseDto.from(post, user);
                })
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        UserDto user = userClient.getUser(post.getUserId());

        return PostResponseDto.from(post, user);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(request.getTitle(), request.getContent());

        UserDto user = userClient.getUser(post.getUserId());

        return PostResponseDto.from(post, user);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        postRepository.delete(post);
    }
}