package meltingpot.server.report.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.service.MailService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Report;
import meltingpot.server.domain.entity.UserReportCount;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.domain.repository.ReportRepository;
import meltingpot.server.domain.repository.UserReportCountRepository;
import meltingpot.server.report.dto.ReportRequest;
import meltingpot.server.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final UserReportCountRepository userReportCountRepository;
    private final MailService mailService;

    public ResponseCode createReport (ReportRequest reportRequest, Account account, Long postId, Long commentId){
        if (postId != null) {
            handlePostReport(reportRequest, postId);
        } else if (commentId != null) {
            handleCommentReport(reportRequest, commentId);
        }
        return ResponseCode.REPORT_CREATE_SUCCESS;
    }

    private void updateUserReportCount(Account account){
        UserReportCount userReportCount = userReportCountRepository.findById(account.getId())
                .orElse(new UserReportCount(account, 0));
        userReportCount.incrementReportCount();
        userReportCountRepository.save(userReportCount);

        if (userReportCount.getReportCount() >= 5) {
            mailService.sendReportAlertEmail(account,userReportCount.getReportCount());
        }

    }
    private void handlePostReport(ReportRequest reportRequest,Long postId){
        Post post = findPostById(postId);
        Report report = reportRequest.toEntity(post.getAccount(),post);
        reportRepository.save(report);

        post.incrementReportCount();
        postRepository.save(post);
        updateUserReportCount(post.getAccount());

        if(post.getReportCount()>=5){
            mailService.sendReportAlertEmail(post.getAccount(),post.getReportCount());
        }
    }

    private void handleCommentReport(ReportRequest reportRequest,Long commentId){
        Comment comment = findCommentById(commentId);
        Report report = reportRequest.toEntity(comment.getAccount(),comment);
        reportRepository.save(report);

        comment.incrementReportCount();
        commentRepository.save(comment);
        updateUserReportCount(comment.getAccount());

        if(comment.getReportCount()>=5){
            mailService.sendReportAlertEmail(comment.getAccount(),comment.getReportCount());
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));
    }
    private Comment findCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()->new RuntimeException("댓글을 찾을 수 없습니다."));
    }


}
