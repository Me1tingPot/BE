package meltingpot.server.report.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Report;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.domain.repository.ReportRepository;
import meltingpot.server.report.dto.ReportRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static meltingpot.server.report.converter.ReportConverter.toReport;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    @Override
    public void createReport (ReportRequestDTO.CreateReportDTO createReportDTO, Account account, Long postId){
        Post post = findPostById(postId);
        Report report = toReport(createReportDTO,account,post);
        reportRepository.save(report);
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));
    }
}
