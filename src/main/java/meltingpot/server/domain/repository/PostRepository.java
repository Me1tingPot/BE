package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

//    @Query("SELECT new meltingpot.server.post.dto.PostResponseDTO.PostsListDTO(" +
//            "p.id, a.name, p.title, p.content, size(p.comments), p.updatedAt) " +
//            "FROM Post p " +
//            "JOIN p.account a " +
//            "WHERE p.postType = :postType AND p.id < :cursor " +
//            "ORDER BY p.id DESC")
Page<Post> findByPostType(@Param("postType") PostType postType, Pageable pageable);
}
