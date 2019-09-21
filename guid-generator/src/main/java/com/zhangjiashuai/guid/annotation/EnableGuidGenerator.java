package com.zhangjiashuai.guid.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.zhangjiashuai.guid.config.GuidGeneratorConfiguration;

/**
 *     开启guid生成
 * @author jiash
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(GuidGeneratorConfiguration.class)
@Documented
@Inherited
public @interface EnableGuidGenerator {

}
