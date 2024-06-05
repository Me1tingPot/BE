package meltingpot.server.report.dto;

import lombok.Getter;

public class ReportRequestDTO {

    @Getter
    public static class CreateReportDTO {
        private String content;
    }
}
