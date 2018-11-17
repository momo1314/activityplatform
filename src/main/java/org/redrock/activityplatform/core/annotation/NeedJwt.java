package org.redrock.activityplatform.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by momo on 2018/8/14
 * 用户验证
 */
@Target(ElementType.METHOD) //对方法注解
@Retention(RetentionPolicy.RUNTIME)     // 运行时有效
public @interface NeedJwt {

}
