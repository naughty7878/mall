package com.hd.mall.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.mall.member.entity.Member;
import com.hd.mall.member.mapper.MemberMapper;
import com.hd.mall.member.service.IMemberService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

}
