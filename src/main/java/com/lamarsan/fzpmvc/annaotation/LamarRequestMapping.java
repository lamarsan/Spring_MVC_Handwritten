package com.lamarsan.fzpmvc.annaotation;

import java.lang.annotation.*;

/**
 * className: LamarRequestMapping
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:36
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LamarRequestMapping {
    String value() default "";
}
