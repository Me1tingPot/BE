package meltingpot.server.comment.converter;

import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class CommentImageConverter {

    public static List<CommentImage> toCommentImage(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Comment comment) {
        if (createCommentDTO.getImageKeys() == null || createCommentDTO.getImageKeys().isEmpty()) {
            return Collections.emptyList();
        }
        return  createCommentDTO.getImageKeys().stream()
                .map(imageKey -> CommentImage.builder()
                        .imageKey(imageKey)
                        .comment(comment)
                        .account(account)
                        .build())
                .collect(Collectors.toList());
    }
}

