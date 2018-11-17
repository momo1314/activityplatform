package org.redrock.activityplatform.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by momo on 2018/5/1
 */
@Configuration
@ComponentScan(
        basePackages = {"org.redrock.activityplatform"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)
        }
)
@Import(value = {
        MybatisConfig.class
})
public class RootConfig {
}
