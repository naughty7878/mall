package com.hd.mall.admin.enums.status;

/**
 * 删除状态枚举
 * 对应数据库 deleted_flag 字段
 */
public enum DeletedStatus {
    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),
    
    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final int code;
    private final String description;

    DeletedStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static DeletedStatus fromCode(int code) {
        for (DeletedStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的删除状态码: " + code);
    }
}