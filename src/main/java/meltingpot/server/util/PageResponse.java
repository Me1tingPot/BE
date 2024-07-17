package meltingpot.server.util;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPage,
        long totalCount
) {
    public static <T> PageResponse<T> of(Page<T> data) {
        return new PageResponse<>(
                data.getContent(),
                data.getPageable().getPageNumber(),
                data.getSize(),
                data.getTotalPages(),
                data.getTotalElements()
        );
    }
}
