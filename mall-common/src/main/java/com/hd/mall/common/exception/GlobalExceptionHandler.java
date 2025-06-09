package com.hd.mall.common.exception;

import com.hd.mall.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器（同时处理Controller层和Service层异常）
 * 优化点：
 * 1. 合并重复的校验异常处理逻辑
 * 2. 增加详细日志记录
 * 3. 支持从请求中获取更多上下文信息
 * 4. 提供国际化消息的扩展点
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务自定义异常
     */
    @ExceptionHandler(ApiException.class)
    public ApiResponse<Void> handleApiException(ApiException e, HttpServletRequest request) {
        log.error("API异常 [url: {}]: errorCode={}, message={}",
                request.getRequestURL(), e.getErrorCode(), e.getMessage(), e);
        return e.getErrorCode() != null ?
                ApiResponse.failed(e.getErrorCode()) :
                ApiResponse.failed(e.getMessage());
    }

    /**
     * 统一处理参数校验异常（Spring Validation）
     * 覆盖以下异常：
     * 1. @RequestBody + @Valid 触发的 MethodArgumentNotValidException
     * 2. @ModelAttribute + @Valid 触发的 BindException
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidationException(Exception ex) {
        BindingResult bindingResult = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex).getBindingResult() :
                ((BindException) ex).getBindingResult();

        String errorMessage = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError ->
                        String.format("%s: %s",
                                fieldError.getField(),
                                // 此处可替换为国际化消息（通过MessageSource）
                                fieldError.getDefaultMessage()))
                .collect(Collectors.joining(" | "));

        log.warn("参数校验失败: {}", errorMessage);
        return ApiResponse.validateFailed(errorMessage);
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("全局异常 [url: {}]: ", request.getRequestURL(), e);
        return ApiResponse.failed("系统繁忙，请稍后重试");
    }
}