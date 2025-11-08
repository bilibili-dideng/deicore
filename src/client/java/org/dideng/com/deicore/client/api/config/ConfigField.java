package org.dideng.com.deicore.client.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置字段注解，用于标记配置类中的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigField {
    
    /**
     * 配置项名称（可选，默认为字段名）
     */
    String name() default "";
    
    /**
     * 配置项描述
     */
    String description() default "";
    
    /**
     * 配置项分类
     */
    String category() default "general";
    
    /**
     * 是否在GUI中显示
     */
    boolean visible() default true;
    
    /**
     * 最小值（适用于数值类型）
     */
    double min() default Double.MIN_VALUE;
    
    /**
     * 最大值（适用于数值类型）
     */
    double max() default Double.MAX_VALUE;
    
    /**
     * 配置项是否可修改
     */
    boolean editable() default true;
}