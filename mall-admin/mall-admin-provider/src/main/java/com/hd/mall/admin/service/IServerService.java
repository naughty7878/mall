package com.hd.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.admin.entity.Server;

import java.util.List;

/**
 * <p>
 * 系统服务表 服务类
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
public interface IServerService extends IService<Server> {

    // 查询所有可用的服务
    List<Server> queryAll();
}
