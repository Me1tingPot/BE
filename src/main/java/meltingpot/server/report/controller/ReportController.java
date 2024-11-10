package meltingpot.server.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.report.dto.ReportRequest;
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

    @Operation(summary = "게시글 신고 작성")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseData> createPostReport (@CurrentUser Account account, @RequestBody ReportRequest  reportRequest, @PathVariable Long postId) {
        try{
            reportService.createReport(reportRequest, account, postId,null);
            return ResponseData.toResponseEntity (ResponseCode.REPORT_CREATE_SUCCESS);
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity( ResponseCode.REPORT_CREATE_FAIL);
        }
    }

    @Operation(summary = "댓글 신고 작성")
    @PostMapping("/{commentId}")
    public ResponseEntity<ResponseData> createCommentReport(@CurrentUser Account account, @RequestBody ReportRequest  reportRequest, @PathVariable Long commentId) {
        try{
            reportService.createReport(reportRequest, account, null ,commentId);
            return ResponseData.toResponseEntity (ResponseCode.REPORT_CREATE_SUCCESS);
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity( ResponseCode.REPORT_CREATE_FAIL);
        }
    }
}
