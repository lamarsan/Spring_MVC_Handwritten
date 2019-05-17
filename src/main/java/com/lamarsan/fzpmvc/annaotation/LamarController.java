package com.lamarsan.fzpmvc.annaotation;

import java.lang.annotation.*;

/**
 * className: LamarController
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 18:59
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LamarController {
    String value() default "";
}
