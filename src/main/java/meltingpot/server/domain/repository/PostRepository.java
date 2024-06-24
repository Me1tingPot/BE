package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPostType(@Param("postType") PostType postType, Pageable pageable);

    @EntityGraph(attributePaths = {"account"})
    Slice<Post> findAllByAccountAndDeletedAtIsNull(Account account, Pageable pageable);


}
