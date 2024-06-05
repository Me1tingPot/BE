package meltingpot.server.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.report.dto.ReportRequestDTO;
import meltingpot.server.report.service.ReportService;
import meltingpot.server.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "신고 작성")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseData> createPost(@CurrentUser Account account, @RequestBody ReportRequestDTO.CreateReportDTO createReportDTO, @PathVariable Long postId) {
        try{
            reportService.createReport(createReportDTO, account, postId);
            return ResponseData.toResponseEntity (ResponseCode.REPORT_CREATE_SUCCESS);
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity( ResponseCode.REPORT_CREATE_FAIL);
        }
    }
}
