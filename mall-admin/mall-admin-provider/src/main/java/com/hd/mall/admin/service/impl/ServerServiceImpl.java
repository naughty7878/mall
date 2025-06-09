package com.hd.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.mall.admin.entity.Server;
import com.hd.mall.admin.enums.status.DeletedStatus;
import com.hd.mall.admin.mapper.ServerMapper;
import com.hd.mall.admin.service.IServerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统服务表 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements IServerService {

    @Override
    public List<Server> queryAll() {
        LambdaQueryWrapper<Server> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Server::getDeletedFlag, DeletedStatus.NOT_DELETED)
                .orderBy(true, false, Server::getId);
        return baseMapper.selectList(queryWrapper);
    }
}
