package com.lamarsan.fzpmvc.annaotation;

import java.lang.annotation.*;

/**
 * className: LamarAutowired
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 18:51
 */
//定义注解使用范围
@Target({ElementType.FIELD})
//运行时可以通过反射机制获取相应的注解
@Retention(RetentionPolicy.RUNTIME)
//javadoc
@Documented
public @interface LamarAutowired {
    String value() default "";
}
