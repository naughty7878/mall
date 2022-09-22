package com.hd.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hd.mall.product.entity.BrandEntity;
import com.hd.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestApplication {

    @Autowired
    BrandService brandService;

    @Test
    public void test01() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("小米");
        brandService.save(brandEntity);
    }

    @Test
    public void test02() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
//        brandEntity.setName("小米");
        brandEntity.setDescript("小米描述");
        brandService.updateById(brandEntity);
    }

    @Test
    public void test03() {

        QueryWrapper<BrandEntity> query = new QueryWrapper<BrandEntity>();
        query.eq("brand_id", 1L);
        List<BrandEntity> list = brandService.list(query);
        System.out.println("list = " + list);
    }
}
