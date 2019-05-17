package com.lamarsan.fzpmvc.annaotation;

import java.lang.annotation.*;

/**
 * className: LamarRequestParam
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:37
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LamarRequestParam {
    String value() default "";
}
