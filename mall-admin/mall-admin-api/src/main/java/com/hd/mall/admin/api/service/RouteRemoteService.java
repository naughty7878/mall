
package com.hd.mall.admin.api.service;

import com.hd.mall.admin.api.dto.api.ApiDto;
import com.hd.mall.admin.api.dto.api.ApiReqDto;
import com.hd.mall.admin.api.dto.api.ServerDto;
import com.hd.mall.common.api.ApiRequest;
import com.hd.mall.common.api.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 远程调用接口
 */
public interface RouteRemoteService {

    // 服务列表
    @PostMapping("/route/server/list")
    ApiResponse<List<ServerDto>> listRouteServer();

    // 服务列表
    @PostMapping("/route/api/get")
    ApiResponse<ApiDto> getRouteApi(@RequestBody ApiRequest<ApiReqDto> req);
}