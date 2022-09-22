package com.hd.mall.member.dao;

import com.hd.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:41:01
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
