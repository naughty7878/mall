package com.hd.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hd.mall.product.entity.Brand;
import com.hd.mall.product.service.IBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestProductApplication {

    @Autowired
    IBrandService brandService;

    @Test
    public void test01() {
        Brand brandEntity = new Brand();
        brandEntity.setName("小米");
        brandService.save(brandEntity);
    }

    @Test
    public void test02() {
        Brand brandEntity = new Brand();
        brandEntity.setBrandId(1L);
//        brandEntity.setName("小米");
        brandEntity.setDescript("小米描述");
        brandService.updateById(brandEntity);
    }

    @Test
    public void test03() {

        QueryWrapper<Brand> query = new QueryWrapper<Brand>();
        query.eq("brand_id", 1L);
        List<Brand> list = brandService.list(query);
        System.out.println("list = " + list);
    }
}
