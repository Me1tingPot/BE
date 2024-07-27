package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(Long id);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND (:parentCursor IS NULL OR c.id > :parentCursor) ORDER BY c.id ASC")
    List<Comment> findParentCommentsByPostId(@Param("postId") Long postId, @Param("parentCursor") Long parentCursor, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND (:cursor IS NULL OR c.id > :cursor) ORDER BY c.id ASC")
    List<Comment> findChildrenCommentsByParentId(@Param("parentId") Long parentId, @Param("cursor") Long cursor);

    Slice<Comment> findAllByAccountAndDeletedAtIsNullOrderByIdDesc(Account account, Pageable pageable);




}
