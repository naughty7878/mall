package com.hd.mall.gateway.api;

/**
 * 枚举了一些常用API操作码
 */
public enum ResultCode implements IErrorCode {
    // ====== 通用错误码 ======
    SYSTEM_BUSY(-1, "系统繁忙，请稍后重试"),
    // ====== 成功状态码 ======
    SUCCESS(0, "成功"),
    FAILED(500, "失败"),

    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
