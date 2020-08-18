package annotation.db;

import java.lang.annotation.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-18 15:50
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSourceSwitch {

    String value() default "dataSource";

}
