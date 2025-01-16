package com.mv.ams.web.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long numberOfElements;
    private int totalPages;
}
