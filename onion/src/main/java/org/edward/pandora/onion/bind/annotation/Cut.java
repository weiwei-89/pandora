package org.edward.pandora.onion.bind.annotation;

import org.edward.pandora.onion.bind.model.DefaultConvertor;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cut {
    /**
     * 标签（如果未指定，则默认为属性名称）
     * @return
     */
    String tag() default "";

    /**
     * 是否可用
     * @return
     */
    boolean available() default true;

    /**
     * 是否忽略空字符串
     * @return
     */
    boolean ignoreEmptyString() default true;

    /**
     * 是否转换
     * @return
     */
    boolean convert() default false;

    /**
     * 转换定义
     * @return
     */
    Class<? extends Enum<?>> convertDefinition() default DefaultConvertor.class;

    /**
     * 转换的key
     * @return
     */
    String convertKey() default "";

    /**
     * 转换的value
     * @return
     */
    String convertValue() default "";
}