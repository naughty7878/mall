package com.hd.mall.coupon.provider.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hd.mall.coupon.provider.entity.HomeSubject;
import com.hd.mall.coupon.provider.mapper.HomeSubjectMapper;
import com.hd.mall.coupon.provider.service.IHomeSubjectService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】 服务实现类
 * </p>
 *
 * @author H__D
 * @since 2025-06-05
 */
@Service
public class HomeSubjectServiceImpl extends ServiceImpl<HomeSubjectMapper, HomeSubject> implements IHomeSubjectService {

}
