package server.annotation;

import java.lang.annotation.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-25 13:58
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenSSL {
}
