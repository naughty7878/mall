package com.hd.mall.common.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页请求对象
 * @param <T> 查询参数类型
 */
@Data
public class PageRequest<T> {
    private Integer pageNum = 1;          // 当前页码，默认为1
    private Integer pageSize = 10;        // 每页大小，默认为10
    private List<SortItem> sorts = new ArrayList<>(); // 排序字段列表
    private T data;                // 查询参数

    /**
     * 排序项定义
     */
    @Data
    public static class SortItem {
        private String field;             // 排序字段名
        private Direction direction;      // 排序方向

        public enum Direction {
            ASC, DESC
        }

        public SortItem() {}

        public SortItem(String field, Direction direction) {
            this.field = field;
            this.direction = direction;
        }
    }

    /**
     * 添加排序字段
     */
    public PageRequest<T> addSort(String field, SortItem.Direction direction) {
        this.sorts.add(new SortItem(field, direction));
        return this;
    }

    /**
     * 添加升序排序
     */
    public PageRequest<T> addAsc(String field) {
        return addSort(field, SortItem.Direction.ASC);
    }

    /**
     * 添加降序排序
     */
    public PageRequest<T> addDesc(String field) {
        return addSort(field, SortItem.Direction.DESC);
    }

    /**
     * 计算偏移量(用于SQL)
     */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }


}