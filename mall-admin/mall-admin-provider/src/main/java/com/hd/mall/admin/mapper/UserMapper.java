package com.hd.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.mall.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author H__D
 * @since 2025-06-09
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
