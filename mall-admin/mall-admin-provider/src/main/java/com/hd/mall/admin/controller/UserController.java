package com.hd.mall.admin.controller;

import com.hd.mall.admin.entity.User;
import com.hd.mall.admin.service.IUserService;
import com.hd.mall.common.api.ApiRequest;
import com.hd.mall.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统用户表 前端控制器
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/get")
    public ApiResponse<User> getUser(@RequestBody  @Validated ApiRequest<User> req) {
        User user = userService.getById(req.getData().getId());
        return ApiResponse.success(user);
    }
}
