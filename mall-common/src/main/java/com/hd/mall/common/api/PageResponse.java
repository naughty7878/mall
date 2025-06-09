package com.hd.mall.common.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页响应对象
 * @param <T> 数据记录类型
 */
@Data
public class PageResponse<T> {
    private Integer pageNum;           // 当前页码
    private Integer pageSize;          // 每页大小
    private List<T> data;           // 数据记录列表
    private Long total;                // 总记录数
    private Integer totalPages;        // 总页数

    /**
     * 空分页响应
     */
    public static <T> PageResponse<T> empty() {
        PageResponse<T> response = new PageResponse<>();
        response.setData(new ArrayList<>());
        response.setTotal(0L);
        response.setTotalPages(0);
        return response;
    }

//    /**
//     * 从Spring Page转换
//     */
//    public static <T, R> PageResponse<R> fromPage(org.springframework.data.domain.Page<T> page, List<R> records) {
//        PageResponse<R> response = new PageResponse<>();
//        response.setPageNum(page.getNumber() + 1);
//        response.setPageSize(page.getSize());
//        response.setRecords(records);
//        response.setTotal(page.getTotalElements());
//        response.setTotalPages(page.getTotalPages());
//        return response;
//    }
}