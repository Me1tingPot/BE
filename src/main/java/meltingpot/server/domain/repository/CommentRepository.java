package meltingpot.server.domain.repository;

import aj.org.objectweb.asm.commons.Remapper;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // 부모 댓글 가져오기 (오래된 순으로 정렬)
    List<Comment> findTopByPostIdAndParentNullOrderByIdAsc(Long postId, Pageable pageable);

    // 커서 이후의 부모 댓글 가져오기 (오래된 순으로 정렬)
    List<Comment> findByPostIdAndParentNullAndIdGreaterThanOrderByIdAsc(Long postId, Long cursor, Pageable pageable);

    // 특정 부모 댓글의 자식 댓글 가져오기
    List<Comment> findByParentId(Long parentId);



    Slice<Comment> findAllByAccountAndDeletedAtIsNullOrderByIdDesc(Account account, Pageable pageable);


}
