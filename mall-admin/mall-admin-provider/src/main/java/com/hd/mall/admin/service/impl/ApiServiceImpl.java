package com.hd.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.mall.admin.entity.Api;
import com.hd.mall.admin.enums.status.DeletedStatus;
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

    @Override
    public Api queryById(Long id) {
        LambdaQueryWrapper<Api> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Api::getId, id)
                .eq(Api::getDeletedFlag, DeletedStatus.NOT_DELETED)
                .last("LIMIT 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Api queryByCode(String code) {
        LambdaQueryWrapper<Api> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Api::getCode, code)
                .eq(Api::getDeletedFlag, DeletedStatus.NOT_DELETED)
                .last("LIMIT 1");
        return baseMapper.selectOne(queryWrapper);
    }
}
