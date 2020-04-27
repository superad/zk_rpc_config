package com.mengma.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fgm
 * @description RPC服务注解
 * @date 2020-04-25
 ***/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCService {

    /**
     * 服务注册接口
     * @return
     */
    Class<?> value();

    /**
     * 服务版本号
     * @return
     */
    String version() default "1.0.0";

}
