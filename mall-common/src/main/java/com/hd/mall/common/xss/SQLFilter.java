/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.hd.mall.common.xss;

import com.hd.mall.common.exception.RRException;

/**
 * SQL过滤
 *
 * @author Mark hd@gmail.com
 */
public class SQLFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        //去掉'|"|;|\字符
        str = str.replaceAll("'", "");
        str = str.replaceAll("\"", "");
        str = str.replaceAll(";", "");
        str = str.replaceAll("\\\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.indexOf(keyword) != -1) {
                throw new RRException("包含非法字符");
            }
        }

        return str;
    }

    public static void main(String[] args) {

    }
}
