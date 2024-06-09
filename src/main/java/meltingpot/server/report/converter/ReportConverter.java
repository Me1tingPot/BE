package meltingpot.server.report.converter;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Report;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.report.dto.ReportRequestDTO;

public class ReportConverter {

    public static Report  toReport (ReportRequestDTO.CreateReportDTO createReportDTO, Account account, Post post){
        return Report.builder()
                .content(createReportDTO.getContent())
                .account(account)
                .post(post)
                .build();
    }
}
