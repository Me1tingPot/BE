package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.enums.PostType;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.postType = :postType AND p.isDraft = false AND (:cursor IS NULL OR p.id > :cursor) ORDER BY p.id ASC")
    List<Post> findByPostTypeAndCursor(@Param("postType") PostType postType, @Param("cursor") Long cursor, Pageable pageable);

    Slice<Post> findAllByAccountAndDeletedAtIsNullOrderByIdDesc(Account account, Pageable page);

    Slice<Post> findByIdAndDeletedAtIsNull(Long id);

    Optional<Post> findByAccountAndIsDraftTrue(Account account);
}
