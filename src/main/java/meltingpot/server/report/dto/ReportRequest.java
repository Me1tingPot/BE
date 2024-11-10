package meltingpot.server.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Report;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {
    private String content;

    public Report toEntity(Account account, Post post){
        return Report.builder()
                .content(content)
                .post(post)
                .account(account)
                .build();
    }

    public Report toEntity(Account account, Comment comment){
        return Report.builder()
                .content(content)
                .comment(comment)
                .account(account)
                .build();
    }
}
