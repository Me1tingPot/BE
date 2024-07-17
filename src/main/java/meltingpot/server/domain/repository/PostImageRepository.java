package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
