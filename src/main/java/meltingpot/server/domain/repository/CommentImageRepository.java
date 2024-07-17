package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {
}
