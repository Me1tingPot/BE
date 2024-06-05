package meltingpot.server.comment.converter;

import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class CommentImageConverter {

    public static CommentImage toCommentImage(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Comment comment) {
        if (createCommentDTO.getImageKey() == null || createCommentDTO.getImageKey().isEmpty()) {
            return null;
        }

        return  CommentImage.builder()
                .imageKey(createCommentDTO.getImageKey())
                .comment(comment)
                .account(account)
                .build();
    }
}

