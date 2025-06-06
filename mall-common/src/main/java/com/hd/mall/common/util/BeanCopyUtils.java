package com.hd.mall.common.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeanCopyUtils {

    /**
     * 拷贝对象属性（基于Spring BeanUtils）
     * @param source 源对象
     * @param targetClass 目标类Class
     * @return 目标类的新实例
     */
    public static <T> T copyObject(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象属性拷贝失败", e);
        }
    }

    /**
     * 拷贝集合中的对象属性到目标类的新实例集合
     *
     * @param sourceList  源对象集合
     * @param targetClass 目标类的 Class
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标类的新实例集合
     */
    public static <S, T> List<T> copyList(Collection<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || targetClass == null) {
            return null;
        }
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            T target = copyObject(source, targetClass);
            targetList.add(target);
        }
        return targetList;
    }
}
