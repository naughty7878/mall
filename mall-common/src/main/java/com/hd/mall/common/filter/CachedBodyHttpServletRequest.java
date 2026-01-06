package com.hd.mall.common.filter;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 自定义 HttpServletRequest 包装器 - Java 8 兼容版
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    
    private final byte[] cachedBody;
    private final String cachedBodyString;
    
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        
        // 使用 Hutool 的 IoUtil 读取字节（Java 8 兼容）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoUtil.copy(request.getInputStream(), baos);
        this.cachedBody = baos.toByteArray();
        
        if (cachedBody.length > 0) {
            this.cachedBodyString = new String(cachedBody, getCharacterEncoding());
        } else {
            this.cachedBodyString = "";
        }
    }
    
    /**
     * 获取缓存的请求体内容
     */
    public String getCachedBody() {
        return cachedBodyString;
    }
    
    /**
     * 获取缓存的字节数组
     */
    public byte[] getCachedBodyBytes() {
        return cachedBody;
    }
    
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setReadListener(ReadListener readListener) {
                // Java 8 兼容，空实现
            }
            
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }
    
    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream, 
            StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }
}