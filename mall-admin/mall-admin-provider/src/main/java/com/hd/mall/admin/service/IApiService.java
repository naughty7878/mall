package com.hd.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.admin.entity.Api;

/**
 * <p>
 * 系统API表 服务类
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
public interface IApiService extends IService<Api> {

    // 根据ID-查询API
    Api getRouteApi(Long id);
}
