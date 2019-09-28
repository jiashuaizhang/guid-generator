package com.zhangjiashuai.guid.client.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.zhangjiashuai.guid.client.config.ZooKeeperClientConfiguration;

/**
 *     开启guid生成
 * @author jiash
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ZooKeeperClientConfiguration.class)
@Documented
@Inherited
public @interface EnableGuidGeneratorZooKeeperClient {

}
