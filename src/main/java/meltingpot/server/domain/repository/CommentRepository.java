package meltingpot.server.domain.repository;

import aj.org.objectweb.asm.commons.Remapper;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Optional<Comment> findById(Long id);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.id < :cursor ORDER BY c.id DESC")
    List<Comment> findParentCommentsByPostIdAndCursor(@Param("postId") Long postId, @Param("cursor") Long cursor, Pageable pageable);

        @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId ORDER BY c.id ASC")
        List<Comment> findChildCommentsByParentId(@Param("parentId") Long parentId, Pageable pageable);


    Slice<Comment> findAllByAccountAndDeletedAtIsNullOrderByIdDesc(Account account, Pageable pageable);
}
