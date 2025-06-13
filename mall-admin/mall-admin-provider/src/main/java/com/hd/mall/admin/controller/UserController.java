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
//@Tag(name = "用户管理", description = "用户管理接口")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/get")
    public ApiResponse<User> getUser(@RequestBody  @Validated ApiRequest<User> req) {
        User user = userService.getById(req.getData().getId());
        return ApiResponse.success(user);
    }

//
//    @GetMapping
//    @Operation(summary = "获取所有用户")
//    public ApiResponse<List<User>> getAllUsers() {
//        return ApiResponse.success(userService.list());
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "根据ID获取用户")
//    public ApiResponse<User> getUserById(
//            @Parameter(description = "用户ID", example = "1") @PathVariable Long id) {
//        User user = userService.getById(id);
//        return ApiResponse.success(user);
//    }
//
//    @PostMapping
//    @Operation(summary = "创建用户")
//    public ApiResponse<User> createUser(@RequestBody UserDTO userDTO) {
//        User user = userService.getById(1L);
//        return ApiResponse.success(user);
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "更新用户")
//    public ApiResponse<User> updateUser(
//            @Parameter(description = "用户ID", example = "1") @PathVariable Long id,
//            @RequestBody UserDTO userDTO) {
//        User user = userService.getById(1L);
//        return ApiResponse.success(user);
//
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "删除用户")
//    public ApiResponse<Void> deleteUser(
//            @Parameter(description = "用户ID", example = "1") @PathVariable Long id) {
//        return ApiResponse.success();
//    }
}
