package com.hd.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hd.mall.common.utils.PageUtils;
import com.hd.mall.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author hd
 * @email hd@gmail.com
 * @date 2022-09-01 19:46:28
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

