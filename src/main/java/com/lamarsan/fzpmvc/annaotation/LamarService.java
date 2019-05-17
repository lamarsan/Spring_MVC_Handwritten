package com.lamarsan.fzpmvc.annaotation;

import java.lang.annotation.*;

/**
 * className: LamarService
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:38
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LamarService {
    String value() default "";
}
