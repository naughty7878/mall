package com.hd.mall;

import com.hd.mall.admin.entity.User;
import com.hd.mall.admin.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAdminApplication {

    @Autowired
    IUserService userService;

    @Test
    public void test01() {
        User user = new User();
        user.setId(1L);
        user.setPassword("1234567");
        userService.updateById(user);
    }



}
