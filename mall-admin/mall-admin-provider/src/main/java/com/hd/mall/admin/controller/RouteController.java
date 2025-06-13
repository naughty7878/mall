package com.hd.mall.admin.controller;

import com.hd.mall.admin.api.dto.api.ApiDto;
import com.hd.mall.admin.api.dto.api.ApiReqDto;
import com.hd.mall.admin.api.dto.api.ServerDto;
import com.hd.mall.admin.api.service.RouteRemoteService;
import com.hd.mall.admin.entity.Api;
import com.hd.mall.admin.entity.Server;
import com.hd.mall.admin.service.IApiService;
import com.hd.mall.admin.service.IServerService;
import com.hd.mall.common.api.ApiRequest;
import com.hd.mall.common.api.ApiResponse;
import com.hd.mall.common.util.BeanCopyUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 路由 前端控制器
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Tag(name = "路由管理", description = "路由管理接口")
@RestController
public class RouteController implements RouteRemoteService {

    @Autowired
    private IApiService apiService;
    @Autowired
    private IServerService serverService;

    @Operation(summary = "路由-服务列表")
    @Override
    public ApiResponse<List<ServerDto>> listRouteServer() {
        List<Server> servers = serverService.queryAll();
        return ApiResponse.success(BeanCopyUtils.copyList(servers, ServerDto.class));
    }

    @Operation(summary = "路由-获取接口")
    @Override
    public ApiResponse<ApiDto> getRouteApi(@Validated ApiRequest<ApiReqDto> req) {
        Api api = apiService.queryByCode(req.getData().getCode());
        return ApiResponse.success(BeanCopyUtils.copyObject(api, ApiDto.class));
    }
}
