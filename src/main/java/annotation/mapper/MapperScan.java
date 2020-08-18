package annotation.mapper;

import java.lang.annotation.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-07 11:36
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperScan {
    String[] pkg();
}
