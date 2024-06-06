package meltingpot.server.report.service;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.report.dto.ReportRequestDTO;

public interface ReportService {

    void createReport (ReportRequestDTO.CreateReportDTO createReportDTO, Account account, Long postId);
}
