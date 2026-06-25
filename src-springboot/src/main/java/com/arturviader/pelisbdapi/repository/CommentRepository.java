package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdAndItemTypeAndIsPublicTrue(Long itemId, MediaType type);

    List<Comment> findByItemIdAndItemType(Long itemId, MediaType type);

     List<Comment> findByUserIdAndItemIdAndItemType(Long userId, Long itemId, MediaType type);

    Page<Comment> findByItemType(MediaType itemType, Pageable pageable);
    Page<Comment> findByCommentTextContainingIgnoreCase(String text, Pageable pageable);
    Page<Comment> findByItemTypeAndCommentTextContainingIgnoreCase(MediaType itemType, String text, Pageable pageable);
}