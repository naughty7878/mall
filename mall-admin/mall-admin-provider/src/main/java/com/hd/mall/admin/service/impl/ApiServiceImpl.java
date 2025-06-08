package com.hd.mall.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.mall.admin.entity.Api;
import com.hd.mall.admin.mapper.ApiMapper;
import com.hd.mall.admin.service.IApiService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统API表 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Service
public class ApiServiceImpl extends ServiceImpl<ApiMapper, Api> implements IApiService {

}
