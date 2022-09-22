package com.hd.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:41:01
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

