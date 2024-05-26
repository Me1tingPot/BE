package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
