package com.hd.mall.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hd.mall.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 会员 Mapper 接口
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

}
