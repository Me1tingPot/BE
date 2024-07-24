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

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.id < :cursor ORDER BY c.id DESC")
    List<Comment> findParentComments(Long postId, Long cursor, Pageable pageable);

    // 자식 댓글을 조회하는 메서드
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId")
    List<Comment> findChildrenComments(Long parentId);

    // 부모가 없는 첫 번째 댓글을 찾기 위한 메서드 (가장 상위 댓글)
    Optional<Comment> findFirstByPostIdAndParentIsNull(Long postId);

    // 특정 부모 댓글의 자식 댓글을 페이지로 가져오는 메서드
    List<Comment> findByParent(Comment parent, Pageable pageable);

    // 다음 부모 댓글을 찾기 위한 메서드
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL AND c.id > :parentId ORDER BY c.id ASC")
    Optional<Comment> findNextParentComment(Long parentId, Long postId);

    Slice<Comment> findAllByAccountAndDeletedAtIsNullOrderByIdDesc(Account account, Pageable pageable);
}
